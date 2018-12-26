<?php
/**
 * Created by PhpStorm.
 * User: ZXR
 * Date: 2018/12/21
 * Time: 11:57
 */
declare(strict_types = 1);
namespace SkyBlockTest\Event;


use pocketmine\event\Listener;
use pocketmine\event\player\PlayerJoinEvent;
use pocketmine\Server;
use pocketmine\utils\TextFormat;
use SkyBlockTest\TextMain;

class JoinEvent implements Listener
{
    //TODO 玩家加入事件

    protected $main;
    public function __construct(TextMain $main)
    {
        $this->main = $main;
    }
    public function onJoin(PlayerJoinEvent $e){
        $player = $e->getPlayer();
        if(!$this->main->if_exitPlayerFile($player)){
            Server::getInstance()->broadcastMessage(TextFormat::GREEN.$player->getName().TextFormat::WHITE."刚刚获得了".TextFormat::GREEN."开启任务时代!! 成就");
            $this->main->getPlayerConfig($player);
            $c = $this->main->getIntegralConfig();
            $array = $c->getAll();
            if(!isset($array[$player->getName()])){
                $array[$player->getName()] = 0;
                $c->setAll($array);
                $c->save();
            }
        }
    }
}