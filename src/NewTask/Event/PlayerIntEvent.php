<?php

declare(strict_types = 1);
namespace NewTask\Event;


use pocketmine\event\Listener;
use pocketmine\event\server\DataPacketReceiveEvent;
use pocketmine\item\Item;
use pocketmine\level\sound\GhastShootSound;
use pocketmine\level\sound\GhastSound;
use pocketmine\math\Vector3;
use pocketmine\network\mcpe\protocol\ModalFormResponsePacket;
use pocketmine\Server;
use pocketmine\utils\TextFormat;
use NewTask\Form\_API;
use NewTask\Form\sendUI;
use NewTask\TextMain;

class PlayerIntEvent implements Listener,_API
{
    protected $main;
    public function __construct(TextMain $main)
    {
        $this->main = $main;
    }
    public function onData(DataPacketReceiveEvent $e){
        $player = $e->getPlayer();
        $pk = $e->getPacket();
        if($pk instanceof ModalFormResponsePacket){
            $form_id = $pk->formId;
            $data = json_decode($pk->formData);
            switch($form_id){
                case self::MENU:
                    if ($pk->formData == "null\n") return;
                    $task = $this->main->getClickByStar($player,(int)$data);
                    $testConfig = $this->main->getTestConfig($task)->getAll();
                    $playerConfig = $this->main->getPlayerConfig($player)->getAll();
                    if(!isset($playerConfig[$task])){
                        $array = TextMain::$defaultPlayer;
                        foreach ($testConfig["完成条件"] as $item){
                            $array["Item"][$item] = 0;
                        }
                        $playerConfig[$task] = $array;
                        $playerConfig[$task]["当前状态"] = true;
                        $this->main->savePlayerConfig($player,$playerConfig);
                    }else{
                        if($testConfig["任务奖励"]["积分"] == 0 && $playerConfig[$task]["当前状态"] != true
                            && $playerConfig[$task]["完成次数"] > 0){
                            $player->sendMessage(TextFormat::RED."抱歉，此任务不能重复领取~~");
                            $sound = new GhastShootSound(new Vector3($player->x,$player->y,$player->z));
                            $player->getLevel()->addSound($sound);
                            return;
                        }
                        $playerConfig[$task]["当前状态"] = true;
                        $this->main->savePlayerConfig($player,$playerConfig);
                    }
                    $ar = $this->main->getPlayerTask();
                    $ar[$player->getName()] = $task;
                    $this->main->setPlayerTask($ar);
                    sendUI::getInstance()->sendMessageUI($player);
                    break;
                case self::MENU_MESSAGE:
                    if ($pk->formData == "null\n") return;
                    if((int)$data == 1){
                        sendUI::getInstance()->sendMenu($player);
                        return;
                    }
                    $task = $this->main->getPlayerTask()[$player->getName()];
                    if($this->main->if_complete($player,$task)){
                        $taskConfig = $this->main->getTestConfig($task)->getAll();
                        if($taskConfig["任务类型"] == "reduce"){
                            foreach ($taskConfig["完成条件"] as $item){
                                //TODO 如果是收集任务，则扣除物品
                                $item_ = Item::get((int)explode(':',$item)[0],(int)explode(':',$item)[1],(int)explode(':',$item)[2]);
                                $player->getInventory()->removeItem($item_);
                            }
                        }
                        $this->main->Success($task,$player);
                        //$player->sendMessage(TextFormat::GREEN."恭喜你完成任务");
                        if(isset($taskConfig["公告类型(0私聊,1公告)"])){
                            $broadcastMessage = str_replace(array("%p","%s"),[$player->getName(),$task],$taskConfig["任务完成公告"]);
                            if($taskConfig["公告类型(0私聊,1公告)"] != 0){
                                Server::getInstance()->broadcastMessage($broadcastMessage);
                            }else{
                                $player->sendMessage($broadcastMessage);
                            }
                        }
                        $sound = new GhastSound(new Vector3($player->x,$player->y,$player->z));
                        $player->getLevel()->addSound($sound);
                    }else{
                        $sound = new GhastShootSound(new Vector3($player->x,$player->y,$player->z));
                        $player->getLevel()->addSound($sound);
                        $player->sendMessage(TextFormat::RED."抱歉，你现在还不能完成此任务");
                    }
                    break;
                case self::ADMIN_ADD_TASK:

                    if ($data[0] == null || $data[0] == ''){
                        $player->sendMessage(TextFormat::RED."未知的任务名称");
                        return;
                    }
                    if ($data[1] == null || $data[1] == ''){
                        $player->sendMessage(TextFormat::RED."请设置难度");
                        return;
                    }
                    if ($data[3] == null || $data[3] == ''){
                        $player->sendMessage(TextFormat::RED."请输入完成条件");
                        return;
                    }
                    if ($data[4] == null || $data[4] == ''){
                        $player->sendMessage(TextFormat::RED."请输入任务介绍");
                        return;
                    }
                    $data[1] = intval($data[1]);
                    $items = [];
                    $addItem = [];
                    if(!isset(explode('@',$data[3])[1])){
                        $items[] = explode('@',$data[3])[0];
                    }else{
                        foreach (explode('@',$data[3]) as $item){

                            $items[] = $item;
                        }
                    }
                    if ($data[5] != null || $data[5] != ''){
                        if(!isset(explode('@',$data[5])[1])){
                            $addItem[] = explode('@',$data[5])[0];
                        }else{
                            foreach (explode('@',$data[5]) as $itemZ){
                                $addItem[] = $itemZ;
                            }
                        }
                    }
                    $cmd = [];
                    if($data[6] != null || $data[6] != '') {
                        foreach (explode('&', $data[6]) as $cmdS) {
                            $cmd[] = [
                                "cmd"=>$cmdS,
                                "name"=>"这是一条指令"
                            ];
                        }
                    }
                    $array = ["reduce","break","place","craft","get","eat","bucket","drop"];
                    $type = $array[(int)$data[2]];
                    if($this->main->if_exitTestFile($data[0])){
                        $player->sendMessage(TextFormat::RED."抱歉，已经有此任务了");
                        return;
                    }else{
                        $a = TextMain::$defaultTest;
                        $conf = $this->main->getTestConfig($data[0]);
                        $a["难度"] = (int)$data[1];
                        $a["任务说明"] = "请去配置文件更改";
                        $a["任务类型"] = $type;
                        $a["完成条件"] = $items;
                        $a["任务首次完成奖励"]["物品"] = $addItem;
                        $a["任务奖励"]["物品"] = $addItem;
                        $a["任务说明"] = str_replace(",","\n",$data[4]);
                        $a["任务首次完成奖励"]["指令"] = $cmd;
                        $a["任务奖励"]["指令"] = $cmd;
                        $conf->setAll($a);
                        $conf->save();
                        $player->sendMessage("任务添加成功");
                    }
                    break;
                case self::MENU_ADMIN:
                    if ($pk->formData == "null\n") return;
                    $playerC = $this->main->getIntegralConfig()->getAll();
                    if(!$this->main->getTaskLevel((int)$data+1,$playerC[$player->getName()])){
                        $player->sendMessage("§c抱歉~你的任务积分不足无法解锁此任务，快做任务升级吧~~");
                        $sound = new GhastShootSound(new Vector3($player->x,$player->y,$player->z));
                        $player->getLevel()->addSound($sound);
                        return;
                    }
                    $this->main->star[$player->getName()] = (int)$data+1;
                    sendUI::getInstance()->sendMenu($player);
                    return;
                    break;

            }
        }

    }

}