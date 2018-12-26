<?php

declare(strict_types = 1);
namespace SkyBlockTest;

use onebone\economyapi\EconomyAPI;
use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\item\Item;
use pocketmine\math\Vector3;
use pocketmine\Player;
use pocketmine\plugin\PluginBase;
use pocketmine\utils\Config;
use SkyBlockTest\Event\BlockBreakEvent;
use SkyBlockTest\Event\BlockPlaceEvent;
use SkyBlockTest\Event\CraftEvent;
use SkyBlockTest\Event\EntityInventoryEvent;
use SkyBlockTest\Event\ItemConsumeEvent;
use SkyBlockTest\Event\JoinEvent;
use SkyBlockTest\Event\PlayerBucketEvent;
use SkyBlockTest\Event\PlayerDropEvent;
use SkyBlockTest\Event\PlayerIntEvent;
use SkyBlockTest\Form\sendUI;
use SkyBlockTest\Item_List\getItem_Name;

class TextMain extends PluginBase
{

    /*TODO 搭建框架
     * Item =>[
     *  "id:damage:count"=>"type
     * ]*/
    //static $isLevel = ["XS","S","M","L","XL"];  不靠谱
    private static $load_php = false;
    static $level = ["新手","简单","普通","困难","地狱"];
    public $star = [];
    static $defaultPlayer = [
        "完成次数"=>0,
        "当前状态"=>false,
        "Item"=>[]
    ];
    public $playerTask = [];
    const TYPE = ["收集","挖掘","放置[可用作种地]","合成","获得","食用","打水","丢弃"];
    const IMAGE_TYPE = "类型(本地/网络)";
    static $defaultTest = [
        "难度"=>1,
        "任务说明"=>"",
        "任务类型"=>"",
        "完成条件"=>[],
        "任务首次完成奖励"=>[
            "金币"=>100,
            "物品"=>[],
            "指令"=>[],
            "积分"=>0
        ],
        "任务奖励"=>[
            "金币"=>100,
            "物品"=>[],
            "指令"=>[],
            "积分"=>0
        ],
        "公告类型(0私聊,1公告)"=>0,
        "任务完成公告"=>"[任务系统]恭喜%p 完成了%s任务",
        "自定义按键图片路径"=>[
            "位置"=>"本地",
            "路径"=>""
        ]
    ];
    public function getPlayerTask(){
        return $this->playerTask;
    }

    public function setPlayerTask(array $array){
        $this->playerTask = $array;
    }
    public function savePlayerConfig($player,array $array):void{
        try{
            if($player instanceof Player){
                $player = $player->getName();
            }
            $Config = $this->getPlayerConfig($player);
            $Config->setAll($array);
            $Config->save();
            unset($Config,$player,$array);
        }catch (\Exception $e){
            if($this->getServer()->getPlayer($player) != null){
                $player->sendMessage("抱歉，在保存数据的时候出现错误.请联系管理员修复文件:".$e->getFile());
                $player->sendMessage($e->getMessage());
            }
        }
    }

    public function saveTestConfig(string $test,array $array):void{
        try{
            $Config = $this->getTestConfig($test);
            $Config->setAll($array);
            $Config->save();
            unset($Config,$player,$array);
        }catch (\Exception $e){
            $this->getLogger()->warning("抱歉，在保存数据的时候出现错误.请联系管理员修复文件:".$e->getFile());
        }
    }



    public function if_exitTestFile(string $test):?bool{
        return is_file($this->getDataFolder()."/Test/".$test.".yml");
    }

    public function if_exitPlayerFile($player):?bool{
        if($player instanceof Player){
            $player = $player->getName();
        }
        return is_file($this->getDataFolder()."/Player/".$player.".yml");
    }
   public function getTestConfig(string $test):?Config{
        return  new Config($this->getDataFolder()."/Test/".$test.".yml",Config::YAML,[]);
   }
   public function getPlayerConfig($player):?Config{
        if($player instanceof Player){
            $player = $player->getName();
        }
        return new Config($this->getDataFolder()."/Player/".$player.".yml",Config::YAML,[]);
   }
    public function getIntegralConfig():?Config{
        return new Config($this->getDataFolder()."Integral.yml",Config::YAML,[]);
    }

    public function getConf():?Config{
        return new Config($this->getDataFolder()."Config.yml",Config::YAML,[]);
    }
    public function onEnable()/* : void /* TODO: uncomment this for next major version */
    {
        self::$load_php = self::is_php()?true:false;
        if(!self::$load_php){
            $this->getLogger()->warning("由于PhP版本小于 7.2 此插件将无法加载");
            $this->getServer()->getPluginManager()->disablePlugin($this);
        }
        $this->getLogger()->info("插件加载成功__当前PHP版本".phpversion());
        @mkdir($this->getDataFolder(),0777,true);
        $this->register();
        @mkdir($this->getDataFolder()."Test/",0777,true);
        @mkdir($this->getDataFolder()."Player/",0777,true);
        $c = $this->getConf();
        if($c->getAll() == null){
            $c->setAll([
                "是否开启积分验证"=>true,
                "任务等级系统"=>[
                    "新手"=>0,
                    "简单"=>100,
                    "普通"=>500,
                    "困难"=>1000,
                    "地狱"=>2000,
                ],
                "金币名称"=>"§d金币§r"
            ]);
            $c->save();
        }
        $this->getIntegralConfig();
        new sendUI($this);
    }

    public function if_complete(Player $player,string $test):bool{
        //TODO 判断任务是否完成+检测
        $playerConfig = $this->getPlayerConfig($player)->getAll();
        if(!isset($playerConfig[$test])){
            return false;//找不到任务
        }
        $count = 0;
        //TODO ID:Damage=>type 收集任务判断条件
            $testArray = $this->getTestConfig($test)->getAll();
            if ($testArray["任务类型"] == "reduce"){
                foreach ($playerConfig[$test]["Item"] as $ID => $value){
                    $item = explode(':',$ID);
                    $items = Item::get((int)$item[0],(int)$item[1],(int)$item[2]);
                    $inventoryItem = $this->ItemIn_arrayPlayerInventory($items,$player);
                    if($inventoryItem->getCount() >= $item[2]){
                        $count++;
                    }
                }
                if($count == count($playerConfig[$test]["Item"])){
                    return true;
                }
            }else{
                $c = 0;
                foreach ($playerConfig[$test]["Item"] as $ID => $value){
                    $item = explode(':',$ID);
                    if($item[2] <= $value){
                        $c++;
                    }
                }
                if($c == count($playerConfig[$test]["Item"])){
                    return true;
                }
            }
        return false;
    }

    /**
     * @param string $test
     * @param Player $player
     */
    public function Success(string $test, Player $player):void{
        //TODO 完成任务奖励
        $testConfig = $this->getTestConfig($test)->getAll();
        $playerConfig = $this->getPlayerConfig($player)->getAll();
        $c = $this->getIntegralConfig();
        if(!isset($playerConfig[$test])) return;
        if($playerConfig[$test]["完成次数"] == 0){
            $testArray = $testConfig["任务首次完成奖励"];

        }else{
            $testArray = $testConfig["任务奖励"];
        }
        $exp = $testArray["积分"];
        $arr = $c->getAll();
        $arr[$player->getName()] += $exp;
        $c->setAll($arr);
        $c->save();

        $playerConfig[$test]["完成次数"]++;
        $playerConfig[$test]["当前状态"] = false;
            //TODO 清空任务次数

        foreach ($playerConfig[$test]["Item"] as $tests => $value){
            $playerConfig[$test]["Item"][$tests] = 0;
        }
        //TODO 颁发积分


        $this->savePlayerConfig($player,$playerConfig);
            //TODO 颁发奖励
        if($testArray["金币"] != 0){
            EconomyAPI::getInstance()->addMoney($player,$testArray["金币"],true);
            $player->sendMessage("§e恭喜 §b你获得了 §7".$testArray["金币"]." ".$this->getConf()->get("金币名称"));
        }
        foreach ($testArray["物品"] as $item=>$value){
            $Item = explode(':',$value);
            try{
                $i = Item::get((int)$Item[0],(int)$Item[1],(int)$Item[2]);
                if($player != null){
                    if(!$player->getInventory()->canAddItem($i)){
                        $player->sendMessage("§b抱歉，你的背包满啦，物品掉出来了哦~~");
                        $level = $player->getLevel();
                        $level->dropItem(new Vector3($player->x,$player->y,$player->z),$i);
                    }else{
                        $player->getInventory()->addItem($i);
                    }
                }
            }catch (\Exception $e){
                if($player instanceof Player){
                    $player->sendMessage(" 抱歉，由于服主的问题导致奖励不能正常给予");
                    break;
                }
            }
            $player->sendMessage("§e恭喜 §b你获得了 §7".getItem_Name::getName($Item[0].":".$Item[1])."§e* §d".$Item[2]);
        }
        foreach ($testArray["指令"] as $cmd){
            if($player instanceof Player){
                if(!$player->isOp()){
                    $this->getServer()->addOp($player->getName());
                    $cmd = str_replace("@p",$player->getName(),$cmd["cmd"]);
                    $this->getServer()->dispatchCommand($player,$cmd);
                    $this->getServer()->removeOp($player->getName());
                }else{
                    $cmd = str_replace("@p",$player->getName(),$cmd["cmd"]);
                    $this->getServer()->dispatchCommand($player,$cmd);
                }
            }
        }

    }

    public static function getStar(int $count):?string {
        $Star = "";
        for ($c = 0;$c < $count;$c++){
            $Star .= " ★ ";
        }
        return $Star;
    }
    public function register(){
        $this->getServer()->getPluginManager()->registerEvents(new BlockBreakEvent($this),$this);
        $this->getServer()->getPluginManager()->registerEvents(new BlockPlaceEvent($this),$this);
        $this->getServer()->getPluginManager()->registerEvents(new CraftEvent($this),$this);
        $this->getServer()->getPluginManager()->registerEvents(new EntityInventoryEvent($this),$this);
        $this->getServer()->getPluginManager()->registerEvents(new PlayerIntEvent($this),$this);
        $this->getServer()->getPluginManager()->registerEvents(new ItemConsumeEvent($this),$this);
        $this->getServer()->getPluginManager()->registerEvents(new JoinEvent($this),$this);
        $this->getServer()->getPluginManager()->registerEvents(new PlayerBucketEvent($this),$this);
        //$this->getServer()->getPluginManager()->registerEvents(new PlayerFishEvent($this),$this);
        //$this->getServer()->getPluginManager()->registerEvents(new PlayerUseEvent($this),$this);
        $this->getServer()->getPluginManager()->registerEvents(new PlayerDropEvent($this),$this);

    }



    public function ItemIn_arrayPlayerInventory(Item $item,Player $player){
        $count = 0;
        foreach ($player->getInventory()->getContents() as $items){
            if($items->getId() == $item->getId() && $items->getDamage() == $item->getDamage()){
                $count += $items->getCount();
            }
        }
        $item->setCount($count);
       return $item;
    }

    //获取所有任务
    public function getTaskList():?array {
        $array = [];
        $dir = $this->getDataFolder() . "/Test/";
        $dir_list = scandir($dir);
        foreach ($dir_list as $l) {
            $name = explode('.yml',$l);
            if ($name[0] != '.' && $name[0] != '..') {
                $array[] = $name[0];
            }
        }
        return $array;
    }
    //获取所有玩家
    public function getPlayerList():?array {
        $array = [];
        $dir = $this->getDataFolder() . "/Player/";
        $dir_list = scandir($dir);
        foreach ($dir_list as $l) {
            $name = explode('.yml',$l);
            if ($name[0] != '.' && $name[0] != '..') {
                $array[] = $name[0];
            }
        }
        return $array;
    }
    public function onCommand(CommandSender $sender, Command $command, string $label, array $args): bool
    {
        if($sender instanceof Player){
            switch($command->getName()){
                case "c":
                    sendUI::getInstance()->sendMenuChose($sender);
                    break;
                case "ic":
                    if($sender->isOp()){
                        sendUI::getInstance()->sendAddTestUI($sender);
                    }
                    break;
                case "fix-task":
                    if($sender->isOp()){
                        $this->fixTask();

                        $sender->sendMessage("§b所有玩家的任务内容已更新!!");
                    }
                    break;
                case "tell-player":
                    if($sender->isOp()){
                        $sender->sendMessage($args[0]);
                    }
                    break;
            }

        }
        return true;
    }
    //修复玩家任务状态
    public function fixTask():void{
        foreach($this->getPlayerList() as $playerName){
            //检测玩家文件是否存在未知的任务
            $playerArray = $this->getPlayerConfig($playerName)->getAll();
            foreach ($playerArray as $tasks=>$value){
                if(!in_array($tasks,$this->getTaskList())){
                    unset($playerArray[$tasks]);
                    $conf = $this->getPlayerConfig($playerName);
                    $conf->setAll($playerArray);
                    $conf->save();
                }
            }
        }
        foreach ($this->getTaskList() as $task){
            //所有任务
            $taskArray = $this->getTestConfig($task)->getAll();
            foreach ($this->getPlayerList() as $playerName){
                try{
                    if(isset($playerArray[$task])){
                        $c = 0;
                        foreach ($taskArray["完成条件"] as $item){
                            if(isset($playerArray[$task]["Item"][$item]))
                            {
                                $c++;
                            }
                        }
                        if($c != count($taskArray["完成条件"]) &&
                            count($playerArray[$task]["Item"]) != count($taskArray["完成条件"])){
                            unset($playerArray[$task]);
                            $array = self::$defaultPlayer;
                            foreach ($taskArray["完成条件"] as $item){
                                $array["Item"][$item] = 0;
                            }
                            $playerArray[$task] = $array;
                            $conf = $this->getPlayerConfig($playerName);
                            $conf->setAll($playerArray);
                            $conf->save();
                        }
                    }
                }catch(\Exception $e){
                    unlink($this->getDataFolder() . "/Test/".$task.".yml");
                    $this->getLogger()->warning("检测到".$task."文件错误，现已删除");
                    $this->fixTask();
                }
            }
        }
    }

    //根据难度返回点击的任务
    public function getClickByStar(Player $player,int $data):?string {
        $star = $this->star[$player->getName()];
        $i = 0;
        foreach ($this->getTaskList() as $task){
            $con = $this->getTestConfig($task)->getAll();
            if($con["难度"] == $star){
                if($i == $data)
                    return $task;
                $i++;
            }
        }
        return null;
    }
    //根据难度,积分返回是否解锁
    public function getTaskLevel(int $star,int $int):bool{
        if($this->getConf()->get("是否开启积分验证") != true) return true;
        $taskLevel = $this->getConf()->getAll();
        $string = self::$level[$star-1];
        if($taskLevel["任务等级系统"][$string] <= $int){
            return true;
        }
        return false;

    }
    public function if_Success_All($player,int $star):bool{
        //TODO 判断该难度任务是否全部完成
        $playerConfig = $this->getPlayerConfig($player)->getAll();
        $count = 0;
        $playerCount = 0;
        foreach ($this->getTaskList() as $task){
            $taskConfig = $this->getTestConfig($task)->getAll();
            if($taskConfig["难度"] == $star){
                if(isset($playerConfig[$task])){
                    if($playerConfig[$task]["完成次数"] > 0)
                        $playerCount++;
                }
                $count++;
            }
        }
        if($count == $playerCount && $count != 0)
            return true;
        return false;
    }
    private static function is_php( $version = '7.2.0' ) {
        $php_version = explode( '-', phpversion() );
        $is_pass = strnatcasecmp( $php_version[0], $version ) >= 0 ? true : false;
        return $is_pass;
    }
}