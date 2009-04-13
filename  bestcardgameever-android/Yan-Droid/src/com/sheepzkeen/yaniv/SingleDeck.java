package com.sheepzkeen.yaniv;

import java.util.Collections;
import java.util.Stack;

public class SingleDeck extends PlayingCardsCollection {
	

	public SingleDeck() {
		super();
		// Create a temporary deck
		Stack<PlayingCard> deckTemp = new Stack<PlayingCard>();
		// Add the 52 suit\value combinations
		for (String suit : suits)  {
			for (String value : values) {
				deckTemp.add(new PlayingCard(suit,value));
			}
			
		}
		// Now add the Jokers
		deckTemp.add(new PlayingCard("o","r"));
		deckTemp.add(new PlayingCard("o","b"));
		// Shuffle it
		Collections.shuffle(deckTemp);
		// And lets get it on!
		cards = deckTemp;
		
	}
	
	/**
	 * returns the next card from the deck and removes it
	 * @return The next card from the deck and removes it
	 */
	public PlayingCard dealOneCard(){
		return cards.pop();
		
		
		
	}

}
