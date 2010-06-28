package com.geekadoo.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * this class represents a stack of cards that have been thrown to the table
 * @author Elad
 *
 */
public class ThrownCards extends PlayingCardsCollection {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ThrownCards() {
		super();
		this.cards = new Stack<PlayingCard>();
		
		System.out.println("Ctor"+ cards.empty() + " size: " + cards.size());
	}
	
	public void push(PlayingCard card){
		cards.push(card);
		System.out.println("push "+cards.empty() + " size: " + cards.size());
	}
	
	public void pushMulti(PlayingCard[] cards){
		if(cards.length >GameData.YANIV_NUM_CARDS)
			throw new RuntimeException("more than "+GameData.YANIV_NUM_CARDS+" cards were thrown " +
					"(actually " + cards.length + " cards were thrown");
		for (PlayingCard playingCard : cards) {
			push(playingCard);
		}
		System.out.println("pushMulti "+ this.cards.empty() + " size: " + this.cards.size());
	}
	
	
	/**
	 * returns the last 5 cards thrown where the last one thrown is the first in the array
	 * note that if there are only 3 thrown cards, the first 2 will be null
	 * @return the last 5 cards thrown where the last one thrown is the first in the array
	 */
	@SuppressWarnings("unchecked")
	public PlayingCard[] peekTopFive() {
		
		PlayingCard[] retVal = new PlayingCard[GameData.YANIV_NUM_CARDS];
		Stack<PlayingCard> tempStack = (Stack<PlayingCard>) cards.clone();
		for (int i = 0; i < GameData.YANIV_NUM_CARDS; i++) {
			if (tempStack.isEmpty()){
				retVal[i] = null;
			}else{
				retVal[i] = tempStack.pop();
			}
		}
		
		System.out.println("peekTopFive "+cards.empty() + " size: " + cards.size());

		return retVal;

	}

	/**
	 * @return All the cards except for the last YANIV_NUM_CARDS (5)
	 */
	public List<PlayingCard> popAllButTopFive() {
		List<PlayingCard> retVal;
		int fromIndex = 0;
		int toIndex = cards.size() - GameData.YANIV_NUM_CARDS;
		
		// Take out all the cards except for the last 5
		retVal = new ArrayList<PlayingCard>(cards.subList(fromIndex, toIndex));
		// Clear the thrown cards list from the cards that were taken
		cards.removeAll(retVal);
				
		return retVal;
	}
}