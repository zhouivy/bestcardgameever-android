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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.UserController;
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.core.model.User;

//this part is included in the ScoreloopCoreUI doc
import com.geekadoo.R;

public class BuddiesActivity extends BaseActivity {

	private class BuddiesRequestObserver extends UserGenericObserver {

		@Override
		public void requestControllerDidFail(final RequestController requestController, final Exception exception) {
			showDialogSafe(BaseActivity.DIALOG_ERROR_NETWORK);
			setProgressIndicator(false);
			blockUI(false);
			adapter.clear();
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController requestController) {
			updateStatusBar();

			adapter.clear();

			final List<User> buddies = Session.getCurrentSession().getUser().getBuddyUsers();
			if (!buddies.isEmpty()) {
				for (final User buddy : buddies) {
					adapter.add(new ListItem(buddy));
				}
			} // no item in case of empty result

			blockUI(false);
			setProgressIndicator(false);
		}

	}

	private class ListItemAdapter extends GenericListItemAdapter {

		public ListItemAdapter(final Context context, final int resource, final List<ListItem> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			convertView = init(position, convertView);

			text0.setVisibility(View.GONE);
			if (listItem.isSpecialItem()) {
				text1.setText(listItem.getLabel());
			} else {
				text1.setText(listItem.getUser().getLogin());
			}
			text2.setVisibility(View.GONE);

			return convertView;
		}
	}

	private ListItemAdapter adapter;
	private LinearLayout addButton;
	private UserController buddiesRequestController;
	private ListView usersListView;

	private void blockUI(final boolean flg) {
		addButton.setEnabled(!flg);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_buddies);

		usersListView = (ListView) findViewById(R.id.list_view);
		usersListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapter, final View view, final int position, final long id) {
				final ListItem listItem = (ListItem) adapter.getItemAtPosition(position);
				if (!listItem.isSpecialItem()) {
					final User user = listItem.getUser();
					ScoreloopManager.setUser(user);
					startActivity(new Intent(BuddiesActivity.this, UserActivity.class));
				}
			}
		});

		addButton = (LinearLayout) findViewById(R.id.button_add);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				((ProfileActivity) getParent()).setSubContent(2);
			}
		});

		final ImageView iconImage = (ImageView) findViewById(R.id.image);
		iconImage.setImageDrawable(getResources().getDrawable(R.drawable.sl_user_add));
		final TextView text0 = (TextView) findViewById(R.id.text0);
		text0.setVisibility(View.GONE);
		final TextView text1 = (TextView) findViewById(R.id.text1);
		text1.setText(R.string.sl_buddies_add);
		final TextView text2 = (TextView) findViewById(R.id.text2);
		text2.setVisibility(View.GONE);

		adapter = new ListItemAdapter(BuddiesActivity.this, R.layout.sl_buddies, new ArrayList<ListItem>());
		usersListView.setAdapter(adapter);

		buddiesRequestController = new UserController(new BuddiesRequestObserver());
	}

	@Override
	protected void onStart() {
		super.onStart();

		blockUI(true);
		setProgressIndicator(true);

		adapter.clear();
		adapter.add(new ListItem(getResources().getString(R.string.sl_loading)));

		buddiesRequestController.loadBuddies();
	}
}
