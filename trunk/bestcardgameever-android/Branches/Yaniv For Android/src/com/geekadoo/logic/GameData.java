package com.geekadoo.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.util.Log;

import com.geekadoo.db.YanivPersistenceAdapter;
import com.geekadoo.exceptions.YanivPersistenceException;
import com.geekadoo.logic.ai.BasicYanivStrategy;
import com.geekadoo.ui.ScoresDialog;

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
	public static final String[] PLAYER_NAMES = {"Player", "Sivan", "Iddo", "Elad"};

	private static final int ASSAF_PENALTY = 30;

	private static final int WIN_SCORE = 0;

	private static final Integer MATCH_LOSING_SCORE = 200; 

	/** This flag is used for debugging the game */
	private final boolean disableOpponentsYanivAbility = false;
	
	public static enum GAME_STATES {
		start, resume, end
	}

	public static enum GAME_INPUT_MODE {
		paused, running
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

	private GAME_INPUT_MODE mode;

	private GameData() {
		if (disableOpponentsYanivAbility) {
			Log.e(LOG_TAG, "*************************\n disableOpponentsYanivAbility IS TRUE!!!!!\n****************");
		}

		// Array of order of players in the beginning of the game (p1 is first)
		playersInOrder = new ArrayList<Hand>();
		p1Hand = new PlayerHand();
		playersInOrder.add(p1Hand);
		o1Hand = new OpponentHand(new BasicYanivStrategy());
		playersInOrder.add(o1Hand);
		o2Hand = new OpponentHand(new BasicYanivStrategy());
		playersInOrder.add(o2Hand);
		o3Hand = new OpponentHand(new BasicYanivStrategy());
		playersInOrder.add(o3Hand);
		currentGameNumber = 0;
		startNewGame(p1Hand,true);
	}

	public static GameData getInstance(boolean firstRun, Context appCtx) {
		persistencAdapter = new YanivPersistenceAdapter(appCtx);

		if (!firstRun) {
			// Existing game, read it from the persistence provider
			try {
				gameData = persistencAdapter.getSavedGameData();
			} catch (YanivPersistenceException e) {
				// TODO: pop up a sorry box and report this problem... - could
				// not load, creating new.
				Log.e(LOG_TAG,
						"GameData could not load state, creating new gamedata");
				gameData = createNewGame();
			}
		} else {
			// First run: Override existing game in memory
			gameData = createNewGame();
		}
		return gameData;
	}

	public void save(Context applicationContext)
			throws YanivPersistenceException {
		Log.e(LOG_TAG, "persistenceAdapter is " + persistencAdapter);
		persistencAdapter.setSavedGameData(this);
	}

	public static GameData getInstance() {
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

	public List<String> getScoreRepresentation() {
		// TODO:game number column as well.
		List<String> retVal = new ArrayList<String>();

		// First row is column titles: game number & player names
		retVal.add("Game");
		for (Hand h : playersInOrder) {
			retVal.add((String) h.getPlayerName());
		}

		// Contents: actual scores
		// For each game in history
		for (int gameNumber = 0; gameNumber < getCurrentGameNumber(); gameNumber++) {
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

		return retVal;
	}

	public int getCurrentGameNumber() {
		return currentGameNumber;
	}

	public void addRoundScores() {
		for (Hand h : playersInOrder) {
			if(h.isWasAssaffed()){
				h.setWasAssaffed(true);
				h.addToScoreHistory(ASSAF_PENALTY + h.sumCards());
			}else{
				if(h.isWonRound()){
					h.setWonRound(false);
					h.addToScoreHistory(WIN_SCORE);
				}else{
					h.addToScoreHistory(h.sumCards());
				}
			}

		}
	}

	public void startNewGame(Hand startingHand, boolean isFirstGameInMatch) {
		if (!isFirstGameInMatch) {
			// advance game number
			currentGameNumber++;
			// reset hands
			p1Hand.reset();
			o1Hand.reset();
			o2Hand.reset();
			o3Hand.reset();
			// Set the starting hand
			turn.newRound(playersInOrder.indexOf(startingHand), false);
		}else{
			firstDeal = true;
			this.turn = new Turn<Hand>(playersInOrder, playersInOrder.indexOf(startingHand));
		}
		this.thrownCards = new ThrownCards();
		this.deck = new SingleDeck();
		this.mode = GAME_INPUT_MODE.running;
	}

	public void setGameInputMode(GAME_INPUT_MODE mode) {
		this.mode = mode;
	}

	public GAME_INPUT_MODE getGameInputMode() {
		return mode;
	}

	/**
	 * This method will use the thrown cards to re-create the deck (after shuffling them)
	 */
	public void refillDeck() {
		// Obtain the cards to be re-used
		List<PlayingCard> usedCards = getThrownCards().popAllButTopFive();
		
		Collections.shuffle(usedCards);
		getDeck().addCards(usedCards);
	}

	public boolean isMatchWon() {
////		//remove this FIXME TODO
//			return true;
////		//
		boolean retVal = false;
		for(int i = 0 ; i<playersInOrder.size() && !retVal ; i++){
			Hand hand = playersInOrder.get(i);
			retVal = hand.getSumScores() > MATCH_LOSING_SCORE;
		}
		return retVal;
	}
}