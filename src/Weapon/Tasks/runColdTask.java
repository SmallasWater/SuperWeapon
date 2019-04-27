
package Weapon.Tasks;

import Weapon.weapon;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;

import java.util.HashMap;

public class runColdTask extends Task{
    @Override
    public void onRun(int i) {
        for (Player player: Server.getInstance().getOnlinePlayers().values()) {
            if(weapon.getObject().bei.containsKey(player.getName())){
                HashMap<String, Integer> m = weapon.getObject().bei.get(player.getName());
                weapon.getObject().bei.put(player.getName(),this.runDelTime(m));
            }
            if(weapon.getObject().cold.containsKey(player.getName())){
                HashMap<String, Integer> map = weapon.getObject().cold.get(player.getName());
                weapon.getObject().cold.put(player.getName(), this.runDelTime(map));
            }
        }
    }
    private HashMap<String,Integer> runDelTime(HashMap<String,Integer> map){
        if(map != null){
            for(String skill:map.keySet()){
                int time = map.get(skill);
                if(--time < 0){
                    map.remove(skill);
                }else{
                    map.put(skill,time);
                }
            }
        }
        return map;
    }
}
