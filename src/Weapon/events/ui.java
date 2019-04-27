package Weapon.events;


import cn.nukkit.Player;
import cn.nukkit.network.protocol.ModalFormRequestPacket;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import net.minidev.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/*这个文件夹之后用作ui 目前没有必要实现
*
*
* */
public class ui {

    //ui发包机制
//    long id = 0;
//    HashMap<String,Object> data = new HashMap<>();
//     public void send(Player player){
//         ModalFormRequestPacket pk = new ModalFormRequestPacket();
//         pk.data = new GsonBuilder().setPrettyPrinting().create().toJson(data);
//         pk.formId = (int)id;
//         player.dataPacket(pk);
//     }
}
