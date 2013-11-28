package chat.client.gui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;

import chat.client.agent.ChatClientAgent;
import chat.client.util.LogKey;
import chat.client.util.SessionSharedPreferences;
import chat.client.util.SharedPreferencesKey;
import jade.android.AndroidHelper;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.main)
public class MainActivity extends RoboActivity implements OnClickListener{

    private MicroRuntimeServiceBinder microRuntimeServiceBinder;
    private ServiceConnection serviceConnection;

    static final int CHAT_REQUEST = 0;
    static final int SETTINGS_REQUEST = 1;

    private MyReceiver myReceiver;
    private MyHandler myHandler;

    private String nickname;
    private ProgressDialog progressDialog;

    @InjectView(R.id.button_chat) private Button button;
    @InjectView(R.id.edit_nickname) private EditText nameField;
    @InjectView(R.id.acceptTerms) private CheckBox acceptTermsCheckBox;
    @InjectView(R.id.useTermsLink) private TextView useTermsLinkLabel;
    @Inject private SessionSharedPreferences sessionSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();

        IntentFilter killFilter = new IntentFilter();
        killFilter.addAction("jade.demo.chat.KILL");
        registerReceiver(myReceiver, killFilter);

        IntentFilter showChatFilter = new IntentFilter();
        showChatFilter.addAction("jade.demo.chat.SHOW_CHAT");
        registerReceiver(myReceiver, showChatFilter);

        myHandler = new MyHandler();
        button.setOnClickListener(buttonChatListener);
        useTermsLinkLabel.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nickname = sessionSharedPreferences.getString(SharedPreferencesKey.NICKNAME);
        if (nickNameIsValid(nickname)){
            nameField.setText(nickname);
            acceptTermsCheckBox.setChecked(true);
//            startChat(nickname, agentStartupCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
        Log.i(LogKey.INFO, "Destroy activity!");
    }

    private static boolean nickNameIsValid(String name) {
        if (name == null || name.trim().length() == 0) {
            return false;
        }
        // FIXME: should also check that name is composed
        // of letters and digits only
        return true;
    }

    private OnClickListener buttonChatListener = new OnClickListener() {
        public void onClick(View v) {
            nickname = nameField.getText().toString();
            if (!nickNameIsValid(nickname)) {
                Log.w(LogKey.INFO, "Invalid nickname!");
                myHandler.postError(getString(R.string.msg_nickname_not_valid));
            }else if (!acceptTermsCheckBox.isChecked()){
                Log.w(LogKey.INFO, "Accept Terms");
                myHandler.postError(getString(R.string.msg_please_accept_terms));
            } else {
                sessionSharedPreferences.addString(SharedPreferencesKey.NICKNAME, nickname);
                try {
                    startChat(nickname, agentStartupCallback);
                } catch (Exception ex) {
                    Log.e(LogKey.INFO, "Unexpected exception creating chat agent!");
                    // alert
                    progressDialog.dismiss();
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent showSettings = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivityForResult(showSettings, SETTINGS_REQUEST);
                return true;
            case R.id.menu_exit:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHAT_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                // The chat activity was closed.
                //stop progress
                progressDialog.dismiss();
                Log.i(LogKey.INFO, "Stopping Jade...");
                microRuntimeServiceBinder.stopAgentContainer(new RuntimeCallback<Void>() {
                    @Override
                    public void onSuccess(Void thisIsNull) {
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i(LogKey.INFO, "Failed to stop the " + ChatClientAgent.class.getName() + "...");
                        agentStartupCallback.onFailure(throwable);
                    }
                });
            }
        }
    }

    private RuntimeCallback<AgentController> agentStartupCallback = new RuntimeCallback<AgentController>() {

        @Override
        public void onSuccess(AgentController agent) {
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.i(LogKey.INFO, "Nickname already in use!");
            myHandler.postError(getString(R.string.msg_nickname_in_use));
            progressDialog.dismiss();
        }

        @Override
        public void notifyFailure(Logger logger, Throwable throwable) {
            super.notifyFailure(logger, throwable);
            progressDialog.dismiss();
        }
    };

    public void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == useTermsLinkLabel.getId()){
            Intent intent = new Intent(this, TermsActivity.class);
            startActivity(intent);
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(LogKey.INFO, "Received intent " + action);
            if (action.equalsIgnoreCase("jade.demo.chat.KILL")) {
                finish();
            }
            if (action.equalsIgnoreCase("jade.demo.chat.SHOW_CHAT")) {
                Intent showChat = new Intent(MainActivity.this,	ChatActivity.class);
                showChat.putExtra("nickname", nickname);
                MainActivity.this.startActivityForResult(showChat, CHAT_REQUEST);
            }
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if (bundle.containsKey("error")) {
                String message = bundle.getString("error");
                showDialog(message);
            }
        }

        public void postError(String error) {
            Message msg = obtainMessage();
            Bundle b = new Bundle();
            b.putString("error", error);
            msg.setData(b);
            sendMessage(msg);
        }
    }

    public void startChat(final String nickname, final RuntimeCallback<AgentController> agentStartupCallback) {
        String host = sessionSharedPreferences.getString(SharedPreferencesKey.DEFAULT_HOST);
        String port = sessionSharedPreferences.getString(SharedPreferencesKey.DEFAULT_HOST);
        // progress dialog
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Conectando...");
        progressDialog.show();

        final Properties profile = new Properties();
        profile.setProperty(Profile.MAIN_HOST, host);
        profile.setProperty(Profile.MAIN_PORT, port);
        profile.setProperty(Profile.MAIN, Boolean.FALSE.toString());
        profile.setProperty(Profile.JVM, Profile.ANDROID);

        if (AndroidHelper.isEmulator()) {
            // Emulator: this is needed to work with emulated devices
            profile.setProperty(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
        } else {
            profile.setProperty(Profile.LOCAL_HOST,	AndroidHelper.getLocalIPAddress());
        }
        // Emulator: this is not really needed on a real device
        profile.setProperty(Profile.LOCAL_PORT, "2000");

        if (microRuntimeServiceBinder == null) {
            serviceConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className,	IBinder service) {
                    microRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;
                    Log.i(LogKey.INFO, "Gateway successfully bound to MicroRuntimeService");
                    startContainer(nickname, profile, agentStartupCallback);
                };

                public void onServiceDisconnected(ComponentName className) {
                    microRuntimeServiceBinder = null;
                    Log.i(LogKey.INFO, "Gateway unbound from MicroRuntimeService");
                    progressDialog.dismiss();
                }
            };
            Log.i(LogKey.INFO, "Binding Gateway to MicroRuntimeService...");
            bindService(new Intent(getApplicationContext(), MicroRuntimeService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            Log.i(LogKey.INFO, "MicroRumtimeGateway already binded to service");
            startContainer(nickname, profile, agentStartupCallback);
        }
    }

    private void startContainer(final String nickname, Properties profile, final RuntimeCallback<AgentController> agentStartupCallback) {
        if (!MicroRuntime.isRunning()) {
            microRuntimeServiceBinder.startAgentContainer(profile,
                    new RuntimeCallback<Void>() {
                        @Override
                        public void onSuccess(Void thisIsNull) {
                            Log.i(LogKey.INFO, "Successfully start of the container...");
                            startAgent(nickname, agentStartupCallback);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.i(LogKey.INFO, "Failed to start the container...");
                        }
                    });
        } else {
            startAgent(nickname, agentStartupCallback);
        }
    }

    private void startAgent(final String nickname, final RuntimeCallback<AgentController> agentStartupCallback) {
        microRuntimeServiceBinder.startAgent(nickname,
                ChatClientAgent.class.getName(),
                new Object[] { getApplicationContext() },
                new RuntimeCallback<Void>() {
                    @Override
                    public void onSuccess(Void thisIsNull) {
                        Log.i(LogKey.INFO, "Successfully start of the " + ChatClientAgent.class.getName() + "...");
                        try {
                            agentStartupCallback.onSuccess(MicroRuntime.getAgent(nickname));
                        } catch (ControllerException e) {
                            // Should never happen
                            agentStartupCallback.onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i(LogKey.INFO, "Failed to start the " + ChatClientAgent.class.getName() + "...");
                        agentStartupCallback.onFailure(throwable);
                    }
                });
    }

}
