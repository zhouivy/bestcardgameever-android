package com.geekadoo.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.geekadoo.db.YanivPersistenceAdapter;
import com.geekadoo.exceptions.YanivPersistenceException;
import com.geekadoo.logic.ai.BasicYanivStrategy;

/**
 * a Serializable Object that holds the game data
 * 
 * @author Elad
 */
public class GameData implements Serializable {
	private static final long serialVersionUID = -4564493907808892053L;

	public static final int YANIV_NUM_CARDS = 5;
	public static final int START_GAME = 1000;
	public static final int RESUME_GAME = 2000;
	public static final int DEFAULT_STARTING_PLAYER = 0;
	public static final String STATE = "state";
	public static final String LOG_TAG = "gameData";

	public static enum STATES {
		start, resume, end
	}

	private static GameData gameData = null;

	// Player 1 - Logic elements
	private PlayerHand p1Hand;
	// Opponent 1 - Logic elements
	private OpponentHand o1Hand;
	// Opponent 2 - Logic elements
	private OpponentHand o2Hand;
	// Opponent 3 - Logic elements
	private OpponentHand o3Hand;
	// Thrown Cards - Logic elements
	private ThrownCards thrownCards;
	// Turn - Logic elements
	private Turn<Hand> turn;
	// Deck - Logic elements
	SingleDeck deck;
	private ArrayList<Hand> playersInOrder;

	private boolean firstDeal;

	private int currentGameNumber;

	private static YanivPersistenceAdapter persistencAdapter;

	private GameData() {
		firstDeal = true;
		// array of order of players in the beginning of the game (p1 is first)
		playersInOrder = new ArrayList<Hand>();
		p1Hand = new PlayerHand();
		playersInOrder.add(p1Hand);
		o1Hand = new OpponentHand(new BasicYanivStrategy());
		playersInOrder.add(o1Hand);
		o2Hand = new OpponentHand(new BasicYanivStrategy());
		playersInOrder.add(o2Hand);
		o3Hand = new OpponentHand(new BasicYanivStrategy());
		playersInOrder.add(o3Hand);
		this.thrownCards = new ThrownCards();
		this.deck = new SingleDeck();
		this.turn = new Turn<Hand>(playersInOrder,
				GameData.DEFAULT_STARTING_PLAYER);
		currentGameNumber = 0;
	}

	public static GameData getInstance(boolean firstRun, Context appCtx) {
		// TODO: change the loading of the GameData to be performed in 
		// GameData - on first access (load from disc)
		persistencAdapter = new YanivPersistenceAdapter(appCtx);
		
		if(!firstRun){
			// Existing game, read it from the persistence provider
    		try{
    			gameData = persistencAdapter.getSavedGameData();
    		}catch (YanivPersistenceException e) {
    			// TODO: pop up a sorry box and report this problem... - could not load, creating new.
    			Log.e(LOG_TAG,"GameData could not load state, creating new gamedata");
    			gameData = createNewGame();
    		}
    	}
		else{
    		// First run: Override existing game in memory 
    		gameData = createNewGame();
    	}
		return gameData;
	}
	public void save(Context applicationContext) throws YanivPersistenceException {
		Log.e(LOG_TAG,"persistenceAdapter is " + persistencAdapter);
		persistencAdapter.setSavedGameData(this);
	}
	
	public static GameData getInstance(){
		return gameData;
	}

	public static GameData createNewGame() {
		gameData = new GameData();
		return gameData;
	}

	public ThrownCards getThrownCards() {
		return thrownCards;
	}

	public Turn<Hand> getTurn() {
		return turn;
	}

	public SingleDeck getDeck() {
		return deck;
	}

	public PlayerHand getP1Hand() {
		return p1Hand;
	}

	public OpponentHand getO1Hand() {
		return o1Hand;
	}

	public OpponentHand getO2Hand() {
		return o2Hand;
	}

	public OpponentHand getO3Hand() {
		return o3Hand;
	}

	public boolean isFirstDeal() {
		return firstDeal;
	}

	public void setFirstDeal(boolean firstDeal) {
		this.firstDeal = firstDeal;
	}

	public ArrayList<Hand> getPlayersInOrder() {
		return playersInOrder;
	}

	public void setPlayersInOrder(ArrayList<Hand> playersInOrder) {
		this.playersInOrder = playersInOrder;
	}

	public List<String> getScoreRepresentation(){
		//TODO:game number column as well.
		List<String> retVal = new ArrayList<String>();
		
		// First row is column titles: game number & player names
		retVal.add("Game");
		for (Hand h : playersInOrder) {
			retVal.add((String) h.getPlayerName());
		}
		
		// Contents: actual scores
		// For each game in history
		for(int gameNumber = 0; gameNumber < getCurrentGameNumber(); gameNumber++){
			Log.e("Sivan", "gameNumber = " + gameNumber + ", String.valueOf = " + String.valueOf(gameNumber));
			// Game count is zero based, so when printing we need to add 1
			retVal.add(String.valueOf(gameNumber + 1));
			// Get history for that game
			for (Hand hand : playersInOrder) {
				// For each player
				retVal.add(hand.getScoreHistory().get(gameNumber).toString());
			}
		}
		// Sum
		retVal.add("Totals:");
		for (Hand h : playersInOrder) {
			// Get the sum for each player
			retVal.add(h.getSumScores().toString());
		}

		Log.e("Sivan", retVal.toString());
		return retVal;
	}

	private int getCurrentGameNumber() {
		return currentGameNumber;
	}

	public void addRoundScores() {
		for (Hand h : playersInOrder) {
			h.addToScoreHistory(h.sumCards());
		}
	}

	public void startNewGame() {
		// TODO Auto-generated method stub
		currentGameNumber++;
		//TODO:THIS!!!
	}
}