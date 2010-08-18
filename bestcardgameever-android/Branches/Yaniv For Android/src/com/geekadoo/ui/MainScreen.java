package com.geekadoo.ui;

/**
 * This is the "main" screen of the game, where the user can start a game
 * resume a game, etc
 * @author Elad & Sivan
 */
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
	Button bugBtn;
	Button exitBtn;

	public MainScreen() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);

		startBtn = (Button) findViewById(id.StartButton);
		resumeBtn = (Button) findViewById(id.ResumeButton);
		tutorialBtn = (Button) findViewById(id.TutorialButton);
		bugBtn = (Button) findViewById(id.BugButton);
		exitBtn = (Button) findViewById(id.ExitButton);

		if (!YanivPersistenceAdapter
				.isSavedGameDataValid(getApplicationContext())) {
			resumeBtn.setEnabled(false);
		}

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
				startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.youtube.com/watch?v=V4XuZRbbs6M")));
				//TODO: this doesnt open the youtube app
				//- think of a way you can do this without having to upgrade the app whenever the tutorial video changes...
//				startActivity(new Intent(Intent.ACTION_VIEW, Uri
//						.parse("http://yanivtutorial.geekadoo.com/")));
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
			break;
		default:
			break;
		}
	}

}