package Weapon.events;


import Weapon.Effects;
import Weapon.Items.ItemFile;
import Weapon.weapon;
import Weapon.weaponFile;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Hash;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;


public class playerEvents implements Listener{


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event){
        if(event instanceof EntityDamageByEntityEvent){
            Entity entity = event.getEntity();
            Entity Damage = ((EntityDamageByEntityEvent) event).getDamager();
            if(Damage instanceof Player){
                Item item = ((Player) Damage).getInventory().getItemInHand();
                if(item.hasCompoundTag()){
                    String weaponName = weapon.getObject().file.getWeaponName(item);
                    if(weaponName != null){
                        HashMap mp = weaponFile.getAPI().getItemAttributeAll(
                                weaponFile.getAPI().getWeaponItems(((Player) Damage).getInventory().getItemInHand()));
                        entity.level.addParticle(new DestroyBlockParticle(new Vector3(entity.x, entity.y, entity.z),
                                Block.get(152,0)));
                        Config weaponConfig = weapon.getObject().getWeaponConfig(weaponName);
                        int damage = weaponConfig.getInt("damage");

                        for(String i :weaponFile.getAPI().getWeaponItems(item)){
                            damage += ItemFile.getObject().getDamage(i);
                        }
                        //设置攻击伤害
                        event.setDamage((float)damage+event.getDamage());

                        //初始化判断
                        if(!weapon.getObject().cold.containsKey(Damage.getName())){
                            weapon.getObject().cold.put(Damage.getName(),new HashMap<>());
                        }
                        HashMap<String, Integer> map= weapon.getObject().cold.get(Damage.getName());
                        double kick = 0;
                        try{
                            if(mp.containsKey("kick")){
                                if(Integer.parseInt(String.valueOf(mp.get("kick") == null?"0":mp.get("kick"))) != 0){
                                    kick += Double.parseDouble(String.valueOf("kick"));
                                }
                            }
                        }catch (Exception e){
                            kick = 0.5;
                        }
                        if(entity instanceof Player) {
                            if(mp.containsKey("Ice")){
                                if (weapon.getObject().cold.containsKey(Damage.getName())) {
                                    if (!map.containsKey("Ice")) {
                                        String[] i = String.valueOf(mp.get("Ice")).split(":");
                                        if (Integer.parseInt(i[0]) != 0) {
                                            map.put("Ice", Integer.parseInt(i[1]));
                                            ((Player) Damage).sendTip(TextFormat.AQUA + "冰冻触发 持续" + i[0] + "冷却 " + i[1] + " 秒");
                                            Effects.addIce(entity.getPosition());
                                            weapon.getObject().move.put(entity.getName(), Integer.parseInt(i[0]));
                                        }
                                    }
                                }
                                double deltaY = entity.x - Damage.x;
                                double deltaZ = entity.z - Damage.z;
                                double yaw = Math.atan2(deltaY, deltaZ);
                                ((Player) entity).knockBack(Damage,0,Math.sin(yaw),Math.cos(yaw), kick);
                            }
                        }else{
                            ((EntityDamageByEntityEvent) event).setKnockBack((float) kick);
                        }
                        if(mp.containsKey("addHealth")){
                            if(!map.containsKey("addHealth")){
                                String[] i= String.valueOf(mp.get("addHealth")).split(":");
                                if(Integer.parseInt(i[0]) != 0){
                                    map.put("addHealth",Integer.parseInt(i[1]));
                                    int add = (int)event.getDamage()*(Integer.parseInt(i[0])/100);
                                    ((Player) Damage).sendTip(TextFormat.AQUA+"吸血触发 冷却 "+i[1]+" 秒");
                                    Damage.setHealth(Damage.getHealth()+add);
                                    Effects.addHealth(Damage.getPosition());
                                }
                            }
                        }
                        if(mp.containsKey("fire")){
                            if(!map.containsKey("fire")){
                                String[] i= String.valueOf(mp.get("fire")).split(":");
                                if(Integer.parseInt(i[0]) != 0){
                                    map.put("fire",Integer.parseInt(i[1]));
                                    ((Player) Damage).sendTip(TextFormat.AQUA+"引燃触发 冷却 "+i[1]+" 秒");
                                    entity.setOnFire(Integer.parseInt(i[1]));
                                }
                            }
                        }

                        weapon.getObject().cold.put(Damage.getName(),map);
                        //药水
                        if(mp.containsKey("addEffect")){
                            HashMap effects = (HashMap)mp.get("addEffect");
                            for (Object key:effects.keySet()){
                                String[] value = String.valueOf(effects.get(key)).split(":");
                                int id = Integer.parseInt(String.valueOf(key));
                                if(weapon.getObject().cold.containsKey(Damage.getName())){
                                    HashMap<String,Integer> c = weapon.getObject().cold.get(Damage.getName());
                                    if(!c.containsKey(String.valueOf(id))){
                                        Effect eff = Effect.getEffect(id);
                                        eff.setDuration(Integer.parseInt(value[1])*20);
                                        eff.setAmplifier(Integer.parseInt(value[0]));
                                        //id:level:时间:冷却
                                        entity.addEffect(eff);
                                        c.put(String.valueOf(id),Integer.parseInt(value[2]));
                                        ((Player) Damage).sendTip(TextFormat.AQUA+"[主动] "+
                                                weaponFile.getEffectStringById(id)+" 触发 "
                                                +weaponFile.getLevelByString(Integer.parseInt(value[0]))
                                                +" 冷却 "+value[2]+" 秒");
                                        weapon.getObject().cold.put(Damage.getName(),c);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(entity instanceof Player){
                Item item = ((Player)entity).getInventory().getItemInHand();
                if(item.hasCompoundTag()){
                    String weaponName = weapon.getObject().file.getWeaponName(item);
                    if(weaponName != null){
                        HashMap<String,Object> mp = weaponFile.getAPI().getItemAttributeAll(
                                weaponFile.getAPI().getWeaponItems(((Player) entity).getInventory().getItemInHand()));
                        if(mp.containsKey("DelDamage")){
                            String[] i= String.valueOf(mp.get("DelDamage")).split(":");
                            if(Integer.parseInt(i[0]) != 0){
                                if(!weapon.getObject().bei.containsKey(entity.getName())){
                                    weapon.getObject().bei.put(entity.getName(),new HashMap<>());
                                }
                                HashMap<String,Integer> map = weapon.getObject().bei.get(entity.getName());

                                String[] del = String.valueOf(mp.get("DelDamage")).split(":");
                                if(weapon.getObject().bei.containsKey(entity.getName())){
                                    if(!map.containsKey("DelDamage")){
                                        Effects.addRelief(entity.getPosition());
                                        if( Float.parseFloat(del[0])!= 0){
                                            float delDamage = event.getDamage()*((Float.parseFloat(del[0])/100) > 1?1:(Float.parseFloat(del[0])/100));
                                            float damage = event.getDamage() - delDamage;
                                            event.setDamage(damage);
                                            map.put("DelDamage",Integer.parseInt(del[1]));
                                        }
                                    }
                                }
                                weapon.getObject().bei.put(entity.getName(),map);
                            }
                        }
                    }
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        /*if(!weapon.getObject().health.containsKey(player.getName())){
            weapon.getObject().health.put(player.getName(),player.getMaxHealth());
        }*/
        if(!weapon.getObject().cold.containsKey(player.getName())){
            weapon.getObject().cold.put(player.getName(),new HashMap<>());
        }
        if(!weapon.getObject().bei.containsKey(player.getName())){
            weapon.getObject().bei.put(player.getName(),new HashMap<>());
        }

    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void killPlayer(PlayerDeathEvent event){
        Player player = event.getEntity();
        weapon.getObject().move.remove(player.getName());
        EntityDamageEvent cause = event.getEntity().getLastDamageCause();
        if(cause instanceof EntityDamageByEntityEvent){
            Entity killer = ((EntityDamageByEntityEvent) cause).getDamager();
            if(killer instanceof Player){
                String weaponName = weaponFile.getAPI().getWeaponName(((Player) killer).getInventory().getItemInHand());
                if(weaponName != null){
                    String killMessage = weapon.getObject().getWeaponConfig(weaponName).getString("killMessage");
                    event.setDeathMessage("");
                    Server.getInstance().broadcastMessage(killMessage.replace("{killer}",killer.getName()).replace("{player}",player.getName()));
                }
            }
        }
    }




    @EventHandler(ignoreCancelled = true,priority = EventPriority.NORMAL)
    public void playerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(weapon.getObject().move.containsKey(player.getName())){
            event.setCancelled();
            player.sendTip(TextFormat.AQUA+"你被冰冻了 "+weapon.getObject().move.get(player.getName())+" 秒内不能移动");
        }
    }

}
