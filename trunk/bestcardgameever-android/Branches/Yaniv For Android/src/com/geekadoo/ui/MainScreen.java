package com.geekadoo.ui;

/**
 * This is the "main" screen of the game, where the user can start a game
 * resume a game, etc
 * @author Elad & Sivan
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.geekadoo.R;
import com.geekadoo.R.id;
import com.geekadoo.db.YanivPersistenceAdapter;
import com.geekadoo.logic.GameData;


public class MainScreen extends Activity {

	static protected final int MATCH_OVER = 1;
	private static final String LOG_TAG = "MainScreen";
	Button startBtn;
	Button resumeBtn;
	Button tutorialBtn;
	Button exitBtn;
	private boolean isResumable;
	
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
		if(!YanivPersistenceAdapter.isSavedGameDataValid(getApplicationContext())){
			resumeBtn.setEnabled(false);
		}
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
				System.exit(0);
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

	// Listen for results.
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	    // See which child activity is calling us back.
	    switch (resultCode) {
	        case MATCH_OVER:
	        	// Disable the resume button
	        	YanivPersistenceAdapter.deleteSavedGameData(getApplicationContext());
	        	resumeBtn.setEnabled(false);
	        	// get the data & populate high scores
	        	break;
	        case RESULT_CANCELED:
	        	// This is the standard resultCode that is sent back if the
	        	// activity crashed or didn't doesn't supply an explicit result.
	        	break;
	        default:
	            break;
	    }
	}

}