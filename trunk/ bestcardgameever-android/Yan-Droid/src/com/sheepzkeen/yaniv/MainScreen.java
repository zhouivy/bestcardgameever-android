package com.sheepzkeen.yaniv;

/**
 * This is the "main" screen of the game, where the user can start a game
 * resume a game, etc
 * @author Elad & Sivan
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sheepzkeen.yaniv.R.id;

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
		setContentView(R.layout.yaniv_main_screen);
		
		startBtn = (Button)findViewById(id.StartButton);
		resumeBtn = (Button)findViewById(id.ResumeButton);
		tutorialBtn = (Button)findViewById(id.TutorialButton);
		exitBtn = (Button)findViewById(id.ExitButton);

		startBtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				System.out.println("startBtn - onClick");
				startYanivHandler();
			}
		});

		resumeBtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				System.out.println("resumeBtn - onClick");
				resumeYanivHandler();
			}
		});

		tutorialBtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				System.out.println("tutorialBtn - onClick");
			}
		});

		exitBtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				System.out.println("exitBtn - onClick");
			}
		});
}

	protected void startYanivHandler() {
		GameData gameData = GameData.getInstance();
		
		// Create an intent 
		Intent yanivIntent = new Intent(this, Yaniv.class);
		// Transfer information to the yaniv (such as state and game attributes)
		yanivIntent.putExtra(Yaniv.STATE, Yaniv.STATES.start);
		yanivIntent.putExtra(Yaniv.GAMEDATA_PARAMNAME, gameData);
		
		startActivityForResult(yanivIntent, Yaniv.START_GAME);
	}
	protected void resumeYanivHandler() {
		// Create an intent 
		Intent yanivIntent = new Intent(this, Yaniv.class);
		yanivIntent.putExtra(Yaniv.STATE, Yaniv.STATES.resume);
		startActivityForResult(yanivIntent, Yaniv.RESUME_GAME);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO:Check what is the reason the Yaniv activity was finished (exit, end of game?)
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.d("PUKI", ""+data.getExtras().getInt("int"));
		GameData gameData = (GameData)data.getExtras().getSerializable(Yaniv.GAMEDATA_PARAMNAME);
		Log.d("PUKI", "gameData.isGameInProgress() = " + gameData.isGameInProgress());
		Log.d("PUKI", "GameData.getInstance().isGameInProgress() = " + GameData.getInstance().isGameInProgress());
		switch (requestCode) {
		case Yaniv.START_GAME:
			System.out.println("returned from a new game");
			break;
		case Yaniv.RESUME_GAME:
			System.out.println("returned from a resumed game");
			break;

		default:
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		Log.d("PUKI","MS_onCreate");

	return super.onCreateDialog(id);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("PUKI","MS_onDestroy");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("PUKI","MS_onPause");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("PUKI","MS_onResume");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("PUKI","MS_onStart");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("PUKI","MS_onStop");
	}
	
}