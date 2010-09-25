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

import android.content.Context;

import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.ScoreController;
import com.scoreloop.client.android.core.model.Client;
import com.scoreloop.client.android.core.model.Game;
import com.scoreloop.client.android.core.model.Range;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.User;

public abstract class ScoreloopManager {

	private static Game game;
	private static ScoreController scoreController;
	private static User user;
	static Client client;
	static final int GAME_MODE_MIN = 0; // can be >= 0, but zero keeps the mapping to gameModeSpinner.getSelectedItemPosition()) simple

	public static Score getScore() {
		return scoreController.getScore();
	}

	// call this early, for example in myApplication.onCreate(); amongst other things a session object will be created, and subsequently
	// Session.getCurrentSession() != null
	public static void init(final Context context, final String gameID, final String gameSecret) {
		if (client == null) {
			client = new Client(context, gameID, gameSecret, null);
		}
	}

	public static void setNumberOfModes(final int modeCount) {
		if (client != null) {
			client.setGameModes(new Range(GAME_MODE_MIN, modeCount));
		} else {
			throw new IllegalStateException("client object is null. has ScoreloopManager.init() been called?");
		}
	}

	public static void submitScore(final int scoreValue, final int gameMode, final RequestControllerObserver observer) {
		final Score score = new Score((double) scoreValue, null);
		score.setMode(gameMode);
		scoreController = new ScoreController(observer);
		scoreController.submitScore(score);
	}

	public static void submitScore(final int scoreValue, final RequestControllerObserver observer) {
		submitScore(scoreValue, GAME_MODE_MIN, observer);
	}

	static Game getGame() {
		return game;
	}

	static User getUser() {
		return user;
	}

	static void setGame(final Game game) {
		ScoreloopManager.game = game;
	}

	static void setUser(final User user) {
		ScoreloopManager.user = user;
	}
	
}
