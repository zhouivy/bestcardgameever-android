package com.sheepzkeen.yaniv;

import java.util.Collections;
import java.util.Stack;

/**
 * This class represents a deck of 54 cards that includes 13 cards X 4 suits + 2 jokers
 * it has the ability to dispense a card
 * @author Elad
 *
 */
public class SingleDeck extends PlayingCardsCollection {
	



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SingleDeck() {
		super();
		// Create a temporary deck
		Stack<PlayingCard> deckTemp = new Stack<PlayingCard>();
		// Add the 52 suit\value combinations
		for (char suit : suits)  {
			for (char value : values) {
				deckTemp.add(new PlayingCard(suit,value));
			}
			
		}
		// Now add the Jokers
		deckTemp.add(new PlayingCard(PlayingCard.RED_SUIT,PlayingCard.JOKER));
		deckTemp.add(new PlayingCard(PlayingCard.BLACK_SUIT,PlayingCard.JOKER));
		// Shuffle it
		Collections.shuffle(deckTemp);
		// And lets get it on!
		cards = deckTemp;
		
	}
	


}
