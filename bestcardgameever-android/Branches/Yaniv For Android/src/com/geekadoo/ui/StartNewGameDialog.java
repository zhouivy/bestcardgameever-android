package com.geekadoo.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.geekadoo.R;
import com.geekadoo.R.id;
import com.geekadoo.db.YanivPersistenceAdapter;
import com.geekadoo.logic.GameData;
import com.geekadoo.logic.GameData.GameDifficultyEnum;

public class StartNewGameDialog extends Dialog implements
		android.view.View.OnClickListener {

	private static final String LOG_TAG = "StartNewGameDialog";
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
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		// PlayerName
		EditText pName = (EditText) findViewById(R.id.PlayerNameEt);
		pName.setText(settings.getString(context.getString(R.string.highscoreUserNamePref),
				context.getString(R.string.pNameDefVal)));
		// Game in progress
		TextView gameInProgressTv = (TextView) findViewById(id.GameExistsTv);
		if (YanivPersistenceAdapter.isSavedGameDataValid(context)) {
			gameInProgressTv.setText(R.string.gameAlreadyInProgress);
		} else {
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
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

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
		int checkedRadioButtonId = ((RadioGroup)findViewById(R.id.difficultyRG)).getCheckedRadioButtonId();
		GameData.GameDifficultyEnum diffEnum = null;
		switch(checkedRadioButtonId){
			case R.id.DifficultyEasy:
				diffEnum = GameDifficultyEnum.EASY;
				break;
			case R.id.DifficultyNormal:
				diffEnum = GameDifficultyEnum.NORMAL;
				break;
			case R.id.DifficultyHard:
				diffEnum = GameDifficultyEnum.HARD;
				break;
			default:
				Log.e(LOG_TAG, "no difficulty selected, selecting normal");
				diffEnum = GameDifficultyEnum.NORMAL;
				break;
		}
		
		// Create an intent
		Intent yanivIntent = new Intent(context, Yaniv.class);
		// Transfer information to the yaniv (such as state and game attributes)
		yanivIntent.putExtra(GameData.STATE, 
				GameData.GAME_STATES.start);
		yanivIntent.putExtra(GameData.PLAYER_NAME, 
				((EditText)findViewById(R.id.PlayerNameEt)).getText().toString() );
		yanivIntent.putExtra(GameData.DIFFICULTY_LEVEL,diffEnum);

		((Activity) context).startActivityForResult(yanivIntent, GameData.START_GAME);
		
	}
}