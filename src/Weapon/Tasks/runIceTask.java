package Weapon.Tasks;


import Weapon.weapon;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;

public class runIceTask extends Task{
    @Override
    public void onRun(int i) {
        for (Player player: Server.getInstance().getOnlinePlayers().values()){
            if(weapon.getObject().move.containsKey(player.getName())){
                int time = weapon.getObject().move.get(player.getName());
                if(--time <= 0){
                    weapon.getObject().move.remove(player.getName());
                }else{
                    weapon.getObject().move.put(player.getName(),time);
                }
            }
        }
    }
}
