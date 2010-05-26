package com.geekadoo.logic;

import java.io.Serializable;

/**
 * a Serializable Object that holds the game data
 * @author Elad
 */
public class GameData implements Serializable {
	private static final long serialVersionUID = -4564493907808892053L;
//TEST
	Integer testInt = 0;
	public Integer promoteTestInt(){
		return testInt++;
	}
	public Integer getTestInt(){
		return testInt;
	}
//END-TEST
	
	private GameData() {
		// Do nothing - just hide from the rest of the world :)
	}

	public static final int YANIV_NUM_CARDS = 5;
	public static final int START_GAME = 1000;
	public static final String STATE = "state";
	public static final int RESUME_GAME = 2000;

	public static enum STATES {
		start, resume, end
	};

	public static final int DEFAULT_STARTING_PLAYER = 0;
	public static final String GAMEDATA_PARAMNAME = "gameData";

	private boolean gameInProgress;

	public boolean isGameInProgress() {
		return gameInProgress;
	}

	public void setGameInProgress(boolean gameInProgress) {
		this.gameInProgress = gameInProgress;
	}

	public static GameData createNewGame() {
		return new GameData();
	}
}