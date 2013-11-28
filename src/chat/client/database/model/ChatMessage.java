package chat.client.database.model;

import java.io.Serializable;
import java.util.Date;

import chat.client.util.DateUtil;

/**
 * Created by desenv on 23/10/13.
 */
public class ChatMessage implements Model, Serializable {

    private static final long serialVersionUID = -2847019722293952333L;
    public static final String MY_MESSAGE = "my";
    public static final String NICKNAME = "nickname";
    public static final String MESSAGE_TYPE = "messagetype";
    public static final String MESSAGE = "message";
    public static final String INPUT_DATE = "inputdate";

    private long id;
    private boolean myMessage;
    private String nickName;
    private String messageType;
    private String message;
    public Date inputDate;

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMyMessage() {
        return myMessage;
    }

    public void setMyMessage(boolean myMessage) {
        this.myMessage = myMessage;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getInputDateString() {
        return DateUtil.convertToDBFormat(inputDate);
    }

    public void setInputDate(String dateString) {
        this.inputDate = DateUtil.convertDBFormat(dateString);
    }

    public void setInputDate() {
        this.inputDate = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessage)) return false;

        ChatMessage that = (ChatMessage) o;

        if (id != that.id) return false;
        if (myMessage != that.myMessage) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (messageType != null ? !messageType.equals(that.messageType) : that.messageType != null)
            return false;
        if (nickName != null ? !nickName.equals(that.nickName) : that.nickName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (myMessage ? 1 : 0);
        result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
        result = 31 * result + (messageType != null ? messageType.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
