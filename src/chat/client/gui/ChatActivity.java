package chat.client.gui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.inject.Inject;

import java.util.List;
import java.util.logging.Level;

import chat.client.agent.ChatClientInterface;
import chat.client.database.dao.ChatMessageDAO;
import chat.client.database.model.ChatJsonMessage;
import chat.client.database.model.ChatMessage;
import chat.client.database.model.ChatMessageType;
import jade.core.MicroRuntime;
import jade.util.Logger;
import jade.wrapper.ControllerException;
import jade.wrapper.O2AException;
import jade.wrapper.StaleProxyException;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.chat)
public class ChatActivity extends RoboActivity implements OnClickListener{

	private Logger logger = Logger.getJADELogger(this.getClass().getName());

	static final int PARTICIPANTS_REQUEST = 0;

    private String nickname;
    private MyReceiver myReceiver;
    private ChatMessageListAdapter chatMessageListAdapter;
    private ChatClientInterface chatClientInterface;

    @Inject private ChatMessageDAO chatMessageDAO;
    @InjectView(R.id.button_send)   private Button button;
    @InjectView(R.id.chatList)      private ListView chatList;
    @InjectView(R.id.edit_message)  private EditText messageField;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			nickname = extras.getString("nickname");
		}
		try {
			chatClientInterface = MicroRuntime.getAgent(nickname).getO2AInterface(ChatClientInterface.class);
		} catch (StaleProxyException e) {
			showAlertDialog(getString(R.string.msg_interface_exc), true);
		} catch (ControllerException e) {
			showAlertDialog(getString(R.string.msg_controller_exc), true);
		}

		myReceiver = new MyReceiver();
		IntentFilter refreshChatFilter = new IntentFilter();
		refreshChatFilter.addAction("jade.demo.chat.REFRESH_CHAT");
		registerReceiver(myReceiver, refreshChatFilter);

		IntentFilter clearChatFilter = new IntentFilter();
		clearChatFilter.addAction("jade.demo.chat.CLEAR_CHAT");
		registerReceiver(myReceiver, clearChatFilter);

		button.setOnClickListener(this);

        ChatJsonMessage jsonMessage = new ChatJsonMessage();
        jsonMessage.type = ChatMessageType.LOGIN;
        jsonMessage.nickname = nickname;
        jsonMessage.setNow();
        chatClientInterface.handleSpoken(jsonMessage.toGSON());
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<ChatMessage> all = chatMessageDAO.findAll();
        chatMessageListAdapter = new ChatMessageListAdapter(this, all);
        chatList.setAdapter(chatMessageListAdapter);
        chatList.requestFocus();
        scrollDown(all.size());
    }

    @Override
	protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
        logger.log(Level.INFO, "Destroy activity!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chat_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_participants:
			Intent showParticipants = new Intent(ChatActivity.this,	ParticipantsActivity.class);
			showParticipants.putExtra("nickname", nickname);
			startActivityForResult(showParticipants, PARTICIPANTS_REQUEST);
			return true;
		case R.id.menu_clear:
			/*
			Intent broadcast = new Intent();
			broadcast.setAction("jade.demo.chat.CLEAR_CHAT");
			logger.info("Sending broadcast " + broadcast.getAction());
			sendBroadcast(broadcast);
			*/
            messageField.setText("");
            chatMessageDAO.deleteAll();
            chatMessageListAdapter.update(chatMessageDAO.findAll());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PARTICIPANTS_REQUEST) {
			if (resultCode == RESULT_OK) {
				// TODO: A partecipant was picked. Send a private message.
			}
		}
	}

    @Override
    public void onClick(View v) {
       if (v.getId() == button.getId()){
           closeKeyBoard();
           String message = messageField.getText().toString();
           if (message != null && message.trim().length() > 0) {
               try {
//                    ChatMessage chatMessage = new ChatMessage();
//                    chatMessage.setMyMessage(true);
//                    chatMessage.setNickName("Eu");
//                    chatMessage.setMessageType(ChatMessageType.MESSAGE);
//                    chatMessage.setMessage(message);
//                    chatMessage.setInputDate();
//                    chatMessageDAO.save(chatMessage);
//                   chatMessageListAdapter.update(chatMessageDAO.findAll());
                   ChatJsonMessage jsonMessage = new ChatJsonMessage();
                   jsonMessage.type = ChatMessageType.MESSAGE;
                   jsonMessage.nickname = nickname;
                   jsonMessage.message = message;
                   jsonMessage.setNow();
                   chatClientInterface.handleSpoken(jsonMessage.toGSON());
                   messageField.setText("");
                } catch (O2AException e) {
                    showAlertDialog(e.getMessage(), false);
                }
            }

        }
    }

    private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			logger.log(Level.INFO, "Received intent " + action);
			if (action.equalsIgnoreCase("jade.demo.chat.REFRESH_CHAT")) {
                String sentence = intent.getExtras().getString("sentence");
                ChatJsonMessage jsonMessage = new Gson().fromJson(sentence, ChatJsonMessage.class);
                ChatMessage chatMessage = null;
                if (ChatMessageType.LOGIN.equals(jsonMessage.type) ||
                    ChatMessageType.LOGOUT.equals(jsonMessage.type)){
                    if (!nickname.equals(jsonMessage.nickname)){
                        chatMessage = new ChatMessage();
                        chatMessage.setNickName(jsonMessage.nickname);
                        chatMessage.setMessageType(jsonMessage.type);
                        chatMessage.setMessage(jsonMessage.type);
                        chatMessage.setInputDate(jsonMessage.inputdate);
                    }
                }else{
                    chatMessage = new ChatMessage();
                    if (nickname.equals(jsonMessage.nickname)){
                        chatMessage.setMyMessage(true);
                    }
                    chatMessage.setNickName(jsonMessage.nickname);
                    chatMessage.setMessageType(jsonMessage.type);
                    chatMessage.setMessage(jsonMessage.message);
                    chatMessage.setInputDate(jsonMessage.inputdate);
                }
                if (chatMessage != null){
                    chatMessageDAO.save(chatMessage);
                    List<ChatMessage> all = chatMessageDAO.findAll();
                    chatMessageListAdapter.update(all);
                    scrollDown(all.size());
                }
			}
			if (action.equalsIgnoreCase("jade.demo.chat.CLEAR_CHAT")) {
//				final TextView chatField = (TextView) findViewById(R.id.chatTextView);
//				chatField.setText("");
			}
		}
	}

	private void scrollDown(int chatListSize) {
        chatList.smoothScrollToPosition(chatListSize - 1);
//		final ScrollView scroller = (ScrollView) findViewById(R.id.scroller);
//		scroller.smoothScrollTo(0, chatList.getBottom());
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
//		final TextView chatField = (TextView) findViewById(R.id.chatTextView);
//		savedInstanceState.putString("chatField", chatField.getText().toString());
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
//		final TextView chatField = (TextView) findViewById(R.id.chatTextView);
//		chatField.setText(savedInstanceState.getString("chatField"));
	}

	private void showAlertDialog(String message, final boolean fatal) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog, int id) {
								dialog.cancel();
								if(fatal) finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();		
	}

    public void closeKeyBoard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageField.getWindowToken(), 0);
    }

}