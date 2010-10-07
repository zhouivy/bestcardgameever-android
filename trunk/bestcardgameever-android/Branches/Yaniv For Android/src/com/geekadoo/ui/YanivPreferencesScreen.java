package com.geekadoo.ui;

import com.geekadoo.R;
import com.scoreloop.client.android.core.controller.UserController;
import com.scoreloop.client.android.core.model.Session;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

public class YanivPreferencesScreen extends PreferenceActivity {
	private static final String LOG_TAG = "YanivPreferencesScreen";
	private MyUserControllerObserver myUserControllerObserver;
	private UserController myUserController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.yaniv_preferences);
		
        PreferenceScreen screen = getPreferenceScreen();
        EditTextPreference userNameETP = (EditTextPreference) screen.findPreference(getString(R.string.highscoreUserNamePref));
        userNameETP.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if(((String)newValue).length()<6){
					// show a toast that says "user name must be 6 chars and up"
					Toast toast = Toast.makeText(YanivPreferencesScreen.this,
							R.string.highscoreUserNameTooShort,
							Toast.LENGTH_LONG);
					toast.show();
					return false;
				}
				setScoreloopUserLogin((String)newValue);
				return true;
			}
		});
        
        myUserControllerObserver = new MyUserControllerObserver(this);
		myUserController = new UserController(myUserControllerObserver);
		myUserController.loadUser();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		boolean retval = false;
		if(preference.getKey().equals(getString(R.string.enableSlPref))){
			if(((CheckBoxPreference)preference).isChecked()){
				setScoreloopUserLoginAccordingToPrefs();
			}
			retval = true;
		}
		return retval;
	}
	
	private void setScoreloopUserLoginAccordingToPrefs() {

		
		if (myUserControllerObserver.gotHandshake()) {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
			final String name = settings.getString(
					getString(R.string.highscoreUserNamePref),
					getString(R.string.pNameDefVal));
			setScoreloopUserLogin(name);
		}else{
			Log.e(LOG_TAG,"UserControllerObserver did not get handshake...");
		}

	}

	private void setScoreloopUserLogin(final String name) {
		Session.getCurrentSession().getUser().setLogin(name);
		myUserController.submitUser();
	}
}
