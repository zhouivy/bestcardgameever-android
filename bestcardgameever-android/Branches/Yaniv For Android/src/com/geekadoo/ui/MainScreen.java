package com.geekadoo.ui;

/**
 * This is the "main" screen of the game, where the user can start a game
 * resume a game, etc
 * @author Elad & Sivan
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.geekadoo.R;
import com.geekadoo.R.id;
import com.geekadoo.db.YanivPersistenceAdapter;
import com.geekadoo.logic.GameData;
import com.scoreloop.android.coreui.HighscoresActivity;
import com.scoreloop.android.coreui.ScoreloopManager;
import com.scoreloop.client.android.core.controller.UserController;
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.core.model.User;

public class MainScreen extends Activity {

	static protected final int MATCH_OVER = 1;
	private static final String LOG_TAG = "MainScreen";
	Button startBtn;
	Button resumeBtn;
	Button tutorialBtn;
	Button highScoreBtn;
	Button settingsBtn;
	Button bugBtn;
	Button exitBtn;
	private UserController myUserController;

	public MainScreen() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
        //evaluate if we will show changelog
	    try {
	        //current version
	        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
	        int versionCode = packageInfo.versionCode; 

	        //version where changelog has been viewed
	        SharedPreferences settings = getSharedPreferences(Yaniv.PREFS_NAME, 0);
	        int viewedChangelogVersion = settings.getInt(Yaniv.PREFS_KEY_CHANGELOG_VERSION_VIEWED, 0);

	        if(viewedChangelogVersion < versionCode) {
	            Editor editor=settings.edit();
	            editor.putInt(Yaniv.PREFS_KEY_CHANGELOG_VERSION_VIEWED, versionCode);
	            editor.commit();
	            displayChangeLog();
	        }
	    } catch (NameNotFoundException e) {
	        Log.e(LOG_TAG,"Unable to get version code. Will not show changelog", e);
	    }

		
		// ScoreLoop
		initializeScoreloop();
	    
		startBtn = (Button) findViewById(id.StartButton);
		resumeBtn = (Button) findViewById(id.ResumeButton);
		tutorialBtn = (Button) findViewById(id.TutorialButton);
		highScoreBtn = (Button) findViewById(id.HighScoreButton);
		settingsBtn = (Button) findViewById(id.SettingsButton);
		bugBtn = (Button) findViewById(id.BugButton);
		exitBtn = (Button) findViewById(id.ExitButton);

		setResumeButtonEnabledStatus();

		startBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				startYanivHandler();
			}
		});

		resumeBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				resumeYanivHandler();
			}
		});

		tutorialBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {

				final CharSequence[] options = {"Video (Opens YouTube)", "Text"};
				AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
				builder.setTitle("Select tutorial method:");
				builder.setItems(options, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        if(item == 0){
			        	startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("http://www.youtube.com/watch?v=V4XuZRbbs6M")));
			        }else{
			        	startActivity(new Intent(Intent.ACTION_VIEW, Uri
			        			.parse("http://sites.google.com/a/geekadoo.com/support/how-to-play")));
			        }
			    }});
				AlertDialog alert = builder.create();
				
				alert.show();
				
//				startActivity(new Intent(Intent.ACTION_VIEW, Uri
//						.parse("http://www.youtube.com/watch?v=V4XuZRbbs6M")));
				
				//TODO: this doesnt open the youtube app
				//- think of a way you can do this without having to upgrade the app whenever the tutorial video changes...
//				startActivity(new Intent(Intent.ACTION_VIEW, Uri
//						.parse("http://yanivtutorial.geekadoo.com/")));
			}
		});
		
		
		highScoreBtn.setOnClickListener(new Button.OnClickListener() {
			
			private boolean userWantsHighScore;

			@Override
			public void onClick(View v) {
				User u = Session.getCurrentSession().getUser();
//				ScoreloopManager.getScore();
				userWantsHighScore = true;
				if(u.getLogin()==null && userWantsHighScore){
					// User has not registered yet
					AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
					builder.setMessage(R.string.highscoreUserLogin)
					       .setCancelable(false)
					       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
//					        	   startActivity(new Intent(MainScreen.this, ProfileActivity.class));
					       		SettingsDialog sDialog = new SettingsDialog(MainScreen.this);
					    		sDialog.show();
					           }
					       })
					       .setNegativeButton("No", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   userWantsHighScore = false; 
					        	   dialog.cancel();
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
				}else{
//				if(userWantsHighScore){
					startActivity(new Intent(MainScreen.this, HighscoresActivity.class));
				}
//				Log.e(LOG_TAG, "User="+u.getDisplayName());
//				Log.e(LOG_TAG, "User active="+u.isActive());
//				Log.e(LOG_TAG, "User login="+u.getLogin());
//				Log.e(LOG_TAG, "User isAuth="+u.isAuthenticated());
//				Log.e(LOG_TAG, "User detail="+u.getDetail().toString());
				
				
			}
		});
		
		settingsBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingsDialog dialog = new SettingsDialog(MainScreen.this);
				dialog.show();
			}
		});


		bugBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {

				final Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);

				emailIntent.setType("text/html");

				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { "bugs@geekadoo.com" });

				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"Please fix this!");
				String yanivVersion;
				try{
					PackageInfo pi = getPackageManager().getPackageInfo("com.geekadoo",PackageManager.GET_META_DATA);
					yanivVersion = "Yaniv Version: Name:"+ pi.versionName +", Code:"+pi.versionCode;
				} catch (NameNotFoundException e) {
					Log.e(LOG_TAG, "Could not find package for debug info");
					yanivVersion="Could not find package for debug info";
				}

				String s;
					s = "<BR>Debug-info:"
					+ yanivVersion
					 + "<BR> OS Version: " + System.getProperty("os.version")
							+ "(" + android.os.Build.VERSION.INCREMENTAL + ")"
					 + "<BR> OS API Level: " + android.os.Build.VERSION.SDK
					 + "<BR> Device: " + android.os.Build.DEVICE
					 + "<BR> Model (and Product): " + android.os.Build.MODEL
							+ " (" + android.os.Build.PRODUCT + ")";

				emailIntent.putExtra(
						android.content.Intent.EXTRA_TEXT,
						Html.fromHtml("Hi, I found this bug, or request this feature:" +
								"<br><br><br>---<br><br><br>"
//								+ "<BR>My Device is: (nexus one)"
//								+ "<BR>What steps will reproduce the problem?"
//								+ "<BR>1. (start new game)"
//								+ "<BR>2.(click on deck)"
//								+ "<BR>3.(nothing happens)"
//								+ "<BR>What is the expected output? (cards should be dealt)"
//								+ "<BR>What do you see instead?(nothing, duh!)"
								+ s));

				MainScreen.this.startActivity(Intent.createChooser(emailIntent,
						"Send mail..."));
			}
		});

		exitBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				System.exit(0);
			}
		});
	}

	private void displayChangeLog() {
        //load some kind of a view
	    LayoutInflater li = LayoutInflater.from(this);
	    View view = li.inflate(R.layout.changelog_view, null);

	    new AlertDialog.Builder(this)
	    .setTitle("Changelog")
	    .setIcon(android.R.drawable.ic_menu_info_details)
	    .setView(view)
	    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int whichButton) {
	          //
	      }
	    }).show();
		
	}

	private void initializeScoreloop() {
		String GAME_ID = "a0d2f0df-071b-448f-9d95-98f40756be87";
		String GAME_SECRET = "Pb9sf2VjkR4pFWom4o8/ArC4uKyihh5DWk1LC7WHPAJy3nT3bcl7+w==";
		ScoreloopManager.init(this, GAME_ID, GAME_SECRET);
		
		myUserController = new UserController(new MyUserControllerObserver(MainScreen.this));


		SharedPreferences settings = getSharedPreferences(Yaniv.PREFS_NAME, 0);
		// makes sure that whenever the shared prefs change, the login will change as well
		settings.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				if(key.equals(Yaniv.PREFS_PLAYER_NAME_PROPERTY)){
					setScoreloopUserLoginAccordingToPrefs(sharedPreferences);
				}
			}
		});
		// and do it for this initialization
		setScoreloopUserLoginAccordingToPrefs(settings);
	}

	private void setScoreloopUserLoginAccordingToPrefs(SharedPreferences settings) {

		final String name = settings.getString(Yaniv.PREFS_PLAYER_NAME_PROPERTY, getString(R.string.pNameDefVal));
		Session.getCurrentSession().getUser().setLogin(name);
		myUserController.submitUser();
		
	}

	private void setResumeButtonEnabledStatus() {
		if (!YanivPersistenceAdapter
				.isSavedGameDataValid(getApplicationContext())) {
			resumeBtn.setEnabled(false);
		}else{
			resumeBtn.setEnabled(true);
		}
	}

	protected void startYanivHandler() {
		StartNewGameDialog dialog = new StartNewGameDialog(MainScreen.this);
		dialog.show();
//		
//		// Create an intent
//		Intent yanivIntent = new Intent(this, Yaniv.class);
//		// Transfer information to the yaniv (such as state and game attributes)
//		yanivIntent.putExtra(GameData.STATE, GameData.GAME_STATES.start);
//
//		startActivityForResult(yanivIntent, GameData.START_GAME);
	}

	protected void resumeYanivHandler() {
		// Create an intent
		Intent yanivIntent = new Intent(this, Yaniv.class);
		yanivIntent.putExtra(GameData.STATE, GameData.GAME_STATES.resume);
		startActivityForResult(yanivIntent, GameData.RESUME_GAME);
	}

	// Listen for results.
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// See which child activity is calling us back.
		switch (resultCode) {
		case MATCH_OVER:
			// Disable the resume button
			YanivPersistenceAdapter
					.deleteSavedGameData(getApplicationContext());
			resumeBtn.setEnabled(false);
			// get the data & populate high scores
			break;
		case RESULT_CANCELED:
			// This is the standard resultCode that is sent back if the
			// activity crashed or didn't doesn't supply an explicit result.
			setResumeButtonEnabledStatus();
			break;
		default:
			setResumeButtonEnabledStatus();
			break;
		}
	}

}