package com.sheepzkeen.yaniv;

import java.util.Stack;

public class ThrownCards extends PlayingCardsCollection {
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
		if(cards.length >Yaniv.YANIV_NUM_CARDS)
			throw new RuntimeException("more than "+Yaniv.YANIV_NUM_CARDS+" cards were thrown " +
					"(actually " + cards.length + " cards were thrown");
		for (PlayingCard playingCard : cards) {
			push(playingCard);
		}
		System.out.println("pushMulti "+ this.cards.empty() + " size: " + this.cards.size());
	}
	
	public PlayingCard popLastCardThrown() {
//		System.out.println("getLastCardThrown B4: Cards: " + cards.toString());

		//if no cards, exception will be thrown, but that shouldn't happen since the views will  not be visible if no cards are thrown
		PlayingCard retVal = cards.pop();
//
//		System.out.println("getLastCardThrown "+ cards.empty() + " size: " + cards.size());
//		System.out.println("getLastCardThrown Fter: Cards: " + cards.toString());
		return retVal;

	}
	
	/**
	 * returns the last 5 cards thrown where the last one thrown is the first in the array
	 * note that if there are only 3 thrown cards, the first 2 will be null
	 * @return the last 5 cards thrown where the last one thrown is the first in the array
	 */
	@SuppressWarnings("unchecked")
	public PlayingCard[] peekTopFive() {
		
		PlayingCard[] retVal = new PlayingCard[Yaniv.YANIV_NUM_CARDS];
		Stack<PlayingCard> tempStack = (Stack<PlayingCard>) cards.clone();
		for (int i = 0; i < Yaniv.YANIV_NUM_CARDS; i++) {
			if(tempStack.isEmpty()){
				retVal[i]=null;
			}else{
				retVal[i] = tempStack.pop();
			}
		}
		
		System.out.println("peekTopFive "+cards.empty() + " size: " + cards.size());

		return retVal;

	}

}
