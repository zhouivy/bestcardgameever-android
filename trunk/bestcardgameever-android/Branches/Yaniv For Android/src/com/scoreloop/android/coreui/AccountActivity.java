/*
 * In derogation of the Scoreloop SDK - License Agreement concluded between
 * Licensor and Licensee, as defined therein, the following conditions shall
 * apply for the source code contained below, whereas apart from that the
 * Scoreloop SDK - License Agreement shall remain unaffected.
 * 
 * Copyright: Scoreloop AG, Germany (Licensor)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.scoreloop.android.coreui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.geekadoo.R;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.UserController;
import com.scoreloop.client.android.core.controller.UserControllerObserver;
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.core.model.User;

public class AccountActivity extends BaseActivity {

	private class UserUpdateObserver implements UserControllerObserver {

		@Override
		public void requestControllerDidFail(final RequestController requestController, final Exception exception) {
			// since the UI blocks there's no failure due to cancellation
			onErrorUpdateUIAndResetData(BaseActivity.DIALOG_ERROR_NETWORK, false);
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController requestController) {
			setProgressIndicator(false);
			blockUI(false);
			setUIToSessionUser();
		}

		@Override
		public void userControllerDidFailOnEmailAlreadyTaken(final UserController controller) { // shouldn't happen when called via onStart()
			onErrorUpdateUIAndResetData(BaseActivity.DIALOG_ERROR_USER_EMAIL_ALREADY_TAKEN, true);
		}

		@Override
		public void userControllerDidFailOnInvalidEmailFormat(final UserController controller) { // shouldn't happen when called via onStart()
			onErrorUpdateUIAndResetData(BaseActivity.DIALOG_ERROR_USER_INVALID_EMAIL_FORMAT, true);
		}

		@Override
		public void userControllerDidFailOnUsernameAlreadyTaken(final UserController controller) { // shouldn't happen when called via onStart()
			onErrorUpdateUIAndResetData(BaseActivity.DIALOG_ERROR_USER_NAME_ALREADY_TAKEN, true);
		}
	}

	private EditText emailEdit;
	private EditText loginEdit;
	private String oldEmail;
	private String oldLogin;
	private Button updateButton;
	private UserController userController;

	private void blockUI(final boolean flg) {
		updateButton.setEnabled(!flg);
		loginEdit.setEnabled(!flg);
		emailEdit.setEnabled(!flg);
	}

	private void onErrorUpdateUIAndResetData(final int error, final boolean enableUI) {
		setProgressIndicator(false);
		if (enableUI) {
			blockUI(false);
		}
		showDialogSafe(error);

		setSessionUserToOldData();
		setUIToSessionUser();
	}

	private void setOldDataToSessionUser() {
		final User user = Session.getCurrentSession().getUser();
		oldLogin = user.getLogin();
		oldEmail = user.getEmailAddress();
	}

	private void setSessionUserToOldData() {
		final User user = Session.getCurrentSession().getUser();
		user.setLogin(oldLogin);
		user.setEmailAddress(oldEmail);
	}

	private void setUIToSessionUser() {
		final User user = Session.getCurrentSession().getUser();
		loginEdit.setText(user.getLogin());
		emailEdit.setText(user.getEmailAddress());
		updateStatusBar();
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_account);

		userController = new UserController(new UserUpdateObserver());

		updateStatusBar();

		loginEdit = (EditText) findViewById(R.id.login_edit);
		emailEdit = (EditText) findViewById(R.id.email_edit);

		updateButton = (Button) findViewById(R.id.update_button);
		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				setOldDataToSessionUser();

				final String login = loginEdit.getText().toString().trim();
				final String email = emailEdit.getText().toString().trim();
				loginEdit.setText(login);
				emailEdit.setText(email);

				final User user = Session.getCurrentSession().getUser();
				user.setLogin(login);
				user.setEmailAddress(email);
				blockUI(true);
				setProgressIndicator(true);
				userController.submitUser();
			}
		});

		ImageButton slappButton = (ImageButton) findViewById(R.id.slapp_download_button);
		slappButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// TODO: getScoreloopAppDownloadUrl returns null before session is authenticated.
				// for now, I just disable this call, probably should disable button until return of observer
				String downloadUrl = Session.getCurrentSession().getScoreloopAppDownloadUrl();
				if (downloadUrl != null) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)));
				}
			}
		});

		setOldDataToSessionUser();
		blockUI(true);
		setProgressIndicator(true);
		userController.loadUser(); // login and email might have been changed by a different client, therefore update every time we come here
	}
}
