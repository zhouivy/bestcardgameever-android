package com.sheepzkeen.yaniv;

import java.util.Collection;
import java.util.Stack;

public abstract class PlayingCardsCollection {

	protected Stack<PlayingCard> cards;
	protected String[] suits = {"c","s","h","s"};
	protected String[] values = {"1","2","3","4","5","6","7","8","9","10",
								"j","q","k"};

	

	public Collection<PlayingCard> getAllCards() {
		return cards;
	}

	public int getRemainingCardsNo() {
		return cards.toArray().length;
	}

}