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

import com.scoreloop.client.android.core.model.Game;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.User;

class ListItem { // a dumb container

	private Game game;
	private String label;
	private Score score;
	private int tag;
	private User user;

	ListItem(final Game game) {
		this.game = game;
	}

	ListItem(final Score score) {
		this.score = score;
	}

	ListItem(final String label) {
		this.label = label;
	}

	ListItem(final String label, final int tag) {
		this.label = label;
		this.tag = tag;
	}

	ListItem(final User user) {
		this.user = user;
	}

	Game getGame() {
		return game;
	}

	String getLabel() {
		return label;
	}

	Score getScore() {
		return score;
	}

	int getTag() {
		return tag;
	}

	User getUser() {
		return user;
	}

	boolean isSpecialItem() {
		return ((user == null) && (score == null) && (game == null));
	}
}
