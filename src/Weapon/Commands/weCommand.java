package Weapon.Commands;


import Weapon.Items.ItemFile;
import Weapon.weapon;
import Weapon.weaponFile;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class weCommand extends Command{
     public weCommand() {
        super("we","武器系统","we help");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        try {
            if(commandSender.isOp()){
                    if(strings.length < 1 || strings.length > 3) return false;
                    List<String> args = Arrays.asList(strings);
                    switch (strings[0]){
                        case "help":
                            commandSender.sendMessage("--------(抱歉，没有彩色和UI)--------");
                            commandSender.sendMessage("/we add <武器名称> <ID:Damage> 添加武器");
                            commandSender.sendMessage("/we give <武器名称> <Player> 给玩家武器");
                            commandSender.sendMessage("/we addItem <宝石名称> <ID:Damage> 添加宝石");
                            commandSender.sendMessage("/we giveItem <宝石名称> <Player> 给玩家宝石");
                            commandSender.sendMessage("--------(抱歉，没有彩色和UI)--------");
                            break;
                        case "add":
                            String weaponName = args.get(1);
                            String id = args.get(2);
                            File f = new File(weapon.getObject().getDataFolder()+"/Weapons/"+weaponName+".yml");
                            if(f.exists()){
                                commandSender.sendMessage(TextFormat.RED+"此武器已存在");
                                return  false;
                            }
                            weaponFile.getAPI().init(weaponName,id);
                            commandSender.sendMessage(TextFormat.GREEN+"武器创建成功");
                            break;
                        case "give":
                            String players = args.get(2);
                            Player player = Server.getInstance().getPlayer(players);
                            if(player == null){
                                commandSender.sendMessage(TextFormat.RED+"该玩家不在线");
                                return false;
                            }
                            String Name = args.get(1);
                            File file = new File(weapon.getObject().getDataFolder()+"/Weapons/"+Name+".yml");
                            if(!file.exists()){
                                commandSender.sendMessage(TextFormat.RED+"此武器不存在");
                                return  false;
                            }
                            player.getInventory().addItem(weaponFile.getAPI().getWeaponItem(Name).setCustomName(args.get(1)));
                            commandSender.sendMessage(TextFormat.GREEN+"给予成功");
                            break;
                        case "addItem":
                            if(!ItemFile.getObject().can_addItem(args.get(1))){
                                commandSender.sendMessage(TextFormat.RED+"此宝石已存在");
                                return  false;
                            }

                            ItemFile.getObject().init(args.get(1),args.get(2));
                            commandSender.sendMessage(TextFormat.GREEN+"宝石创建成功");
                            break;
                        case "giveItem":
                            Player player1 = Server.getInstance().getPlayer(args.get(2));
                            if(player1 == null){
                                commandSender.sendMessage(TextFormat.RED+"该玩家不在线");
                                return false;
                            }
                            String Name1 = args.get(1);
                            if(ItemFile.getObject().can_addItem(Name1)){
                                commandSender.sendMessage(TextFormat.RED+"此宝石不存在");
                                return  false;
                            }
                            player1.getInventory().addItem(ItemFile.getObject().getItem(Name1).setCustomName(args.get(1)));
                            commandSender.sendMessage(TextFormat.GREEN+"给予成功");

                            break;
                    }

            }
        }catch (ArrayIndexOutOfBoundsException e){
            return false;
        }

        return false;
    }
}
