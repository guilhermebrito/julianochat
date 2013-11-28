/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

package chat.client.gui;

import java.util.logging.Level;

import chat.client.util.SharedPreferencesKey;
import jade.util.Logger;
import android.app.Application;
import android.content.SharedPreferences;

/**
 * This is the Android Chat Demo Application.
 * 
 * @author Michele Izzo - Telecomitalia
 */

public class ChatApplication extends Application {
	private Logger logger = Logger.getJADELogger(this.getClass().getName());

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences settings = getSharedPreferences("jadeChatPrefsFile", 0);
		String defaultHost = settings.getString(SharedPreferencesKey.DEFAULT_HOST.name(), "");
		String defaultPort = settings.getString(SharedPreferencesKey.DEFAULT_PORT.name(), "");
		if (defaultHost.isEmpty() || defaultPort.isEmpty()) {
			logger.log(Level.INFO, "Create default properties");
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(SharedPreferencesKey.DEFAULT_HOST.name(), "eyso.com.br");
			editor.putString(SharedPreferencesKey.DEFAULT_PORT.name(), "1099");
			editor.commit();
		}
	}
}
