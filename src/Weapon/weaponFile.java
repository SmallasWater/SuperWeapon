package Weapon;



import Weapon.Items.ItemFile;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.utils.Config;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

public class weaponFile {

    private static weaponFile api;
    weaponFile(){
        api = this;
    }

    public static weaponFile getAPI(){
        return api;
    }

    public Item getWeaponItem(String weaponName) {

        Config weaponConfig = weapon.getObject().getWeaponConfig(weaponName);
        String[] id = weaponConfig.getString("ID").split(":");
        Item item = new Item(Integer.parseInt(id[0]), Integer.parseInt(id[1]), 1);
        ArrayList<String> lore = this.getLore(weaponConfig);
        String[] setLore = lore.toArray(new String[lore.size()]);
        item.setNamedTag(this.getTag(weaponName));
        item.setLore(setLore);
        Enchantment enchantment = Enchantment.get(weaponConfig.getInt("enchantID"));
        enchantment.setLevel(weaponConfig.getInt("enchantLevel"));
        item.addEnchantment(enchantment);
        return item;
    }

    private CompoundTag getTag(String weaponName){
        Config weaponConfig = weapon.getObject().getWeaponConfig(weaponName);
        CompoundTag tag;
        if(weaponConfig.getBoolean("nai")){
             tag = new CompoundTag()
                    .putString("id","weapon")
                    .putString("name",weaponName)
                     .putList(new ListTag<StringTag>("bao"));
        }else{
            tag = new CompoundTag()
                    .putByte("Unbreakable",1)
                    .putString("id","weapon")
                    .putString("name",weaponName)
                    .putList(new ListTag<StringTag>("bao"));
        }
        return tag;
    }

    public String getWeaponName(Item item){
        if(item.hasCompoundTag()){
            CompoundTag tag = item.getNamedTag();
            if(tag.contains("id") && tag.contains("name")){
                if(tag.getString("id").equals("weapon")){
                    //return tag.getString("name");
                    String weaponName = tag.getString("name");
                    if(weapon.getObject().getWeaponConfig(weaponName) != null){
                        return weaponName;
                    }
                }
            }
        }
        return null;
    }


    private static String getStar(int count){
        String Star = "";
        for (int c = 0;c < count;c++){
            Star = Star.concat(" ★ ");
        }
        return Star;
    }


    public void init(String weaponName, String id){
        Map<String,Object> file = new LinkedHashMap<>();
        file.put("ID",id);
        file.put("star",1);
        file.put("enchantID",0);
        file.put("enchantLevel",1);
        file.put("unDestruction",true);
        file.put("message","这是一把武器");
        file.put("damage",6);
        file.put("addHealth",10);
        file.put("kick",Double.parseDouble("0.1"));
        file.put("can-drop",true);
        file.put("killMessage","{killer}使用"+weaponName+"砸死了{player}");
        file.put("count-add-Item",5);
        Config config = weapon.getObject().getWeaponConfig(weaponName);
        config.setAll((LinkedHashMap<String, Object>) file);
        config.save();
    }



    public static String getEffectStringById(int id){
        String[] array = new String[]{"","§f急速","§c减速","§e破坏","§4疲劳","§c强壮","§a瞬间回血",
                "§4真实伤害","§b跳跃增幅","§4眩晕","§a治疗","§7护甲","§6炎热抗性",
                "§9深海行走","§7隐身","§4致盲","§8夜间视力","§4饥饿者","§4虚弱","§4毒素","§4死亡诅咒",
                "§d血量增益","§e伤害吸收","§6饱食者","§d飞行","剧毒?",""};
        try{
            return array[id];
        }catch (ArrayIndexOutOfBoundsException e) {
            return array[0];
        }
    }

    public static String getLevelByString(int level){
        String[] array = new String[]{"","I","II","III","IV","V","VI","VII","VIII","IX","X","XI","XII"};
        if(level <= 12){
            return array[level];
        }else{
            return "";
        }
    }


    public List<String> getWeaponItems(Item weaponItem){
        List<String> Items = new ArrayList<>();
        if(this.getWeaponName(weaponItem) != null){
            CompoundTag tag = weaponItem.getNamedTag();
            if(tag.contains("bao")){
                ListTag l = tag.getList("bao");
                if(l.getAll() != null){
                    for (Object c:l.getAll()){
                        if(c instanceof StringTag){
                            if(((StringTag) c).data != null){
                                if(!ItemFile.getObject().can_addItem(((StringTag) c).data)){
                                    Items.add(((StringTag) c).data);
                                }
                            }
                        }
                    }
                }
            }
        }
        return Items;
    }

    public Item addItems(Item weaponItem,String item){
        if(this.getWeaponName(weaponItem) != null){
            CompoundTag tag = weaponItem.getNamedTag();
            List<String> l =  this.getWeaponItems(weaponItem);
            l.add(item);
            ListTag<StringTag> t = new ListTag<>("bao");
            for(String i:l){
                t.add(new StringTag(i,i));
            }
            weaponItem.setNamedTag(tag.putList(t));
        }
        return weaponItem;
    }
    public Item removeItems(Item weaponItem, String item){
        if(this.getWeaponName(weaponItem) != null){
            CompoundTag tag = weaponItem.getNamedTag();
            List<String> l =  this.getWeaponItems(weaponItem);
            l.remove(item);
            ListTag<StringTag> t = new ListTag<>("bao");
            if(l.size() > 0){
                for(String i:l){
                    t.add(new StringTag(i,i));
                }
            }
            weaponItem.setNamedTag(tag.putList(t));
        }
        return weaponItem;
    }


    public boolean can_addItems(Item weaponItem,String item){
        if(this.getWeaponName(weaponItem) != null){
            Config weaponConfig = weapon.getObject().getWeaponConfig(this.getWeaponName(weaponItem));
            Config itemConfig = weapon.getObject().getItemConfig(item);
            CompoundTag tag = weaponItem.getNamedTag();
            int list = tag.getList("items").getAll().size();
            if(list <= weaponConfig.getInt("count-add-Item")){
                List<String> array = this.getWeaponItems(weaponItem);
                if(array.contains(item)){
                    return false;
                }else{
                    for(String i:array){
                        Config item_ = weapon.getObject().getItemConfig(i);
                        if(item_.getString("type").equals(itemConfig.getString("type"))){
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }



    public boolean can_remove_item(Item weaponItem,String item){
        if(this.getWeaponName(weaponItem) != null){
            CompoundTag tag = weaponItem.getNamedTag();
            if(!tag.contains("items"))return false;
            if(tag.getList(item) != null) return true;
        }
        return false;
    }


    //获取武器中所有的宝石，并合并伤害
    public HashMap<String,Object> getItemAttributeAll(List<String> weaponItems){
        HashMap<String,Object> attribute = new HashMap<>();
        double kick = 0;
        HashMap<Integer,String> effect    = new HashMap<>(),
                                addEffect = new HashMap<>();
        int AddDamage = 0,AddHealth = 0;
        for(String item:weaponItems){
            Config itemConfig = weapon.getObject().getItemConfig(item);
            kick += itemConfig.getDouble("kick");
            AddDamage += Integer.parseInt(String.valueOf(attribute.get("AddDamage")!=null?
                    attribute.get("AddDamage"):"0"))+itemConfig.getInt("AddDamage");
            AddHealth += Integer.parseInt(String.valueOf(attribute.get("AddHealth")!=null?
                    attribute.get("AddHealth"):"0"))+itemConfig.getInt("AddHealth");
            DecimalFormat df = new DecimalFormat("0.00");
            kick = Double.parseDouble(df.format(kick));
            for(String s : itemConfig.getStringList("effect")) {
                List<String> arrays = Arrays.asList(s.split(":"));
                //id:level:时间:冷却
                if(arrays.size() == 1){
                    effect.put(Integer.parseInt(arrays.get(0)),"1:5:0");
                }else if(arrays.size() == 2){
                    effect.put(Integer.parseInt(arrays.get(0)),arrays.get(1)+":5:0");
                }else if(arrays.size() == 3){
                    effect.put(Integer.parseInt(arrays.get(0)),arrays.get(1)+":"+arrays.get(2)+":0");
                }else{
                    effect.put(Integer.parseInt(arrays.get(0)),arrays.get(1)+":"+arrays.get(2)+":"+arrays.get(3));
                }
            }
            //effect.put()
            for(String s : itemConfig.getStringList("addEffect")) {
                List<String> array = Arrays.asList(s.split(":"));
                //id:level:时间:冷却
                if(array.size() == 1){
                    addEffect.put(Integer.parseInt(array.get(0)),"1:5:30");
                }else if(array.size() == 2){
                    addEffect.put(Integer.parseInt(array.get(0)),array.get(1)+":5:30");
                }else if(array.size() == 3){
                    addEffect.put(Integer.parseInt(array.get(0)),array.get(1)+":"+array.get(2)+":30");
                }else{
                    addEffect.put(Integer.parseInt(array.get(0)),array.get(1)+":"+array.get(2)+":"+array.get(3));
                }
            }
        }
        HashMap<String,String> invite = this.invite(weaponItems);
        attribute.put("AddDamage",AddDamage);
        attribute.put("AddHealth",AddHealth);
        attribute.put("DelDamage",invite.get("DelDamage") != null?invite.get("DelDamage"):"0:0");
        attribute.put("Ice",invite.get("Ice") != null?invite.get("Ice"):"0:0");
        attribute.put("addHealth",invite.get("addHealth") != null?invite.get("addHealth"):"0:0");
        attribute.put("kick",kick);
        attribute.put("fire",invite.get("fire") != null?invite.get("fire"):"0:0");
        attribute.put("effect",effect);
        attribute.put("addEffect",addEffect);
        return attribute;

    }

    public ArrayList<String> getLore(Config weaponConfig){
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§r§c▂§6▂§e▂§a▂§b▂§9▂§d▂§9▂§b▂§a▂§e▂§6▂§c▂");
        lore.add("§r§e品质 §d"+getStar(weaponConfig.getInt("star")));
        lore.add("§r§d介绍 ");
        lore.add("- "+weaponConfig.getString("message"));
        if(weaponConfig.getInt("damage") != 0){
            lore.add("§r§b伤害 §e+"+weaponConfig.getInt("damage"));
        }
        if(weaponConfig.getInt("addHealth") != 0){
            lore.add("§r§a生命提升 §e+"+weaponConfig.getInt("addHealth"));
        }
        if(weaponConfig.getDouble("kick") != 0){
            lore.add("§r§c击退 §e+"+weaponConfig.getInt("kick"));
        }
        lore.add("§r§e┣┳┳┳┳┳┳┳╋┳┳┳┳┳┳┳┫");
        lore.add("§r§6宝石: ");
        lore.add("§r§c未镶嵌 ");
        lore.add("§r§e当前可镶嵌 §a"+weaponConfig.getInt("count-add-Item"));
        lore.add("§r§e┣┳┳   ==============   ┳┳┫");
        lore.add("§r§a<<<<<<<<<<<§e宝石技能§a>>>>>>>>>>>>>");
        lore.add("§r§c无");
        lore.add("§r§a<<<<<<<<<<<§e宝石技能§a>>>>>>>>>>>>>");
        lore.add("§r§e┣┳┳   ==============   ┳┳┫");
        lore.add("§r§e┣┳┳┳┳┳┳┳╋┳┳┳┳┳┳┳┫");
        lore.add( weaponConfig.getBoolean("unDestruction")?"§c无限耐久":"");
        lore.add("§r§c▂§6▂§e▂§a▂§b▂§9▂§d▂§9▂§b▂§a▂§e▂§6▂§c▂");
        return lore;
    }

    public ArrayList<String> getLore(Item weaponItem){
        ArrayList<String> lore = new ArrayList<>();
        if(this.getWeaponName(weaponItem) != null){
            Config weaponConfig = weapon.getObject().getWeaponConfig(this.getWeaponName(weaponItem));
            HashMap<String,Object> itemDamage = this.getItemAttributeAll(this.getWeaponItems(weaponItem));
            int addDamage = Integer.parseInt(String.valueOf(itemDamage.get("AddDamage")));
            String damage = "";
            if(addDamage > 0){
                damage = "§b(§6宝石加成§b)§d + "+addDamage;
            }
            String addHealth = "";
            if(Integer.parseInt(String.valueOf(itemDamage.get("AddHealth"))) > 0){
                addHealth = "§b(§6宝石加成§b)§d + "+Integer.parseInt(String.valueOf(itemDamage.get("AddHealth")));
            }
            String k = "";
            if(Double.parseDouble(String.valueOf(itemDamage.get("kick"))) > 0){
                k = "§b(§6宝石加成§b)§d + "+itemDamage.get("kick");
            }
            lore.add("§r§c▂§6▂§e▂§a▂§b▂§9▂§d▂§9▂§b▂§a▂§e▂§6▂§c▂");
            lore.add("§r§e品质 §d"+getStar(weaponConfig.getInt("star")));
            lore.add("§r§d介绍 ");
            lore.add("- "+weaponConfig.getString("message"));
            lore.add("§r§b伤害 §e+"+weaponConfig.getInt("damage")+damage);
            lore.add("§r§b生命提升 §e+"+weaponConfig.getInt("addHealth")+addHealth);
            lore.add("§r§c击退 §e+"+weaponConfig.getInt("kick")+k);
            lore.add("§r§e┣┳┳┳┳┳┳┳╋┳┳┳┳┳┳┳┫");
            lore.add("§r§6宝石: ");
            int c = 1;
            if(this.getWeaponItems(weaponItem).size() != 0){
                StringBuilder lo = new StringBuilder();
                for (String i:this.getWeaponItems(weaponItem)){
                    lo.append(" - ").append(i);
                    c++;
                    if(c %2 != 0){
                        lo.append("\n");
                    }
                }

                lore.add(String.valueOf(lo));
            }else {
                lore.add("§r§c未镶嵌 ");
            }

            int math = weaponConfig.getInt("count-add-Item") - this.getWeaponItems(weaponItem).size();
            lore.add("§r§e当前可镶嵌 §a"+(math <= 0?"§c不可镶嵌":math));
            lore.add("§r§e┣┳┳   ==============   ┳┳┫");
            lore.add("§r§a<<<<<<<<<<<§e宝石技能§a>>>>>>>>>>>>>");
            lore.addAll(this.getSkill(itemDamage));
            lore.add("§r§a<<<<<<<<<<<§e宝石技能§a>>>>>>>>>>>>>");
            lore.add("§r§e┣┳┳   ==============   ┳┳┫");
            lore.add("§r§e┣┳┳┳┳┳┳┳╋┳┳┳┳┳┳┳┫");
            lore.add( weaponConfig.getBoolean("unDestruction")?"§c无限耐久":"");
            lore.add("§r§c▂§6▂§e▂§a▂§b▂§9▂§d▂§9▂§b▂§a▂§e▂§6▂§c▂");

        }
        return lore;
    }

//id:  level:时间:冷却
    private ArrayList<String> getSkill(HashMap<String,Object> skill){
        ArrayList<String> skills = new ArrayList<>();
        String[] ice = this.canAddSkill(String.valueOf(skill.get("Ice")));
        String[] fire = this.canAddSkill(String.valueOf(skill.get("fire")));
        String[] delDamage = this.canAddSkill(String.valueOf(skill.get("DelDamage")));
        String[] addHealth = this.canAddSkill(String.valueOf(skill.get("addHealth")));
        if(ice != null){
            if(Integer.parseInt(ice[0]) > 0 && Integer.parseInt(ice[1]) > 0 ){
                skills.add("§e【主动】§b冰冻 §6持续§a "+ice[0]+"§6秒 §7冷却§a "+ice[1]+"§6秒");
            }
        }
        if(fire != null){
            if(Integer.parseInt(fire[0]) > 0 && Integer.parseInt(fire[1]) > 0){
                skills.add("§e【主动】§4引燃 §6持续§a "+fire[0]+"§6秒 §7冷却§a "+fire[1]+"§6秒");
            }
        }
       if(delDamage != null){
           if(Integer.parseInt(delDamage[0]) > 0 && Integer.parseInt(delDamage[1]) > 0){
               skills.add("§c【被动】§7伤害减免 §6减免§a "+delDamage[0]+"％ §7冷却§a "+delDamage[1]+"§6秒");
           }
       }
       if(addHealth != null){
           if(Integer.parseInt(addHealth[0]) > 0 && Integer.parseInt(addHealth[1]) > 0){
               skills.add("§c【被动】§4嗜血 §6伤害的§a "+addHealth[0]+"％ §7冷却§a "+addHealth[1]+"§6秒");
           }
       }
        if(skill.get("effect") instanceof HashMap){
            for (Object id:((HashMap) skill.get("effect")).keySet()){
                int ids = Integer.parseInt(String.valueOf(id));
                String[] value = String.valueOf (((HashMap) skill.get("effect")).get(ids)).split(":");
                skills.add("§c【被动】 "+getEffectStringById(ids)+" "+
                        getLevelByString(Integer.parseInt(value[0]))+" §6持续§a "+value[1]+" §6秒"+" §7冷却§a "+value[2]+"§6秒");
            }
        }
        if(skill.get("addEffect") instanceof HashMap){
            for (Object id:((HashMap) skill.get("addEffect")).keySet()){
                int ids = Integer.parseInt(String.valueOf(id));
                String[] value = String.valueOf (((HashMap) skill.get("addEffect")).get(ids)).split(":");
                skills.add("§e【主动】 "+getEffectStringById(ids)+" "+
                        getLevelByString(Integer.parseInt(value[0]))+" §6持续§a "+value[1]+" §6秒"+"§7冷却§a "+value[2]+"§6秒");
            }
        }
        return skills;
    }
    private HashMap<String,String> invite(List<String> lists){
        HashMap<String,String> listHashMap = new HashMap<>();
        //集合计算
        String[] s = new String[]{"DelDamage","addHealth","Ice","fire"};
        for (String item:lists){
            if(!ItemFile.getObject().can_addItem(item)){
                Config list = weapon.getObject().getItemConfig(item);
                for(String string:s){
                    if(!listHashMap.containsKey(string)){
                        listHashMap.put(string,"0:0");
                    }
                    List<String> l = Arrays.asList(list.getString(string).split(":"));
                    String[] map = listHashMap.get(string).split(":");
                    map[0] = String.valueOf(Integer.parseInt(l.get(0))+Integer.parseInt(map[0]));
                    map[1] = String.valueOf(Integer.parseInt(l.get(1))+Integer.parseInt(map[1]));
                    if(string.equals(s[0])){
                        if(Integer.parseInt(map[0]) > 100){
                            map[0] = "100";
                        }
                    }
                    listHashMap.put(string,map[0]+":"+map[1]);
                }
            }
        }
        return listHashMap;
    }


    private String[] canAddSkill(String skill){
        if(skill != null){
            String[] a = skill.split(":");
            if(Integer.parseInt(a[0]) > 0 && Integer.parseInt(a[1]) > 0){
                return a;
            }
        }
        return null;
    }

}
