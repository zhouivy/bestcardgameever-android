package com.geekadoo.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.geekadoo.R;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.UserController;
import com.scoreloop.client.android.core.controller.UserControllerObserver;

public class MyUserControllerObserver implements UserControllerObserver {

	private static final String LOG_TAG = "MyUserControllerObserver";
	private Context context;

	public MyUserControllerObserver(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void requestControllerDidReceiveResponse(RequestController arg0) {
		// show a toast that says "user registered successfully"
		Toast toast = Toast.makeText(context,
				context.getString(R.string.highscoreLoginSuccessful),
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void requestControllerDidFail(RequestController arg0,
			final Exception arg1) {
		Log.e(LOG_TAG, "requestControllerDidFail", arg1);
		// show a dialog that says "user was not registered" and the exception +
		// send it with acra or something
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.highscoreLoginUnsuccessful)
				.setPositiveButton(R.string.highscoreReportThis,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								final Intent emailIntent = new Intent(
										android.content.Intent.ACTION_SEND);
								emailIntent.setType("text/html");
								emailIntent.putExtra(
										android.content.Intent.EXTRA_EMAIL,
										new String[] { "bugs@geekadoo.com" });
								emailIntent.putExtra(
										android.content.Intent.EXTRA_SUBJECT,
										R.string.highscoreBugReportSubject);
								emailIntent.putExtra(
										android.content.Intent.EXTRA_TEXT,
										arg1.toString());
								context.startActivity(Intent.createChooser(
										emailIntent, "Send mail..."));

							}
						})
				.setNegativeButton(R.string.highscoreDontReportThis,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
	}

	@Override
	public void userControllerDidFailOnUsernameAlreadyTaken(UserController arg0) {
		Log.e(LOG_TAG, "ELAD in userControllerDidFailOnUsernameAlreadyTaken");

		// say user already exists and popup the settings page
		Toast toast = Toast.makeText(context,
				context.getString(R.string.highscoreLoginAlreadyExists),
				Toast.LENGTH_LONG);
		toast.show();

		SettingsDialog dialog = new SettingsDialog(context);
		dialog.show();

	}

	@Override
	public void userControllerDidFailOnInvalidEmailFormat(UserController arg0) {
		// shouldn't happen, not giving email
		Log.e(LOG_TAG, "userControllerDidFailOnInvalidEmailFormat");
	}

	@Override
	public void userControllerDidFailOnEmailAlreadyTaken(UserController arg0) {
		// shouldn't happen, not giving email
		Log.e(LOG_TAG, "userControllerDidFailOnEmailAlreadyTaken");
	}
}
