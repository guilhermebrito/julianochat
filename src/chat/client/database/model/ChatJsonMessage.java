package chat.client.database.model;

import com.google.gson.Gson;

import java.util.Date;

import chat.client.util.DateUtil;

/**
 * Created by desenv on 10/11/13.
 */
public class ChatJsonMessage {

    public String nickname;
    public String type;
    public String message;
    public String inputdate;

    public String toGSON(){
        return new Gson().toJson(this);
    }

    public void setNow() {
        this.inputdate = DateUtil.convertToDBFormat(new Date());
    }
}
