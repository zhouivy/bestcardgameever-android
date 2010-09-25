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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.scoreloop.client.android.core.controller.RequestCancelledException;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.UsersController;
import com.scoreloop.client.android.core.model.Game;
import com.scoreloop.client.android.core.model.User;

//this part is included in the ScoreloopCoreUI doc
import com.geekadoo.R;

public class GameActivity extends BaseActivity {

	private class ListItemAdapter extends GenericListItemAdapter {

		public ListItemAdapter(final Context context, final int resource, final List<ListItem> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			convertView = init(position, convertView);

			text0.setVisibility(View.GONE);
			text2.setVisibility(View.GONE);
			if (listItem.isSpecialItem()) {
				text1.setText(listItem.getLabel());
				image.setVisibility(View.GONE);
			} else {
				text1.setText(listItem.getUser().getLogin());
				image.setVisibility(View.VISIBLE);
			}

			return convertView;
		}
	}

	private class UsersControllerObserver implements RequestControllerObserver {

		@Override
		public void requestControllerDidFail(final RequestController requestController, final Exception exception) {
			if (!(exception instanceof RequestCancelledException)) {
				showDialogSafe(BaseActivity.DIALOG_ERROR_NETWORK);
				setProgressIndicator(false);
				// blockUI(false);
				adapter.clear();
			}
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController requestController) {
			updateStatusBar();

			adapter.clear();

			if (usersController.isOverLimit()) {
				if (usersController.isMaxUserCount()) {
					showToast(getResources().getString(R.string.sl_found_too_many_users));
				} else {
					showToast(String.format(getResources().getString(R.string.sl_found_many_users_format),
							usersController.getCountOfUsers()));
				}

				return;
			}

			final List<User> users = usersController.getUsers();
			if (!users.isEmpty()) {
				for (final User user : users) {
					adapter.add(new ListItem(user));
				}
			} else {
				adapter.add(new ListItem(getResources().getString(R.string.sl_no_results_found)));
			}

			// blockUI(false);
			setProgressIndicator(false);
		}
	}

	private static final int SEARCH_LIMIT = 100;
	private ListItemAdapter adapter;
	private LinearLayout detailsLayout;
	private Game game;
	private ImageView iconImage;
	private ListView listView;
	private UsersController usersController;

	// private void blockUI(final boolean flg) {
	// listView.setEnabled(!flg);
	// }

	private void switchToTab(final int tab) {
		if (tab == 0) {
			detailsLayout.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			detailsLayout.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);

			if (adapter.isEmpty()) {
				// blockUI(true);
				setProgressIndicator(true);

				adapter.add(new ListItem(getResources().getString(R.string.sl_loading)));

				usersController.loadBuddiesForGame(game);
			}
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_game);

		updateStatusBar();
		updateHeading(getString(R.string.sl_games), false);

		game = ScoreloopManager.getGame();

		final TextView nameText = (TextView) findViewById(R.id.name_text);
		nameText.setText(game.getName());
		nameText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

		((TextView) findViewById(R.id.publisher_text)).setText(game.getPublisherName());

		iconImage = (ImageView) findViewById(R.id.image_view);
		iconImage.setImageDrawable(getDrawable(game.getImageUrl()));

		detailsLayout = (LinearLayout) findViewById(R.id.details_layout);

		final TextView descriptionText = (TextView) findViewById(R.id.description_text);
		final String description = game.getDescription();
		if (!"".equals(description)) {
			descriptionText.setText(description);
		}

		final Button getGameButton = (Button) findViewById(R.id.get_game_button);
		getGameButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(game.getDownloadUrl())));
			}
		});

		listView = (ListView) findViewById(R.id.list_view);

		final SegmentedView segmentedView = (SegmentedView) findViewById(R.id.segments);
		segmentedView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {
				switchToTab(segmentedView.getSelectedSegment());
			}

		});

		adapter = new ListItemAdapter(GameActivity.this, R.layout.sl_game, new ArrayList<ListItem>());
		listView.setAdapter(adapter);

		usersController = new UsersController(new UsersControllerObserver());
		usersController.setSearchLimit(SEARCH_LIMIT);
	}

	@Override
	protected void onStart() {
		super.onStart();

		setNotify(new Runnable() {
			public void run() {
				iconImage.setImageDrawable(getDrawable(game.getImageUrl()));
			}
		});
	}
}
