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

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.UserController;
import com.scoreloop.client.android.core.controller.UsersController;
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.core.model.SocialProvider;
import com.scoreloop.client.android.core.model.User;

//this part is included in the ScoreloopCoreUI doc
import com.geekadoo.R;

public class BuddiesAddActivity extends BaseActivity {

	private class BuddyAddObserver extends UserGenericObserver {
		@Override
		public void requestControllerDidFail(final RequestController requestController, final Exception exception) {
			showDialogSafe(BaseActivity.DIALOG_ERROR_NETWORK);
			setProgressIndicator(false);
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController requestController) {
			buddiesAdded++;
			if (!addNextBuddy()) {
				setProgressIndicator(false);
				final int resId = buddiesAdded == 1 ? R.string.sl_buddies_added_one_format
						: R.string.sl_buddies_added_other_format;
				showToast(String.format(getResources().getString(resId), buddiesAdded));
				((ProfileActivity) getParent()).onBackPressed();
			}
		}
	}

	private class ListItemAdapter extends GenericListItemAdapter {

		public ListItemAdapter(final Context context, final int resource, final List<ListItem> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			convertView = init(position, convertView);

			final LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) image.getLayoutParams();
			layoutParams.height = LayoutParams.WRAP_CONTENT;
			layoutParams.width = LayoutParams.WRAP_CONTENT;
			image.setImageDrawable(getResources().getDrawable(imgRes[position]));
			text0.setVisibility(View.GONE);
			text1.setText(listItem.getLabel());
			text2.setVisibility(View.GONE);

			return convertView;
		}
	}

	private class LoginDialog extends Dialog implements OnClickListener {
		private EditText loginEdit;
		private Button okButton;

		LoginDialog(final Context context) {
			super(context);
		}

		@Override
		public void onClick(final View v) {
			if (v.getId() == R.id.button_ok) {
				dismiss();
				handleDialogClick(loginEdit.getText().toString());
			}
		}

		@Override
		protected void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.sl_dialog_login);
			setTitle(getResources().getString(R.string.sl_sl_login));
			loginEdit = (EditText) findViewById(R.id.edit_login);
			okButton = (Button) findViewById(R.id.button_ok);
			okButton.setOnClickListener(this);
		}
	}

	private class UsersSearchObserver implements RequestControllerObserver {

		@Override
		public void requestControllerDidFail(final RequestController requestController, final Exception exception) {
			showDialogSafe(BaseActivity.DIALOG_ERROR_NETWORK);
			setProgressIndicator(false);
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController requestController) {
			setProgressIndicator(false);

			if (usersSearchController.isOverLimit()) {
				if (usersSearchController.isMaxUserCount()) {
					showToast(getResources().getString(R.string.sl_found_too_many_users));
					((ProfileActivity) getParent()).onBackPressed();
				} else {
					showToast(String.format(getResources().getString(R.string.sl_found_many_users_format),
							usersSearchController.getCountOfUsers()));
					((ProfileActivity) getParent()).onBackPressed();
				}

				return;
			}

			usersFound = new ArrayList<User>(usersSearchController.getUsers());
			if (usersFound.isEmpty()) {
				showToast(getResources().getString(R.string.sl_found_no_user));
				((ProfileActivity) getParent()).onBackPressed();
			} else {
				setProgressIndicator(true);
				buddiesAdded = 0;
				if (!addNextBuddy()) {
					setProgressIndicator(false);
					showToast(getResources().getString(R.string.sl_found_no_user));
					((ProfileActivity) getParent()).onBackPressed();
				}
			}
		}
	}

	private static final int ADDRESSBOOK = 3;
	private static final int DIALOG_LOGIN = 1000;
	private static final int FACEBOOK = 0;
	private static int[] imgRes = { R.drawable.sl_facebook, R.drawable.sl_myspace, R.drawable.sl_twitter,
			R.drawable.sl_addressbook, R.drawable.sl_login };
	private static final int LOGIN = 4;
	private static final int MYSPACE = 1;

	private static final int TWITTER = 2;

	private int buddiesAdded;
	private UserController buddyAddController;
	private List<User> usersFound;
	private UsersController usersSearchController;

	private boolean addNextBuddy() {
		User user;
		do {
			user = popUser();
		} while ((user != null)
				&& (Session.getCurrentSession().getUser().equals(user) || Session.getCurrentSession().getUser()
						.getBuddyUsers().contains(user)));

		if (user != null) {
			buddyAddController.setUser(user);
			buddyAddController.addAsBuddy();
			return true;
		}
		return false;
	}

	private void handleDialogClick(final String login) {
		setProgressIndicator(true);
		usersSearchController.setSearchOperator(UsersController.LoginSearchOperator.EXACT_MATCH);
		usersSearchController.searchByLogin(login);
	}

	private void handleItemClick(final int position) {
		switch (position) {
		case FACEBOOK:
			handleSocialItemClick(facebookProvider);
			break;
		case MYSPACE:
			handleSocialItemClick(myspaceProvider);
			break;
		case TWITTER:
			handleSocialItemClick(twitterProvider);
			break;
		case ADDRESSBOOK:
			setProgressIndicator(true);
			usersSearchController.searchByLocalAddressBook();
			break;
		case LOGIN:
			showDialogSafe(DIALOG_LOGIN);
			break;
		default:
			break;
		}
	}

	private void handleSocialItemClick(final SocialProvider socialProvider) {
		setProgressIndicator(true);
		connectToSocialProvider(socialProvider);
	}

	private User popUser() {
		if (usersFound == null) {
			return null;
		}
		if (usersFound.isEmpty()) {
			return null;
		}
		return usersFound.remove(0);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_buddies_add);

		final ListView listView = (ListView) findViewById(R.id.list_view);
		final ListItemAdapter adapter = new ListItemAdapter(BuddiesAddActivity.this, R.layout.sl_buddies_add,
				new ArrayList<ListItem>());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				handleItemClick(position);
			}
		});

		adapter.add(new ListItem(getResources().getString(R.string.sl_facebook)));
		adapter.add(new ListItem(getResources().getString(R.string.sl_myspace)));
		adapter.add(new ListItem(getResources().getString(R.string.sl_twitter)));
		adapter.add(new ListItem(getResources().getString(R.string.sl_addressbook)));
		adapter.add(new ListItem(getResources().getString(R.string.sl_sl_login)));

		usersSearchController = new UsersController(new UsersSearchObserver());
		usersSearchController.setSearchesGlobal(true);

		buddyAddController = new UserController(new BuddyAddObserver());

	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		final Dialog dialog = super.onCreateDialog(id);
		if (dialog != null) {
			return dialog;
		}

		switch (id) {
		case DIALOG_LOGIN:
			return new LoginDialog(this);
		default:
			return null;
		}
	}

	@Override
	void onSocialProviderConnectionStatusChange(final SocialProvider socialProvider) {
		setProgressIndicator(false);
		if (socialProvider.isUserConnected(Session.getCurrentSession().getUser())) {
			setProgressIndicator(true);
			usersSearchController.searchBySocialProvider(socialProvider);
		} else {
			((ProfileActivity) getParent()).onBackPressed();
		}
	}
}
