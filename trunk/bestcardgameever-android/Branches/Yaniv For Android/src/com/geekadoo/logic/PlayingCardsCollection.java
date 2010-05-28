package com.geekadoo.logic;

import java.io.Serializable;
import java.util.Collection;
import java.util.Stack;

/**
 * this abstract class represents a collection of cards
 * @author Elad
 */
public abstract class PlayingCardsCollection implements Serializable{
	private static final long serialVersionUID = 1L;
	
	protected Stack<PlayingCard> cards;
	protected char[] suits = {PlayingCard.CLUBS,PlayingCard.SPADES,PlayingCard.HEARTS,PlayingCard.DIAMOND};
	protected char[] values = {PlayingCard.ACE,'2','3','4','5','6','7','8','9',PlayingCard.TEN,
			PlayingCard.JACK,PlayingCard.QUEEN,PlayingCard.KING};

	public Collection<PlayingCard> getAllCards() {
		return cards;
	}

	public int getRemainingCardsNo() {
		return cards.toArray().length;
	}
	
	/**
	 * @return The next card from the Collection and removes it
	 */
	public PlayingCard popTopCard() {
		// If no cards, exception will be thrown - TODO: fix this bug for deck! (won't happen on thrown cards)
		PlayingCard retVal = cards.pop();
		return retVal;

	}
	
	public final PlayingCard peekTopCard(){
		return cards.peek();
	}
	
	public int count() {
		return cards.size();
	}
}