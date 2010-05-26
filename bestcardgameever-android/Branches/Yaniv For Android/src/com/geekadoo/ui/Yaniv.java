package com.geekadoo.ui;

/*
 * TODO: 
 * GameComponents need to be initialized from MainScreen activity, so it will not "die"
 * when Yaniv activity dies...
 * The separation we performed for init() is not sufficient.
 * we need to separate the graphics components from the hand object and then pass it an adapter upon 
 * initialization of graphic elements or keep the entire thing outside this activity and use it 
 */

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.geekadoo.R;
import com.geekadoo.R.id;
import com.geekadoo.db.YanivPersistenceAdapter;
import com.geekadoo.exceptions.YanivPersistenceException;
import com.geekadoo.logic.GameData;

/**
 * The core activity of the application
 * @author Elad
 */

public class Yaniv extends Activity {
	
	private static final String YANIV_TAG = "Yaniv";
	private GameData gameData;
	private boolean firstRun;
	private YanivPersistenceAdapter persistencAdapter;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		
		// Load state given by MainScreen
		switch ((GameData.STATES)getIntent().getExtras().get(GameData.STATE)) {
		case start:
			firstRun = true;
			break;
		case resume:
			firstRun = false;
			break;
		default:
			firstRun = true;
			break;
		}
		
		persistencAdapter = new YanivPersistenceAdapter(getApplicationContext());

		gameData = (savedInstanceState == null) ? null :
            (GameData) savedInstanceState.getSerializable(YanivPersistenceAdapter.GAME_DATA);
        if (gameData == null) {
        	if(!firstRun){
        		// existing game, read it from the persistence provider
        		try{
        			gameData = persistencAdapter.getSavedGameData();
        		}catch (YanivPersistenceException e) {
        			//TODO: popup sorry dialog here - could not load saved file, creating new file
        			gameData = GameData.createNewGame();
        		}
        	}else{
        		gameData = GameData.createNewGame();
        	}
        }
		// Just testing to see we can pass data within activities
		final TextView title = (TextView)findViewById(id.titleTV);
		title.setText((CharSequence)(gameData.getTestInt().toString()));
		Button btn = (Button)findViewById(id.promoteBtn);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				title.setText(gameData.promoteTestInt().toString());
			}
		});
		//End Test
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		populateGameData();
	}



	@Override
	protected void onStop() {
		super.onStart();
		Log.e(YANIV_TAG,"OnStop");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		System.out.println("new config, orientation: " + newConfig.orientation );
		
		if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES){
			System.out.println("Keyboard open");
		}
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			System.out.println("Orientation Landscape");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(YanivPersistenceAdapter.GAME_DATA, gameData);

	}

	private void saveState() {
		try{
			persistencAdapter.setSavedGameData(gameData);
		}catch (YanivPersistenceException e) {
			// TODO: popup a sorry box and report this problem... - could not save
		}
	}
	private void populateGameData() {
		try{
			gameData = persistencAdapter.getSavedGameData();
		}catch (YanivPersistenceException e) {
			// TODO: popup a sorry box and report this problem... - could not load, creating new.
			gameData = GameData.createNewGame();
		}
	}	
	
}