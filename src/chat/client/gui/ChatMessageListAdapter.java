package chat.client.gui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import chat.client.database.model.ChatMessage;
import chat.client.database.model.ChatMessageType;
import chat.client.util.LogKey;

/**
 * Created by desenv on 02/10/13.
 */
public class ChatMessageListAdapter extends BaseAdapter {

    private Context context;
    private List<ChatMessage> chatList;

    private ImageView imageState;


    public ChatMessageListAdapter(Context context, List<ChatMessage> chatList){
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public int getCount() {
        Log.i(LogKey.INFO, "Size ->" + chatList.size());
        return chatList.size();
    }

    @Override
    public ChatMessage getItem(int index) {
        return chatList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ImageView getImageState() {
        return imageState;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.layout_chat_ballon, null);
        }
        LinearLayout logged = (LinearLayout) view.findViewById(R.id.logLayout);
        LinearLayout chatDriver = (LinearLayout) view.findViewById(R.id.chatDriver);
        RelativeLayout chatCustomer = (RelativeLayout) view.findViewById(R.id.chat);
        ChatMessage chatMessage = getItem(position);
        if(chatMessage.isMyMessage()){
            chatDriver.setVisibility(View.GONE);
            chatCustomer.setVisibility(View.VISIBLE);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView chatMsg = (TextView) view.findViewById(R.id.chatMsg);
            ImageView imageStatus = (ImageView) view.findViewById(R.id.imageState);
            chatMsg.setText(chatMessage.getMessage());
//            date.setText(chatMessage.getHour());
            date.setText(chatMessage.getInputDateString());
            imageStatus.setVisibility(View.VISIBLE);
//            if(chatMessage.isSent()){
            imageStatus.setImageResource(R.drawable.chat_sent);
//            }else {
//                imageStatus.setImageResource(R.drawable.chat_waiting);
//            }
//            imageStatus.setImageResource(R.drawable.chat_waiting);
        }else {
            if (ChatMessageType.MESSAGE.equals(chatMessage.getMessageType())){
                chatDriver.setVisibility(View.VISIBLE);
                chatCustomer.setVisibility(View.GONE);
                TextView date = (TextView) view.findViewById(R.id.driverChatDate);
                TextView chatMsg = (TextView) view.findViewById(R.id.chatDriverMsg);
                TextView chatFrom  = (TextView) view.findViewById(R.id.chatFrom);
                chatMsg.setText(chatMessage.getMessage());
                date.setText(chatMessage.getInputDateString());
                chatFrom.setText(chatMessage.getNickName());
            }else {
                logged.setVisibility(View.VISIBLE);
                chatDriver.setVisibility(View.GONE);
                chatCustomer.setVisibility(View.GONE);
                TextView log  = (TextView) view.findViewById(R.id.log);
                if (ChatMessageType.LOGIN.equals(chatMessage.getMessageType())){
                    log.setText(chatMessage.getNickName() + " entrou");
                }else if (ChatMessageType.LOGOUT.equals(chatMessage.getMessageType())){
                    log.setText(chatMessage.getNickName() + " saiu");
                }

            }
        }
        Log.i(LogKey.INFO, "->" + position + "-" + chatMessage.toString());
        return view;
    }

    public void update(List<ChatMessage> messages) {
        Log.i(LogKey.INFO, "-> UPDATE mensagens ..." + messages.size());
        this.chatList.clear();
        this.chatList.addAll(messages);
        notifyDataSetChanged();
        Log.i(LogKey.INFO, "-> UPDATE ChatList ..." + chatList.size());
    }
}
