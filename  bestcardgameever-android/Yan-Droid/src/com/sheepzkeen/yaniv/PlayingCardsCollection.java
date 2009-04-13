package com.sheepzkeen.yaniv;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

public abstract class PlayingCardsCollection {

//	 public static final Map<String, Integer> cardToValue = new HashMap<String, Integer>();
//	 static {
//		 cardToValue.put("1", 1);
//		 //...
//	 }

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