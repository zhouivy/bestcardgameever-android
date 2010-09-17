package com.geekadoo.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.geekadoo.R;
import com.geekadoo.R.id;
import com.geekadoo.db.YanivPersistenceAdapter;
import com.geekadoo.logic.GameData;

public class StartNewGameDialog extends Dialog implements
		android.view.View.OnClickListener {

	Button okButton;
	Button cancelButton;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Buttons
		okButton = (Button) findViewById(id.startNewGameDialogOkButton);
		okButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(id.startNewGameDialogCancelButton);
		cancelButton.setOnClickListener(this);
		// Settings
		SharedPreferences settings = context.getSharedPreferences(Yaniv.PREFS_NAME, 0);
		// PlayerName
		EditText pName = (EditText)findViewById(R.id.PlayerNameEt);
		pName.setText(settings.getString(Yaniv.PREFS_PLAYER_NAME_PROPERTY, context.getString(R.string.pNameDefVal)));
		// Game in progress
		TextView gameInProgressTv = (TextView)findViewById(id.GameExistsTv);
		if (YanivPersistenceAdapter.isSavedGameDataValid(context)) {
			gameInProgressTv.setText(R.string.gameAlreadyInProgress);
		}else{
			gameInProgressTv.setText("");
		}
	}

	public StartNewGameDialog(Context context) {
		super(context);
		this.context = context;

		this.setTitle(R.string.startNewGameDialogHeading);
		setContentView(R.layout.start_new_game_view);

		// Have the system blur any windows behind this one.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	    getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startNewGameDialogOkButton:
			startNewGame();
			dismiss();
			break;
		case R.id.startNewGameDialogCancelButton:
			dismiss();
			break;
		default:
			break;
		}
	}

	private void startNewGame() {
		// Create an intent
		Intent yanivIntent = new Intent(context, Yaniv.class);
		// Transfer information to the yaniv (such as state and game attributes)
		yanivIntent.putExtra(GameData.STATE, GameData.GAME_STATES.start);
		yanivIntent.putExtra(GameData.PLAYER_NAME,((EditText)findViewById(R.id.PlayerNameEt)).getText().toString() );

		((Activity) context).startActivityForResult(yanivIntent, GameData.START_GAME);
		
	}

}