<?php

namespace NewTask\Form;


use pocketmine\item\Item;
use pocketmine\network\mcpe\protocol\ModalFormRequestPacket;
use pocketmine\Player;
use SkyBlockTest\Item_List\getItem_Name;
use NewTask\TextMain;

class sendUI implements _API
{
    protected $main,$id,$data;
    static $api;
    public function __construct(TextMain $main)
    {
        self::$api = $this;
        $this->main = $main;
    }
    static function getInstance():?sendUI{
        return self::$api;
    }
    public function send(Player $player):void{
        $pk = new ModalFormRequestPacket();
        $pk->formId = $this->id;
        $pk->formData = json_encode($this->data);
        unset($this->cid,$this->data);
        $player->sendDataPacket($pk);
    }
    public function getID():?int{
        return $this->id;
    }

    public function getData():?array{
        return $this->data;
    }

    public function sendMenu(Player $player){
        $data = self::DEFAULT_BUTTON;
        $this->id = self::MENU;
        $data[self::CONTENT] = "开始任务,请点击按钮完成后将不再记录";
        $star = $this->main->star[$player->getName()];
        $buttons = [];
        $playerArray = $this->main->getPlayerConfig($player)->getAll();
        foreach ($this->main->getTaskList() as $task) {
            $taskArray = $this->main->getTestConfig($task)->getAll();
            if ($taskArray["难度"] == $star) {
                $array = ["网络", "本地"];
                $type = "path";
                if (in_array($taskArray["自定义按键图片路径"]["位置"], $array)) {
                    $type = str_replace($array, array("url", "path"), $taskArray["自定义按键图片路径"]["位置"]);
                }
                $message = "§b[可领取]";
                if (isset($playerArray[$task])) {
                    if ($playerArray[$task]["完成次数"] > 0) {
                        if ($playerArray[$task]["当前状态"] == false && $taskArray["任务奖励"]["积分"] != 0) {
                            $message = "§c[已完成 可重复领取]";
                        } else {
                            $message = "§c[已完成 不可重复领取]";
                        }
                    }
                    if ($playerArray[$task]["当前状态"] == true && !$this->main->if_complete($player, $task)) {
                        $message = "§7[正在进行]";
                    } else if ($playerArray[$task]["当前状态"] == true && $this->main->if_complete($player, $task)) {
                        $message = "§e[可完成]";
                    }

                }
                $buttons[] = [
                    self::TEXT => $task . $message,
                    "image" => [
                        "type" => $type,
                        "data" => $taskArray["自定义按键图片路径"]["路径"]
                    ]
                ];
            }
        }
        if ($buttons == null) {
                $data[self::CONTENT] = $data[self::CONTENT] . "\n\n\n\n当前没有任何任务哦";
        }
        $data[self::TITLE] = "任务系统";
        $data[self::BUTTONS] = $buttons;
        $this->data = $data;
        $this->send($player);

    }

    public function sendMessageUI(Player $player){
        $task = $this->main->getPlayerTask()[$player->getName()];
        try{
        $data = self::DEFAULT_BUTTON;
        $taskArray = $this->main->getTestConfig($task)->getAll();
        $playerArray = $this->main->getPlayerConfig($player)->getAll();
        $this->id = self::MENU_MESSAGE;

        $m = "";
        if($taskArray["任务类型"] == "reduce"){
            foreach ($playerArray[$task]["Item"] as $item=>$value){
                $item = explode(':',$item);
                $itemName = getItem_Name::getName($item[0].":".$item[1]);
                $items = Item::get($item[0],$item[1],$item[2]);
                $i = $this->main->ItemIn_arrayPlayerInventory($items,$player);
                $c = $i->getCount();
                $m .= $itemName.">  ".$c." / ".$item[2]."\n";
            }
        }else{
            foreach ($playerArray[$task]["Item"] as $item=>$value) {
                $item = explode(':', $item);
                $itemName = getItem_Name::getName($item[0] . ":" . $item[1]);
                $m .= $itemName . ">  " . $value . " / " . $item[2] . "\n";
            }
        }
        $Success = "";
        if($playerArray[$task]["完成次数"] == 0){
            $arrays = $taskArray["任务首次完成奖励"];
        }else{
            $arrays = $taskArray["任务奖励"];
        }
        $Success .= $this->main->getConf()->get("金币名称")." > ".$arrays["金币"]."\n";
        foreach ($arrays["物品"] as $value=>$is){
            $i = explode(':',$is);
            $Success .= getItem_Name::getName($i[0].":".$i[1])." * ".$i[2]."\n";
        }
        foreach ($arrays["指令"] as $cmd){
            $Success .= $cmd["name"]."\n";
        }
        $exp = $arrays["积分"];
        $message = "§l■任务主题:    §e【".$task."】\n".
                   "§e任务难度:  §c".TextMain::getStar($taskArray["难度"])."\n\n".
                   "§e任务内容:  §f\n".$taskArray["任务说明"]."\n\n".
                   "§e当前进度:  §f\n".$m."\n\n".
                   "§e奖励内容:  §f\n".$Success."\n奖励积分:  ".$exp;
        $data[self::CONTENT] = $message;
        $buttons = [];
        if($this->main->if_complete($player,$task)){
            $buttons[] = [
                self::TEXT =>"§e提交任务",
                "image"=>[
                    "type"=>"path",
                    "data"=>"textures/ui/confirm"
                ]
            ];
        }else{
            $buttons[] = [
                self::TEXT =>"§c未完成,请继续努力",
                "image"=>[
                    "type"=>"path",
                    "data"=>"textures/ui/cancel"
                ]
            ];
        }
        $buttons[] = [
            self::TEXT =>"返回",
            "image"=>[
                "type"=>"path",
                "data"=>"textures/ui/refresh_light"
            ]
        ];

        $data[self::BUTTONS] = $buttons;
        $this->data = $data;
        $this->send($player);
        }catch(\Exception $e){
            unlink($this->main->getDataFolder()."/Test/".$task.".yml");
            $this->main->getLogger()->warning("检测到".$task."文件错误，现已删除");
            $this->main->fixTask();
            $this->sendMessageUI($player);
            return;

        }
    }
    public function sendMenuChose(Player $player){
        try {
            $this->id = self::MENU_ADMIN;
            $data = self::DEFAULT_BUTTON;
            $buttons = [];
            $i = 1;
            $playerConfig = $this->main->getPlayerConfig($player)->getAll();
            $playerArray = $this->main->getIntegralConfig()->getAll();
            $intString = $this->main->getConf()->getAll();
            $message = "";
            foreach (self::DEFAULT_STAR as $name => $image) {
                if($this->main->getConf()->get("是否开启积分验证") == true){
                    $message = "§c(任务积分达到" . $intString["任务等级系统"][TextMain::$level[$i - 1]] . "解锁)";
                    if ($intString["任务等级系统"][TextMain::$level[$i - 1]] <= $playerArray[$player->getName()]) {
                        $message = "";
                    }
                }
                $success = "";
                $number = 0;
                $number_run = 0;
                $number_get = 0;
                if ($this->main->if_Success_All($player, $i)) {
                    $success = "§c[全部达成]";
                }else{
                    if($this->main->getTaskLevel($i,$playerArray[$player->getName()])){
                        foreach ($this->main->getTaskList() as $task){
                            $taskArray = $this->main->getTestConfig($task)->getAll();
                            if($taskArray["难度"] == $i){
                                if($this->main->if_complete($player,$task)){
                                    $number++;
                                }else if (isset($playerConfig[$task])){
                                    if($playerConfig[$task]["当前状态"] == true){
                                        $number_run++;
                                    }
                                }else{
                                    $number_get++;
                                }

                            }
                        }
                    }
                }
                if($number > 0){
                    $success = "§e[你有§a".$number."§e项可以完成]";
                }else if($number_run > 0){
                    $success = "§7[你有§a".$number_run."§7项正在进行]";
                }else if($number_get > 0){
                    $success = "§b[你有§a".$number_get."§b项可以领取]";
                }
                $buttons[] = [
                    "text" => $name . $message . $success,
                    "image" => [
                        "type" => "path",
                        "data" => $image
                    ]
                ];
                $i++;
            }
            $data[self::TITLE] = "任务系统";
            if($this->main->getConf()->get("是否开启积分验证") == true) {
                $data[self::CONTENT] = "§b当前任务积分   " . $playerArray[$player->getName()];
            }
            $data[self::BUTTONS] = $buttons;
            $this->data = $data;
            $this->send($player);
        }catch (\Exception $e){
            $this->main->fixTask();
            return;
        }
    }

    public function sendAddTestUI(Player $player){
        $this->id = self::ADMIN_ADD_TASK;
        $data = self::DEFAULT_INPUT;
        $const = [
            ["type"=>"input",
                "text"=>"请输入任务名称"],
            ["type"=>"input",
                "text"=>"请输入难度(1 - 5)"],
            ["type"=>"dropdown",
                "text"=>"请选择任务类型",
                "options"=>TextMain::TYPE],
            ["type"=>"input",
                "text"=>"请输入完成条件 (物品ID:特殊值:数量)(用@分隔开多个物品)"],
            ["type"=>"input",
                "text"=>"请输入任务介绍(英文,换行)"],
            ["type"=>"input",
                "text"=>"请输入奖励的物品(可空)"],
            ["type"=>"input",
                "text"=>"请输入指令奖励[@p代表玩家 &间隔多个指令"],
        ];
        $data[self::CONTENT] = $const;
        $this->data = $data;
        $this->send($player);
    }


}