package com.geekadoo.logic;

import java.io.Serializable;
import java.util.ArrayList;

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
	public static final String GAMEDATA_PARAMNAME = "gameData";

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
	}

	public static GameData getInstance() {
		// TODO: change the loading of the GameData to be performed in 
		// GameData - on first access (load from disc)
		if (gameData == null) {
			gameData = new GameData();
		}
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
	
}