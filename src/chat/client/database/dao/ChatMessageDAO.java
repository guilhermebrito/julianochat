package chat.client.database.dao;

import android.content.ContentValues;
import android.database.Cursor;

import chat.client.database.model.ChatMessage;
import chat.client.database.model.Model;

/**
 * Created by desenv on 23/10/13.
 */
public class ChatMessageDAO extends DAO<ChatMessage>{

    private static final String TABLE_NAME = "chatmessage";

    private static final String[] COLUMNS = new String[]{
            ChatMessage._ID,
            ChatMessage.MY_MESSAGE,
            ChatMessage.MESSAGE_TYPE,
            ChatMessage.NICKNAME,
            ChatMessage.MESSAGE,
            ChatMessage.INPUT_DATE
    };

    private ContentValues values;

    public ChatMessageDAO(){
        super(TABLE_NAME, COLUMNS);
        values = new ContentValues();
    }

    protected ContentValues createValues(Model model) {
        ChatMessage chatMessage = (ChatMessage) model;
        values.put(ChatMessage.MY_MESSAGE, chatMessage.isMyMessage() ? 1 : 0);
        values.put(ChatMessage.MESSAGE_TYPE, chatMessage.getMessageType());
        values.put(ChatMessage.NICKNAME, chatMessage.getNickName());
        values.put(ChatMessage.MESSAGE, chatMessage.getMessage());

        return values;
    }

    public ChatMessage createModel(Cursor cursor) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(cursor.getLong(getColumnIndex(ChatMessage._ID)));
        chatMessage.setMessageType(cursor.getString(getColumnIndex(ChatMessage.MESSAGE_TYPE)));
        chatMessage.setNickName(cursor.getString(getColumnIndex(ChatMessage.NICKNAME)));
        chatMessage.setMessage(cursor.getString(getColumnIndex(ChatMessage.MESSAGE)));
        boolean myMessage = cursor.getInt(getColumnIndex(ChatMessage.MY_MESSAGE)) == 1 ? true : false;
        chatMessage.setMyMessage(myMessage);
        chatMessage.setInputDate(cursor.getString(getColumnIndex(ChatMessage.INPUT_DATE)));
        return chatMessage;
    }
}
