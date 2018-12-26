<?php

declare(strict_types = 1);
namespace NewTask\Event;


use pocketmine\event\entity\EntityInventoryChangeEvent;
use pocketmine\event\Listener;
use pocketmine\level\sound\AnvilUseSound;
use pocketmine\math\Vector3;
use pocketmine\Player;
use NewTask\TextMain;

class EntityInventoryEvent implements Listener
{
    protected $main;
    public function __construct(TextMain $main)
    {
        $this->main = $main;
    }
    //TODO 玩家拾取物品
    public function addItems(EntityInventoryChangeEvent $e){
        $entity = $e->getEntity();
        if($entity instanceof Player){
            $item = $e->getNewItem();
            $playerConfig = $this->main->getPlayerConfig($entity->getName())->getAll();
            foreach ($playerConfig as $test => $value){
                if($playerConfig[$test]["当前状态"] == false) continue;
                $testArray = $this->main->getTestConfig($test)->getAll();
                if($testArray["任务类型"] == "get"){
                    foreach ($value["Item"] as $name=>$items){
                        $names = explode(':',$name);
                        if($item->getId().":".$item->getDamage() == $names[0].":".$names[1]){
                            $playerConfig[$test]["Item"][$name] += $item->getCount();
                        }
                    }
                    $this->main->savePlayerConfig($entity,$playerConfig);
                    if($this->main->if_complete($entity,$test)){
                        $entity->sendMessage("§e叮咚~~恭喜你完成了§b[§c".TextMain::getStar((int)$testArray["难度"]).
                            "§b]§r§b[".$test."§b]§a任务~~§d快去领取奖励吧");
                        $sound = new AnvilUseSound(new Vector3($entity->x,$entity->y,$entity->z));
                        $entity->level->addSound($sound);
                    }
                }
            }
        }
    }
}