package com.geekadoo.logic;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import android.util.Log;

/**
 * This class represents a deck of 54 cards that includes 13 cards X 4 suits + 2 jokers
 * it has the ability to dispense a card
 * @author Elad
 */
public class SingleDeck extends PlayingCardsCollection {
	private static final long serialVersionUID = 1L;
	private static final String LOG_TAG = "SingleDeck";

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
	
	public void addCards(List<PlayingCard> cardsToAdd) {
		cards.addAll(cardsToAdd);
	}
	
	@Override
	public PlayingCard popTopCard() {
		// If there are no cards, refill deck 
		if (cards.isEmpty()) {
			Log.w(LOG_TAG,"Deck was empty, refilling...");
			GameData.getInstance().refillDeck();
		}

		PlayingCard retVal = cards.pop();
		return retVal;
	}

}