package com.sheepzkeen.yaniv;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * a Serializable Singleton that holds the game data
 * @author Elad
 *
 */
public class GameData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4564493907808892053L;
	// Player 1 - Logic elements
	private PlayerHand p1Hand;
	// Opponent 1 - Logic elements
	private OpponentHand o1Hand;
	// Opponent 2 - Logic elements
	private OpponentHand o2Hand;
	// Opponent 3 - Logic elements
	private OpponentHand o3Hand;
	//Thrown Cards - Logic elements	
	private ThrownCards thrownCards;
	//Turn - Logic elements
	private Turn<Hand> turn;
	//Deck - Logic elements
	SingleDeck deck;
	private ArrayList<Hand> playersInOrder;
	// Flag to indicate if this is the beginning of the game or middle
	private boolean gameInProgress;

	// Eagerly created instance
	private static GameData instance = new GameData();
	
	private GameData(){
		//Do nothing
	}
	
	public static GameData getInstance() {
		return instance;
	}

	protected void init(PlayerHand hand, OpponentHand hand2, OpponentHand hand3,
			OpponentHand hand4, ThrownCards thrownCards, 
			SingleDeck deck, Turn<Hand> turn, ArrayList<Hand> playersInOrder) {
		this.p1Hand = hand;
		this.o1Hand = hand2;
		this.o2Hand = hand3;
		this.o3Hand = hand4;
		this.thrownCards = thrownCards;
		this.deck = deck;
		this.turn = turn;
		this.playersInOrder = playersInOrder;
		this.gameInProgress = false;
		turn.addOnTurnEndedListener(new Turn.OnTurnEndedListener<Hand>(){
			@Override
			public void onTurnEnded(Hand currentPlayer) {
				//Log.v("Roey","P1:" + p1Hand +", O1: "+ o1Hand+", O2: "+ o2Hand +", O3: "+ o3Hand);
			}
		});
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

	public ThrownCards getThrownCards() {
		return thrownCards;
	}

	public Turn<Hand> getTurn() {
		return turn;
	}

	public SingleDeck getDeck() {
		return deck;
	}
	
	public ArrayList<Hand> getPlayersInOrder() {
		return playersInOrder;
	}

	public boolean isGameInProgress() {
		return gameInProgress;
	}

	public void setGameInProgress(boolean gameInProgress) {
		this.gameInProgress = gameInProgress;
	}
}