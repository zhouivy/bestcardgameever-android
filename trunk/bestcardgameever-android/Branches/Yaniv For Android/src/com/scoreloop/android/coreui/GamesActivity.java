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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.scoreloop.client.android.core.controller.GamesController;
import com.scoreloop.client.android.core.controller.RequestCancelledException;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.model.Game;

//this part is included in the ScoreloopCoreUI doc
import com.geekadoo.R;

public class GamesActivity extends BaseActivity {

	private class GamesControllerObserver implements RequestControllerObserver {

		private int target;

		@Override
		public void requestControllerDidFail(final RequestController requestController, final Exception exception) {
			if (!(exception instanceof RequestCancelledException)) {
				showDialogSafe(BaseActivity.DIALOG_ERROR_NETWORK);
				setProgressIndicator(false);
				// blockUI(false);
				adapter[target].clear();
			}
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController requestController) {
			updateStatusBar();

			adapter[target].clear();

			final List<Game> games = gamesController[target].getGames();
			if (!games.isEmpty()) {
				for (final Game game : games) {
					adapter[target].add(new ListItem(game));
				}
			} else {
				adapter[target].add(new ListItem(getResources().getString(R.string.sl_no_results_found)));
			}

			// blockUI(false);
			setProgressIndicator(false);
		}

		private void setTarget(final int target) {
			this.target = target;
		}

	}

	private class GamesFeaturedControllerObserver implements RequestControllerObserver {

		@Override
		public void requestControllerDidFail(final RequestController requestController, final Exception exception) {
			if (!(exception instanceof RequestCancelledException)) {
				subheadingLayout.setVisibility(View.GONE);
			}
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController requestController) {
			updateStatusBar();

			final List<Game> games = gamesFeaturedController.getGames();
			if (!games.isEmpty()) {
				((TextView) findViewById(R.id.loading_text)).setVisibility(View.GONE);
				featuredLayout.setVisibility(View.VISIBLE);

				final ImageView imageView = (ImageView) findViewById(R.id.image_view);
				final TextView nameText = (TextView) findViewById(R.id.name_text);
				final TextView publisherText = (TextView) findViewById(R.id.publisher_text);

				featuredGame = games.get(0);
				nameText.setText(featuredGame.getName());
				publisherText.setText(featuredGame.getPublisherName());
				imageView.setImageDrawable(getDrawable(featuredGame.getImageUrl()));
			} else {
				subheadingLayout.setVisibility(View.GONE);
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

			if (listItem.isSpecialItem()) {
				text0.setText("");
				text1.setText(listItem.getLabel());
				text2.setVisibility(View.GONE);
				image.setVisibility(View.GONE);
			} else {
				text0.setVisibility(View.GONE);
				text1.setText(listItem.getGame().getName());
				text1.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
				text2.setText(listItem.getGame().getPublisherName());
				text2.setVisibility(View.VISIBLE);
				image.setVisibility(View.VISIBLE);
				image.setImageDrawable(getDrawable(listItem.getGame().getImageUrl()));
			}

			return convertView;
		}

	}

	private enum Tab { // see NR_OF_TABS
		POPULAR, NEW, FRIENDS;
	}

	private static final int NR_OF_TABS = 3;
	private static final int RANGE_LENGTH_FEATURED = 1;
	private static final int RANGE_LENGTH_OTHER = 10;
	private final ListItemAdapter adapter[] = new ListItemAdapter[NR_OF_TABS];
	private Game featuredGame;
	private FrameLayout featuredLayout;
	private final GamesController gamesController[] = new GamesController[NR_OF_TABS];
	private GamesController gamesFeaturedController;
	private ListView gamesListView;
	private ImageView iconImage;
	private FrameLayout subheadingLayout;

	private Tab tab;

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(Menu.NONE, MENU_PROFILE, Menu.NONE, R.string.sl_profile).setIcon(R.drawable.sl_menu_profile);
		menu.add(Menu.NONE, MENU_HIGHSCORES, Menu.NONE, R.string.sl_highscores).setIcon(R.drawable.sl_menu_highscores);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PROFILE:
			startActivity(new Intent(GamesActivity.this, ProfileActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
			return true;
		case MENU_HIGHSCORES:
			startActivity(new Intent(GamesActivity.this, HighscoresActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// private void blockUI(final boolean flg) {
	// gamesListView.setEnabled(!flg);
	// }

	private void switchToTab(final Tab newTab) {
		tab = newTab;
		gamesListView.setAdapter(adapter[tab.ordinal()]);
		if (adapter[tab.ordinal()].isEmpty()) {
			// blockUI(true);
			setProgressIndicator(true);

			adapter[tab.ordinal()].add(new ListItem(getResources().getString(R.string.sl_loading)));

			switch (tab) {
			case POPULAR:
				gamesController[tab.ordinal()].loadRangeForPopular();
				break;
			case NEW:
				gamesController[tab.ordinal()].loadRangeForNew();
				break;
			case FRIENDS:
				gamesController[tab.ordinal()].loadRangeForBuddies();
				break;
			default:
				gamesController[0].loadRangeForPopular();
				break;
			}
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_games);

		updateStatusBar();
		updateHeading(getString(R.string.sl_games), false);

		subheadingLayout = (FrameLayout) findViewById(R.id.subheading_layout);

		featuredLayout = (FrameLayout) findViewById(R.id.featured_layout);
		featuredLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				ScoreloopManager.setGame(featuredGame);
				startActivity(new Intent(GamesActivity.this, GameActivity.class));
			}
		});

		iconImage = (ImageView) findViewById(R.id.image_view);

		gamesListView = (ListView) findViewById(R.id.list_view);
		gamesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapter, final View view, final int position, final long id) {
				final ListItem listItem = (ListItem) adapter.getItemAtPosition(position);
				if (!listItem.isSpecialItem()) {
					ScoreloopManager.setGame(listItem.getGame());
					startActivity(new Intent(GamesActivity.this, GameActivity.class));
				}
			}
		});

		final SegmentedView segmentedView = (SegmentedView) findViewById(R.id.segments);
		segmentedView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {
				switchToTab(Tab.values()[segmentedView.getSelectedSegment()]);
			}

		});

		tab = Tab.POPULAR;

		for (int i = 0; i < NR_OF_TABS; i++) {
			final GamesControllerObserver observer = new GamesControllerObserver();
			observer.setTarget(i);
			gamesController[i] = new GamesController(observer);
			gamesController[i].setRangeLength(RANGE_LENGTH_OTHER);

			adapter[i] = new ListItemAdapter(GamesActivity.this, R.layout.sl_games, new ArrayList<ListItem>());
		}

		gamesFeaturedController = new GamesController(new GamesFeaturedControllerObserver());
		gamesFeaturedController.setRangeLength(RANGE_LENGTH_FEATURED);
		gamesFeaturedController.loadRangeForFeatured(); // no progress indicator for this one

	}

	@Override
	protected void onStart() {
		super.onStart();

		setNotify(new Runnable() {
			public void run() {
				if (featuredGame != null) {
					iconImage.setImageDrawable(getDrawable(featuredGame.getImageUrl()));
				}

				for (int i = 0; i < NR_OF_TABS; i++) {
					adapter[i].notifyDataSetChanged();
				}
			}
		});

		if (featuredGame != null) {
			iconImage.setImageDrawable(getDrawable(featuredGame.getImageUrl()));
		}

		switchToTab(tab);
	}
}
