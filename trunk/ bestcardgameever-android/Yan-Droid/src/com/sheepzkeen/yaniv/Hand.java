package com.sheepzkeen.yaniv;

public abstract class Hand {
	protected PlayingCard[] cards;
	int firstFreeLocation;
	private PlayingCard[] compactedArr;
	public PlayingCard getCardByLocation(int location) {
		return cards[location];
	}
	public Hand() {
		this.cards = new PlayingCard[5];
		firstFreeLocation = 0;
	}
	public Hand(PlayingCard[] cards) {
		this.cards = cards;
		firstFreeLocation = 5;
	}
	public abstract void pickup(PlayingCardsCollection deck);
	//		after Drop compact hand (i.e. if cards 2 and 4 were dropped ,
	//		compact the deck so it will show the missing cards at the end)
	public abstract void drop(); 
	public boolean canYaniv(){
		//TODO: This
		return false;
	}
	public abstract void doYaniv();
	/**
	 * plays a turn
	 * @param currentDeck the deck in its current state (missing cards and all)
	 * @return true iff the player has declared Yaniv and the game has ended
	 */
	public boolean playTurn(PlayingCardsCollection currentDeck) {
		if(canYaniv())
		{
			doYaniv();
			return true;
		}
		else
		{
			pickup(currentDeck);
			drop();
			return false;
		}
		
	}

	/**
	 * returns true for player and false for opponent	
	 * @return true if this is an instance of player, false otherwise
	 */
	public abstract boolean shouldCardsBeVisible();
	
	/**
	 * adds a card to the hand based on the first free location (@see firstFreeLocation)
	 * returns the slot it was added in
	 * @param card
	 * @return
	 */
	public int addCard(PlayingCard card) {
		cards[firstFreeLocation] = card;
		return firstFreeLocation++;
		
	}
	
	/**
	 * compacting-filling the first cards and leaving the blanks towards the end of the hand
	 * for instance if the hand was: {3h,null,null,joker,9h}
	 * after compacting it will be: {3h,joker,9h,null,null}
	 */
	protected void compactHand(){
		compactedArr = new PlayingCard[5];
		int idxInComapctedArr=0;
		for (int idxInCards = 0; idxInCards < cards.length; idxInCards++) {
			if (cards[idxInCards]!=null) {
				compactedArr[idxInComapctedArr++] = cards[idxInCards];
			}
		}
		this.cards = compactedArr;
		this.firstFreeLocation = idxInComapctedArr;
	}
	public abstract boolean isCardSelected(int cardIndex);
	public abstract boolean isAnyCardSelected();
	
}
