<?php

namespace NewTask\Item_List;


use pocketmine\item\Item;

 class getItem_Name implements DataList
{
    static function getName($string):?string {
        try{
            if($string instanceof Item){
                if($string->getCustomName() != ""){
                    return $string->getCustomName();
                }else{
                    $string = $string->getId().":".$string->getDamage();
                }
            }
            $item = explode(":", (string)$string);
            if(in_array($string, self::ITEM_ID))
            {
                return array_search($string, self::ITEM_ID);
            }
            if(in_array((string)$item[0], self::ITEM_ID))
            {
                return array_search((string)$item[0],self::ITEM_ID);
            }
            $name = Item::get((int)$item[0],(int)$item[1])->getName();
            return ($name === "Unknown")? "未知物品": $name;

        }catch(\Exception $e){
            return "请自定义名称";
        }
    }
}