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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.scoreloop.client.android.core.controller.RequestCancelledException;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.ScoresController;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.SearchList;
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.core.model.User;

//this part is included in the ScoreloopCoreUI doc
import com.geekadoo.R;

public class HighscoresActivity extends BaseActivity {

	private class ListItemAdapter extends GenericListItemAdapter {

		public ListItemAdapter(final Context context, final int resource, final List<ListItem> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			convertView = init(position, convertView);

			if (listItem.isSpecialItem()) {
				image.setVisibility(View.GONE);
				text0.setText("");
				text2.setVisibility(View.GONE);
				text1.setText(listItem.getLabel());
			} else {
				image.setVisibility(View.VISIBLE);
				text0.setText(listItem.getScore().getRank() + ".");
				text1.setText(listItem.getScore().getUser().getLogin());
				text2.setVisibility(View.VISIBLE);
				text2.setText("" + listItem.getScore().getResult().intValue());
				text2.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			}

			if (!listItem.isSpecialItem()) {
				highlightView(convertView, listItem.getScore().getUser().equals(Session.getCurrentSession().getUser()));
			} else {
				highlightView(convertView, false);
			}

			return convertView;
		}

	}

	private class ScoresControllerObserver implements RequestControllerObserver {

		@Override
		public void requestControllerDidFail(final RequestController requestController, final Exception exception) {
			if (!(exception instanceof RequestCancelledException)) {
				showDialogSafe(BaseActivity.DIALOG_ERROR_NETWORK);
				setProgressIndicator(false);
				blockUI(false);
				adapter.clear();
			}
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController requestController) {
			updateStatusBar();

			adapter.clear();

			final List<Score> scores = scoresController.getScores();
			if (!scores.isEmpty()) {
				for (final Score score : scores) {
					adapter.add(new ListItem(score));
				}
			} else {
				adapter.add(new ListItem(getResources().getString(R.string.sl_no_results_found)));
			}

			if (scoresController.hasPreviousRange()) {
				adapter.insert(new ListItem(getResources().getString(R.string.sl_prev), TAG_PREV), 0);
				adapter.insert(new ListItem(getResources().getString(R.string.sl_top), TAG_TOP), 0);
			}

			if (scoresController.hasNextRange()) {
				adapter.add(new ListItem(getResources().getString(R.string.sl_next), TAG_NEXT));
			}

			Score myScore = null;
			final User currentUser = Session.getCurrentSession().getUser();
			int idx = 0;
			for (final Score score : scores) {
				if ((score != null) && (score.getUser().equals(currentUser))) {
					myScore = score;
					break;
				}
				++idx;
			}

			if (loadingType == LoadingType.ME) {
				if (myScore != null) {
					scoresListView.setSelection(idx < FIXED_OFFSET ? 0 : idx - FIXED_OFFSET);
				} else {
					showToast(getResources().getString(R.string.sl_not_on_highscore_list));
				}
			}

			if (myScore != null) {
				((TextView) myScoreView.findViewById(R.id.text0)).setText(myScore.getRank() + ".");
				((TextView) myScoreView.findViewById(R.id.text1)).setText(myScore.getUser().getLogin());
				((TextView) myScoreView.findViewById(R.id.text2)).setText("" + myScore.getResult().intValue());
				((TextView) myScoreView.findViewById(R.id.text2)).setTypeface(Typeface.DEFAULT, Typeface.BOLD);
				myScoreView.setVisibility(View.GONE);
				dividerView.setVisibility(View.GONE);
			} else {
				if (((TextView) myScoreView.findViewById(R.id.text0)).getText().toString().equals("")) {
					myScoreView.setVisibility(View.GONE);
					dividerView.setVisibility(View.GONE);
				} else {
					myScoreView.setVisibility(View.VISIBLE);
					dividerView.setVisibility(View.VISIBLE);
				}
			}

			blockUI(false);
			setProgressIndicator(false);
		}

	}

	enum LoadingType {
		ME, OTHER;
	}

	private static final int DIALOG_GAME_MODE = 1000;
	private static final int FIXED_OFFSET = 1;
	private static final int RANGE_LENGTH = 20;

	private static final int RANK_TOP = 1;
	private static final SearchList[] searchList = { SearchList.getGlobalScoreSearchList(),
			SearchList.getTwentyFourHourScoreSearchList(), SearchList.getBuddiesScoreSearchList() };
	private static final int TAG_NEXT = 2;
	private static final int TAG_PREV = 1;
	private static final int TAG_TOP = 0;
	private ListItemAdapter adapter;
	private View dividerView;
	private LoadingType loadingType;
	private TextView modeText;
	private LinearLayout myScoreView;
	private ScoresController scoresController;
	private ListView scoresListView;

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(Menu.NONE, MENU_PROFILE, Menu.NONE, R.string.sl_profile).setIcon(R.drawable.sl_menu_profile);
		menu.add(Menu.NONE, MENU_GAMES, Menu.NONE, R.string.sl_games).setIcon(R.drawable.sl_menu_games);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PROFILE:
			startActivity(new Intent(HighscoresActivity.this, ProfileActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
			return true;
		case MENU_GAMES:
			startActivity(new Intent(HighscoresActivity.this, GamesActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void blockUI(final boolean flg) {
		scoresListView.setEnabled(!flg);
		myScoreView.setEnabled(!flg);
	}

	private void highlightView(final View view, final boolean flg) {
		final int c = flg ? getResources().getColor(R.color.sl_color_sl) : getResources().getColor(
				R.color.sl_color_foreground);
		((TextView) view.findViewById(R.id.text0)).setTextColor(c);
		((TextView) view.findViewById(R.id.text1)).setTextColor(c);
		((TextView) view.findViewById(R.id.text2)).setTextColor(c);
	}

	private void loadRangeForMe() {
		loadingType = LoadingType.ME;
		blockUI(true);
		setProgressIndicator(true);
		adapter.clear();
		adapter.add(new ListItem(getResources().getString(R.string.sl_loading)));
		scoresController.loadRangeForUser(Session.getCurrentSession().getUser());
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_highscores);

		updateStatusBar();
		updateHeading(getString(R.string.sl_highscores), false);

		dividerView = findViewById(R.id.divider);
		dividerView.setVisibility(View.GONE);

		myScoreView = (LinearLayout) findViewById(R.id.myscore_view);
		myScoreView.setVisibility(View.GONE);
		((TextView) myScoreView.findViewById(R.id.text0)).setText("");
		highlightView(myScoreView, true);
		myScoreView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				loadRangeForMe();
			}
		});

		scoresListView = (ListView) findViewById(R.id.list_view);
		scoresListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final ListItem listItem = (ListItem) adapterView.getItemAtPosition(position);
				if (listItem.isSpecialItem()) {
					loadingType = LoadingType.OTHER;
					blockUI(true);
					setProgressIndicator(true);
					adapter.clear();
					adapter.add(new ListItem(getResources().getString(R.string.sl_loading)));

					switch (listItem.getTag()) {
					case TAG_TOP:
						scoresController.loadRangeAtRank(RANK_TOP);
						break;
					case TAG_PREV:
						scoresController.loadPreviousRange();
						break;
					case TAG_NEXT:
						scoresController.loadNextRange();
						break;
					default:
						break;
					}
				} else {
					final User user = listItem.getScore().getUser();
					if (!user.equals(Session.getCurrentSession().getUser())) {
						ScoreloopManager.setUser(user);
						startActivity(new Intent(HighscoresActivity.this, UserActivity.class));
					}
				}
			}
		});

		final FrameLayout modeLayout = (FrameLayout) findViewById(R.id.layout_mode);
		if (ScoreloopManager.client.getGameModes().getLength() > 1) {
			modeLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					showDialogSafe(DIALOG_GAME_MODE);
				}
			});
		} else {
			modeLayout.setVisibility(View.GONE);
		}

		modeText = (TextView) findViewById(R.id.mode_text);
		modeText.setText(getResources().getStringArray(R.array.sl_game_modes)[0].toString());

		final SegmentedView segmentedView = (SegmentedView) findViewById(R.id.search_list_segments);
		segmentedView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {
				((TextView) myScoreView.findViewById(R.id.text0)).setText("");
				scoresController.setSearchList(searchList[segmentedView.getSelectedSegment()]);
				loadRangeForMe();
			}

		});

		adapter = new ListItemAdapter(HighscoresActivity.this, R.layout.sl_highscores, new ArrayList<ListItem>());
		scoresListView.setAdapter(adapter);

		scoresController = new ScoresController(new ScoresControllerObserver());
		scoresController.setRangeLength(RANGE_LENGTH);
		loadRangeForMe();
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		final Dialog dialog = super.onCreateDialog(id);
		if (dialog != null) {
			return dialog;
		}

		switch (id) {
		case DIALOG_GAME_MODE:
			return new AlertDialog.Builder(HighscoresActivity.this).setItems(R.array.sl_game_modes,
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, final int position) {
							modeText.setText(getResources().getStringArray(R.array.sl_game_modes)[position].toString());
							scoresController.setMode(position);
							((TextView) myScoreView.findViewById(R.id.text0)).setText("");
							loadRangeForMe();
						}
					}).create();
		default:
			return null;
		}
	}
}
