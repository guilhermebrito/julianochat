package chat.client.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.inject.Inject;

public class SessionSharedPreferences {

	private static final String SESSION = "jadeChatPrefsFile";
	private Context context;

	@Inject
	public SessionSharedPreferences(Context context){
		this.context = context;
	}
	
	public void addBoolean(SharedPreferencesKey key, boolean value){
		SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(key.name(), value);
		editor.commit();
	}
	
	public boolean getBoolean(SharedPreferencesKey key){
		SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
		return preferences.getBoolean(key.name(), false);
	}

    public void addString(SharedPreferencesKey key, String value){
        SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key.name(), value);
        editor.commit();
    }

    public String getString(SharedPreferencesKey key){
		SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
		return preferences.getString(key.name(), null);
	}

	public void remove(SharedPreferencesKey key){
		SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.remove(key.name());
		editor.commit();
	}
}
