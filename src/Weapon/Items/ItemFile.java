package Weapon.Items;

import Weapon.weapon;
import Weapon.weaponFile;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import java.io.File;
import java.util.*;


public class ItemFile {
    private static ItemFile object;

    public ItemFile(){
        object = this;
    }

    public static ItemFile getObject() {
        return object;
    }

    public void init(String itemName,String id){
        LinkedHashMap<String,Object> file = new LinkedHashMap<>();
        file.put("ID",id);
        file.put("star",1);
        file.put("type","damage");
        file.put("AddDamage",5);
        file.put("AddHealth",10);
        file.put("DelDamage","1:100");
        file.put("Ice","5:30");
        file.put("addHealth","0:0");//吸血
        file.put("kick",0.1);
        file.put("fire","0:0");
        file.put("effect",new ArrayList<String>());
        file.put("addEffect",new ArrayList<String>());
        Config config = weapon.getObject().getItemConfig(itemName);
        config.setAll(file);
        config.save();
    }


    public boolean can_addItem(String string){
        File f = new File(weapon.getObject().getDataFolder()+"/Items/"+string+".yml");
        return !f.exists();
    }

    public int getDamage(String itemName){
        return weapon.getObject().getItemConfig(itemName).getInt("AddDamage");
    }

    public int getHealth(String itemName){
        return weapon.getObject().getItemConfig(itemName).getInt("AddHealth");
    }

    private CompoundTag getTag(String itemName){
        return new CompoundTag()
                .putString("id","item")
                .putString("name",itemName);
    }

    private String getItemName(Item item){
        if(item.hasCompoundTag()){
            CompoundTag tag = item.getNamedTag();
            if(tag.contains("id")){
                if(tag.getString("id").contains("item")){
                    return tag.getString("name");
                }
            }
        }
        return null;
    }
    public Item getItem(String itemName){
        Config config = weapon.getObject().getItemConfig(itemName);
        String id = config.getString("ID");
        Item item = new Item(Integer.parseInt(id.split(":")[0]),Integer.parseInt(id.split(":")[1]),1);
        item.setNamedTag(getTag(itemName));
        Enchantment enchantment = Enchantment.get(0);
        enchantment.setLevel(1);
        item.addEnchantment(enchantment);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§r§c▂§6▂§e▂§a▂§b▂§9▂§d▂§9▂§b▂§a▂§e▂§6▂§c▂");
        lore.add("类型 "+config.getString("type"));
        lore.add("§r§e品质 §d"+getStar(config.getInt("star")));
        lore.add("§r§a镶嵌后: ");
        if(config.getInt("AddDamage") > 0)
            lore.add("§r§c伤害 + "+config.getInt("AddDamage"));
        if(config.getInt("AddHealth") > 0)
            lore.add("§r§e生命提升 + "+config.getInt("AddHealth"));
        if(config.getInt("kick") > 0)
            lore.add("§r§6击退距离 + "+config.getInt("kick"));
        lore.add("§r§e┣┳┳┳┳┳┳┳╋┳┳┳┳┳┳┳┫");
        lore.add("§r§b附加技能:");
        lore.addAll(this.getSkill(config));
        lore.add("§r§e┣┳┳┳┳┳┳┳╋┳┳┳┳┳┳┳┫");
        lore.add("§r§c▂§6▂§e▂§a▂§b▂§9▂§d▂§9▂§b▂§a▂§e▂§6▂§c▂");
        item.setLore(lore.toArray(new String[lore.size()]));
        return item;
    }

    private static String getStar(int count){
        String Star = "";
        for (int c = 0;c < count;c++){
            Star = Star.concat(" ★ ");
        }
        return Star;
    }
    private ArrayList<String> getSkill(Config itemConfig){
        ArrayList<String> skills = new ArrayList<>();
        String[] ice = String.valueOf(itemConfig.getString("Ice")).split(":");
        String[] fire = String.valueOf(itemConfig.getString("fire")).split(":");
        String[] delDamage = String.valueOf(itemConfig.getString("DelDamage")).split(":");
        String[] addHealth = String.valueOf(itemConfig.getString("addHealth")).split(":");
        if(Integer.parseInt(ice[0]) != 0){
            skills.add("§e【主动】§b冰冻 冻结敌方，在一定的时间内无法移动\n§6持续§a "+ice[0]+" §6秒§7冷却§a "+ice[1]+"§6秒");
        }
        if(Integer.parseInt(fire[0]) != 0){
            skills.add("§e【主动】§4引燃 使敌方收到一定时间的火焰伤害\n§6持续§a "+fire[0]+" §6秒§7冷却§a "+fire[1]+"§6秒");
        }
        if(Integer.parseInt(delDamage[0]) != 0){
            skills.add("§c【被动】§7伤害减免 减免受到的伤害百分比[仅受到攻击有效]\n§6减免§a "+delDamage[0]+"％ §7冷却§a "+delDamage[1]+"§6秒");
        }
        if(Integer.parseInt(addHealth[0]) != 0){
            skills.add("§c【被动】§4嗜血 攻击敌方增加血量[仅受到攻击有效]\n§6伤害的§a "+addHealth[0]+"％ §7冷却§a "+addHealth[1]+"§6秒");
        }
        for (String id:itemConfig.getStringList("effect")){
            String[] value = id.split(":");
            int ids = Integer.parseInt(value[0]);
            skills.add("§c【被动】 "+ weaponFile.getEffectStringById(ids)+" "+
                    weaponFile.getLevelByString(Integer.parseInt(value[1]))+" §6持续§a "+value[2]+" §6秒"+"§7冷却§a "+value[3]+"§6秒");
        }
        for (String id:itemConfig.getStringList("addEffect")){
            String[] value = id.split(":");
            int ids = Integer.parseInt(String.valueOf(value[0]));
            skills.add("§e【主动】 "+weaponFile.getEffectStringById(ids)+" "+
                    weaponFile.getLevelByString(Integer.parseInt(value[0]))+" §6持续§a "+value[1]+" §6秒"+"§7冷却§a "+value[2]+"§6秒");
        }
        return skills;
    }



    public void removeItem(Player player,String itemName){
        if(this.can_removeItem(player,itemName)){
            for (Item item:player.getInventory().getContents().values()){
                String name = this.getItemName(item);
                if(name != null){
                    if(name.equals(itemName)){
                        player.getInventory().removeItem(item);
                        break;
                    }
                }
            }
        }
    }


    public boolean can_removeItem(Player player,String itemName){
        for (Item item:player.getInventory().getContents().values()){
            String name = this.getItemName(item);
            if(name != null){
                if(Objects.equals(name, itemName)){
                    return true;
                }
            }
        }
        return false;
    }

}
