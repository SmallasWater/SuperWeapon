package Weapon.Tasks;


import Weapon.weapon;
import Weapon.weaponFile;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class runWeaponHandTask extends Task{
//    private HashMap<String,ArrayList<Integer>> timer = new HashMap<>();
//    private String listWeapon = null;
    @Override
    public void onRun(int i) {
        for (Player player: Server.getInstance().getOnlinePlayers().values()) {
            Item item = player.getInventory().getItemInHand();
            HashMap<String,Object> mp = weaponFile.getAPI().getItemAttributeAll(weaponFile.getAPI().getWeaponItems(item));
            String weaponName = weaponFile.getAPI().getWeaponName(item);
            if(weaponName != null) {
                if(mp.containsKey("effect")){
                    HashMap effect = (HashMap) mp.get("effect");
                    if (effect != null) {
                        for (Object key : effect.keySet()) {
                            String id = String.valueOf(key);
                            String[] effects = String.valueOf(effect.get(key)).split(":");
                            if (weapon.getObject().bei.containsKey(player.getName())) {
                                HashMap<String, Integer> b = weapon.getObject().bei.get(player.getName());
                                if (!b.containsKey(id)) {
                                    Effect eff = Effect.getEffect(Integer.parseInt(id)).setDuration(Integer.parseInt(effects[1]) * 20)
                                            .setAmplifier(Integer.parseInt(effects[0]));
                                    player.addEffect(eff);
                                    b.put(id, Integer.parseInt(effects[2]));
                                    player.sendTip("§c[被动] 触发 " + weaponFile.getEffectStringById(Integer.parseInt(id)) + "持续 " + effects[1] + "秒");
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}
