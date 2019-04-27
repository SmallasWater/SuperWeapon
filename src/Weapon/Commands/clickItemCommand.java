package Weapon.Commands;


import Weapon.Items.ItemFile;
import Weapon.weapon;
import Weapon.weaponFile;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class clickItemCommand extends Command{

    public clickItemCommand() {
        super("click","武器镶嵌宝石指令","/click help");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player) {
            if (strings.length < 1 || strings.length > 3){
                commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                return false;
            }
            List<String> args = Arrays.asList(strings);
            switch (args.get(0)){
                case "help":
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    commandSender.sendMessage("/click add <宝石名称> 给手持的§c武器§a镶嵌宝石");
                    commandSender.sendMessage("/click remove <宝石名称> 给手持的§c武器§a拆除宝石宝石");
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    break;
                case "add":
                    if(args.get(1) == null && args.get(1).equals("")){
                        commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                        return false;
                    }
                    Item itemHand = ((Player) commandSender).getInventory().getItemInHand();
                    String weaponName = weaponFile.getAPI().getWeaponName(itemHand);
                    if(weaponName == null){
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§e[镶嵌系统]§c此物品不可镶嵌宝石 ");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        return false;
                    }
                    if(!ItemFile.getObject().can_removeItem((Player) commandSender,args.get(1))){
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§e[镶嵌系统]§c抱歉，你背包并没有"+args.get(1)+"这个宝石 ");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        return false;
                    }
                    if(!weaponFile.getAPI().can_addItems(itemHand,args.get(1))){
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§e[镶嵌系统]§c宝石镶嵌失败    ");
                        commandSender.sendMessage("§r§e[镶嵌系统]§c已存在相同类型的宝石  ");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        return false;
                    }
                    int math = weapon.getObject().getWeaponConfig(weaponName).getInt("count-add-Item")
                            - weaponFile.getAPI().getWeaponItems(itemHand).size();
                    if(math <= 0){
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§e[镶嵌系统]§c宝石镶嵌失败    ");
                        commandSender.sendMessage("§r§e[镶嵌系统]§c你没有多余的空槽  ");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        return false;
                    }
                    Item weapons = weaponFile.getAPI().addItems(itemHand,args.get(1));
                    ArrayList<String> setLore = weaponFile.getAPI().getLore(weapons);
                    String[] lore = setLore.toArray(new String[setLore.size()]);
                    weapons.setLore(lore);
                    ((Player) commandSender).getInventory().setItemInHand(weapons);
                    ItemFile.getObject().removeItem((Player) commandSender,args.get(1));
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    commandSender.sendMessage("§r§e[镶嵌系统]§e恭喜   宝石镶嵌成功  ");
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    break;
                case "remove":
                    if(args.get(1) == null && args.get(1).equals("")){
                        commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                        return false;
                    }
                    Item itemHand1 = ((Player) commandSender).getInventory().getItemInHand();
                    String weaponName1 = weaponFile.getAPI().getWeaponName(itemHand1);
                    if(weaponName1 == null){
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§e[镶嵌系统]§c此物品无宝石 ");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        return false;
                    }

                    if(weaponFile.getAPI().can_remove_item(itemHand1,args.get(1))){
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§e[镶嵌系统]§c抱歉，你的武器并没有这个宝石 ");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        return false;
                    }
                    itemHand1 = weaponFile.getAPI().removeItems(itemHand1,args.get(1));
                    if(weaponFile.getAPI().getWeaponItems(itemHand1).size() == 0){
                        Config we = weapon.getObject().getWeaponConfig(weaponFile.getAPI().getWeaponName(itemHand1));
                        ArrayList<String> set = weaponFile.getAPI().getLore(we);
                        String[] l = set.toArray(new String[set.size()]);
                        itemHand1.setLore(l);
                    }else{
                        ArrayList<String> set = weaponFile.getAPI().getLore(itemHand1);
                        String[] l = set.toArray(new String[set.size()]);
                        itemHand1.setLore(l);
                    }
                    ((Player) commandSender).getInventory().setItemInHand(itemHand1);
                    ((Player) commandSender).getInventory().addItem(ItemFile.getObject().getItem(args.get(1)).setCustomName(args.get(1)));
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    commandSender.sendMessage("§r§e[镶嵌系统]§e恭喜   宝石拆除成功  ");
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    break;
                default:
                    commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                    break;
            }
        }
        return false;
    }
}
