package com.geekadoo.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.geekadoo.R;
import com.geekadoo.R.id;
import com.geekadoo.utils.MutableSoundManager;

public class SettingsDialog extends Dialog implements
		android.view.View.OnClickListener {

	Button okButton;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		okButton = (Button) findViewById(id.settingsDialogOkButton);
		okButton.setOnClickListener(this);
		// Settings
		SharedPreferences settings = context.getSharedPreferences(
				Yaniv.PREFS_NAME, 0);
		// Sound
		final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.soundToggle);
		boolean silent = settings.getBoolean(
				MutableSoundManager.SILENT_MODE_PROPERTY, false);

		togglebutton.setChecked(silent);
		togglebutton
				.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// We need an Editor object to make preference changes.
						// All objects are from android.context.Context
						SharedPreferences settings = context
								.getSharedPreferences(Yaniv.PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putBoolean(
								MutableSoundManager.SILENT_MODE_PROPERTY,
								isChecked);
						// Commit the edits!
						editor.commit();
					}
				});
		// PlayerName
		EditText pName = (EditText) findViewById(R.id.PlayerNameEt);
		pName.setText(settings.getString(Yaniv.PREFS_PLAYER_NAME_PROPERTY,
				context.getString(R.string.pNameDefVal)));

	}

	public SettingsDialog(Context context) {
		super(context);
		this.context = context;

		this.setTitle(R.string.settingsDialogHeading);
		setContentView(R.layout.settings_view);

		// Have the system blur any windows behind this one.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.settingsDialogOkButton:
			saveSettings();
			dismiss();
			break;
		default:
			break;
		}
	}

	private void saveSettings() {
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = context.getSharedPreferences(
				Yaniv.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Yaniv.PREFS_PLAYER_NAME_PROPERTY,
				((EditText) findViewById(R.id.PlayerNameEt)).getText()
						.toString());
		// Commit the edits!
		editor.commit();

	}
}