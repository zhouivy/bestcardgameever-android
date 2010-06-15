package com.geekadoo.ui;

/**
 * This is the "main" screen of the game, where the user can start a game
 * resume a game, etc
 * @author Elad & Sivan
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geekadoo.R;
import com.geekadoo.R.id;
import com.geekadoo.logic.GameData;


public class MainScreen extends Activity {

	Button startBtn;
	Button resumeBtn;
	Button tutorialBtn;
	Button exitBtn;
	
	public MainScreen() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		startBtn = (Button)findViewById(id.StartButton);
		resumeBtn = (Button)findViewById(id.ResumeButton);
		tutorialBtn = (Button)findViewById(id.TutorialButton);
		exitBtn = (Button)findViewById(id.ExitButton);

		startBtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				startYanivHandler();
			}
		});

		resumeBtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				resumeYanivHandler();
			}
		});

		tutorialBtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				System.out.println("tutorialBtn - onClick");
				//TODO:implement
			}
		});

		exitBtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				System.out.println("exitBtn - onClick");
				//TODO:implement
			}
		});
}

	protected void startYanivHandler() {
		
		// Create an intent 
		Intent yanivIntent = new Intent(this, Yaniv.class);
		// Transfer information to the yaniv (such as state and game attributes)
		yanivIntent.putExtra(GameData.STATE, GameData.GAME_STATES.start);
		
		startActivityForResult(yanivIntent, GameData.START_GAME);
	}
	
	protected void resumeYanivHandler() {
		// Create an intent 
		Intent yanivIntent = new Intent(this, Yaniv.class);
		yanivIntent.putExtra(GameData.STATE, GameData.GAME_STATES.resume);
		startActivityForResult(yanivIntent, GameData.RESUME_GAME);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO:Just testing to see we can pass data within activities
		TextView title = (TextView)findViewById(id.MainScreenTitle);
//		title.setText(gameData.)

		
		//		// TODO:Check what is the reason the Yaniv activity was finished (exit, end of game?)
//		super.onActivityResult(requestCode, resultCode, data);
//		
//		Log.d("PUKI", ""+data.getExtras().getInt("int"));
//		GameData gameData = (GameData)data.getExtras().getSerializable(GameData.GAMEDATA_PARAMNAME);
//		Log.d("PUKI", "gameData.isGameInProgress() = " + gameData.isGameInProgress());
//		Log.d("PUKI", "GameData.getInstance().isGameInProgress() = " + GameData.getInstance().isGameInProgress());
//		switch (requestCode) {
//		case GameData.START_GAME:
//			System.out.println("returned from a new game");
//			break;
//		case GameData.RESUME_GAME:
//			System.out.println("returned from a resumed game");
//			break;
//
//		default:
//			break;
//		}
	}
}