package Weapon.Tasks;






import AwakenSystem.AwakenSystem;
import AwakenSystem.data.DamageMath;
import AwakenSystem.data.baseAPI;
import AwakenSystem.data.defaultAPI;
import Weapon.Items.ItemFile;
import Weapon.weapon;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.Task;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class runTask extends Task{
    @Override
    public void onRun(int i) {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            player.getSkin();
            String weaponName = weapon.getObject().file.getWeaponName(player.getInventory().getItemInHand());
            if (weaponName != null) {
                File file = new File(weapon.getObject().getDataFolder() + "/Weapons/" + weaponName + ".yml");
                if (!file.exists()) {
                    player.getInventory().removeItem(player.getInventory().getItemInHand());
                    player.sendMessage("抱歉,此武器已经被服主移除");
                } else {
                    int addHealth = weapon.getObject().getWeaponConfig(weaponName).getInt("addHealth");
                    int itemAddHealth = 0;
                    for (String name : weapon.getObject().file.getWeaponItems(player.getInventory().getItemInHand())) {
                        itemAddHealth += ItemFile.getObject().getHealth(name);
                    }
                    if (!weapon.getObject().health.containsKey(player.getName())) {
                        weapon.getObject().health.put(player.getName(), player.getMaxHealth());
                        if (Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null) {
                            defaultAPI.addPlayerAttributeInt(player.getName(), baseAPI.PlayerAttType.HEALTH,(addHealth + itemAddHealth));
                            weapon.getObject().addhealth.put(player.getName(),(addHealth + itemAddHealth));
                            player.attack(0.1F);
                        } else {
                            player.setMaxHealth(player.getMaxHealth() + addHealth + itemAddHealth);
                        }
                    }

                }
            } else {
                if (weapon.getObject().health.containsKey(player.getName())) {
                    if (Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") == null) {
                        player.setMaxHealth(weapon.getObject().health.get(player.getName()));
                        weapon.getObject().health.remove(player.getName());
                    }else{
                        if(weapon.getObject().addhealth.containsKey(player.getName())){
                            int h = weapon.getObject().addhealth.get(player.getName());
                            defaultAPI.removePlayerAttributeInt(player.getName(), baseAPI.PlayerAttType.HEALTH,h);
                            player.attack(0.1F);
                            weapon.getObject().addhealth.remove(player.getName());
                            weapon.getObject().health.remove(player.getName());
                        }
                    }
                }

            }
        }
    }
}
