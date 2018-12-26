<?php


namespace SkyBlockTest\Event;


use pocketmine\event\Listener;

use pocketmine\level\sound\AnvilUseSound;
use pocketmine\math\Vector3;
use SkyBlockTest\TextMain;

class PlayerBucketEvent implements Listener
{
    //TODO 玩家打水任务
    protected $main;
    public function __construct(TextMain $main)
    {
        $this->main = $main;
    }
    public function Bucket(\pocketmine\event\player\PlayerBucketEvent $e){
        $player = $e->getPlayer();
        $item = $e->getItem();
        $playerConfig = $this->main->getPlayerConfig($player)->getAll();
        foreach ($playerConfig as $test => $value){
            if($playerConfig[$test]["当前状态"] == false) continue;
            $testArray = $this->main->getTestConfig($test)->getAll();
            if($testArray["任务类型"] == "bucket"){
                foreach ($value["Item"] as $name=>$items){
                    $names = explode(':',$name);
                    if($item->getId().":".$item->getDamage() == $names[0].":".$names[1]){
                        $playerConfig[$test]["Item"][$name] = $items+1;
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