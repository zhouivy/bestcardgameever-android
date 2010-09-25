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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

//this part is included in the ScoreloopCoreUI doc
import com.geekadoo.R;

public class ProfileActivity extends BaseActivity {

	private final static int CONTENT_ACCOUNT = 0;
	private final static int CONTENT_BUDDIES = 1;
	private final static int CONTENT_BUDDIES_ADD = 2;

	private int currentSubContent;

	public void onBackPressed() {
		if (currentSubContent == CONTENT_BUDDIES_ADD) {
			setSubContent(CONTENT_BUDDIES);
		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(Menu.NONE, MENU_HIGHSCORES, Menu.NONE, R.string.sl_highscores).setIcon(R.drawable.sl_menu_highscores);
		menu.add(Menu.NONE, MENU_GAMES, Menu.NONE, R.string.sl_games).setIcon(R.drawable.sl_menu_games);
		return true;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
			onBackPressed();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case MENU_HIGHSCORES:
			startActivity(new Intent(ProfileActivity.this, HighscoresActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
			return true;
		case MENU_GAMES:
			startActivity(new Intent(ProfileActivity.this, GamesActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_profile);

		updateStatusBar();
		updateHeading(getString(R.string.sl_profile), true);

		final SegmentedView segmentedView = (SegmentedView) findViewById(R.id.segmented_view);
		segmentedView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {
				setSubContent(segmentedView.getSelectedSegment());
			}

		});

		setSubContent(CONTENT_ACCOUNT);
	}

	void setSubContent(final int index) {
		final LinearLayout view = (LinearLayout) findViewById(R.id.activity_container);
		if (view.getChildCount() != 0) {
			view.removeViewAt(0);
		}

		Class<? extends Activity> clazz;
		switch (index) {
		case CONTENT_ACCOUNT:
			clazz = AccountActivity.class;
			break;
		case CONTENT_BUDDIES:
			clazz = BuddiesActivity.class;
			break;
		case CONTENT_BUDDIES_ADD:
			clazz = BuddiesAddActivity.class;
			break;
		default:
			clazz = AccountActivity.class;
			break;
		}
		final Intent intent = new Intent(ProfileActivity.this, clazz).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // force onCreate to be called in embedded activites
		final Window window = getLocalActivityManager().startActivity(clazz.toString(), intent);
		view.addView(window.getDecorView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		currentSubContent = index;
	}

}
