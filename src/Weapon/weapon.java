package Weapon;


import Weapon.Commands.clickItemCommand;
import Weapon.Commands.weCommand;
import Weapon.Items.ItemFile;
import Weapon.Tasks.*;
import Weapon.events.playerEvents;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Config;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class weapon extends PluginBase{


    public HashMap<String,Integer> health    = new HashMap<>(),
                                   addhealth = new HashMap<>(),
                                   move      = new HashMap<>() ;

    public HashMap<String,HashMap<String,Integer>> cold = new HashMap<>(),
                                                   bei  = new HashMap<>();//技能冷却

    private static weapon object;

    public weaponFile file;

    public static weapon getObject() {
        return object;
    }

    public Config getWeaponConfig(String weapon){
        return new Config(this.getDataFolder()+"/Weapons/"+weapon+".yml",Config.YAML);
    }



    public Config getItemConfig(String itemName){
        return new Config(this.getDataFolder()+"/Items/"+itemName+".yml",Config.YAML);
    }

    public weaponFile getWeaponFile(){
        return weaponFile.getAPI();
    }



    @Override
    public void onEnable() {
        object = this;
        this.file = new weaponFile();
        new ItemFile();
        this.getServer().getPluginManager().registerEvents(new playerEvents(),this);
        this.getServer().getCommandMap().register("",new weCommand());
        this.getServer().getCommandMap().register("",new clickItemCommand());
        if(this.getConfig().getString("add-Item-money") == null){
            Config conf = this.getConfig();
            conf.setAll(this.initConfig());
            conf.save();
        }
        this.getServer().getScheduler().scheduleDelayedTask(this,()->
                this.getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {
                    public void onRun() {
                        Server.getInstance().getScheduler().scheduleRepeatingTask(new runTask(),20);
                        Server.getInstance().getScheduler().scheduleRepeatingTask(new runColdTask(),20);
                        Server.getInstance().getScheduler().scheduleRepeatingTask(new runIceTask(),20);
                        Server.getInstance().getScheduler().scheduleRepeatingTask(new runWeaponHandTask(),20);
                    }
                }), 60);
    }

    private LinkedHashMap<String,Object> initConfig(){
        LinkedHashMap<String,Object> file = new LinkedHashMap<>();
        file.put("add-Item-money",1000);
        return file;
    }

}
