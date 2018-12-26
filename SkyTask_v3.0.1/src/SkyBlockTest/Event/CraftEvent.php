<?php
/**
 * Created by PhpStorm.
 * User: ZXR
 * Date: 2018/12/21
 * Time: 11:41
 */
declare(strict_types = 1);
namespace SkyBlockTest\Event;


use pocketmine\event\inventory\CraftItemEvent;
use pocketmine\event\Listener;
use pocketmine\level\sound\AnvilUseSound;
use pocketmine\math\Vector3;
use SkyBlockTest\TextMain;

class CraftEvent implements Listener
{
    protected $main;
    public function __construct(TextMain $main)
    {
        $this->main = $main;
    }
    //TODO 玩家合成物品
    public function Craft(CraftItemEvent $e){
        $player = $e->getPlayer();
        $items = $e->getOutputs();
        foreach ($items as $item){
            $playerConfig = $this->main->getPlayerConfig($player)->getAll();
            foreach ($playerConfig as $test => $value){
                if($playerConfig[$test]["当前状态"] == false) continue;
                $testArray = $this->main->getTestConfig($test)->getAll();
                if($testArray["任务类型"] == "craft"){
                    foreach ($value["Item"] as $name => $item_){
                        $names = explode(':',$name);
                        if($item->getId().":".$item->getDamage() == $names[0].":".$names[1]){
                            $playerConfig[$test]["Item"][$name] += $item->getCount();
                        }
                    }
                    $this->main->savePlayerConfig($player,$playerConfig);
                    if($this->main->if_complete($player,$test)){
                        $player->sendMessage("§e叮咚~~恭喜你完成了§b[§c".TextMain::getStar((int)$testArray["难度"]).
                            "§b]§r§b[".$test."§b]§a任务~~§d快去领取奖励吧");
                        $sound = new AnvilUseSound(new Vector3($player->x,$player->y,$player->z));
                        $player->level->addSound($sound);
                    }
                }
            }
        }
    }
}