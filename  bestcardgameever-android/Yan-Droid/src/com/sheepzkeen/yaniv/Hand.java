package com.sheepzkeen.yaniv;

import java.util.ArrayList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public abstract class Hand {
	private PlayingCard[] cards;
	int firstFreeLocation;
	private PlayingCard[] compactedArr;
	private boolean[] cardsSelected = {false,false,false,false,false};
	private ImageView[] cardsViews;
	public ImageView[] getCardsViews() {
		return cardsViews;
	}
	public View getContainer() {
		return container;
	}

	private View container;
	private String name;
	private boolean canDrop;
	private boolean canPickup;

	public Hand(View container, ImageView[] cardsViews, TextView name) {

		this.cards = new PlayingCard[Yaniv.YANIV_NUM_CARDS];
		firstFreeLocation = 0;
		this.container = container;
		this.cardsViews = cardsViews;
		this.name = (String) name.getText();
		this.canDrop = true;
		this.canPickup = false;
	}
	
//	public Hand(PlayingCard[] cards) {
//		this.cards = cards;
//		firstFreeLocation = cards.length;
//	}
	
	/**
	 * Hand picks up a card
	 * first decide if should pick up from deck or thrown cards
	 * then do it
	 * AI players will pick up according to AI
	 * human player will pick up on his own
	 * @param deck
	 */
	public abstract void pickup(PlayingCard card);
	
	public PlayingCard getCardByLocation(int location) {
		return cards[location];
	}

	/**
	 * Selected which cards, and drops them, in case it is a human player, it simply drops the cards he selected
	 * in case of an opponent, AI decides it
	 * @return an array of type {@link PlayingCard} which contains the cards to drop 
	 */
	public PlayingCard[] drop(){
		selectCardsToDrop();
		return dropSelected();
	}

	/**
	 * Selects which cards will be dropped and moves them to the 'cardsSelected' array 
	 */
	protected abstract void selectCardsToDrop();
	
	/**
	 * returns the selected cards so that they can be thrown
	 * @return The cards to be thrown
	 */
	private PlayingCard[] dropSelected() {
		ArrayList<PlayingCard> cardsToDrop = new ArrayList<PlayingCard>();
		for (int cardIndex = 0; cardIndex < cards.length; cardIndex++) {
			if(cardsSelected[cardIndex]){
				cardsToDrop.add(cards[cardIndex]);
				cards[cardIndex]=null;
			}
		}
		compactHand();
		resetSelectedCards();
		PlayingCard[] retval = new PlayingCard[cardsToDrop.size()];
		cardsToDrop.toArray(retval);
		return retval;
	}


	public boolean canYaniv(){
		//TODO: This (need to add int value to card objects to sum them)
		return false;
	}
	
	public abstract void doYaniv();
	
//	/**
//	 * plays a turn
//	 * @param currentDeck the deck in its current state (missing cards and all)
//	 * @return true iff the player has declared Yaniv and the game has ended
//	 */
//	public boolean playTurn(PlayingCardsCollection currentDeck) {
//		if(canYaniv())
//		{
//			doYaniv();
//			return true;
//		}
//		else
//		{
//			pickup();
//			drop();
//			return false;
//		}
//		
//	}

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
	protected int addCard(PlayingCard card) {
		cards[firstFreeLocation] = card;
		return firstFreeLocation++;
		
	}
	
	/**
	 * compacting-filling the first cards and leaving the blanks towards the end of the hand
	 * for instance if the hand was: {3h,null,null,joker,9h}
	 * after compacting it will be: {3h,joker,9h,null,null}
	 */
	protected void compactHand(){
		compactedArr = new PlayingCard[Yaniv.YANIV_NUM_CARDS];
		int idxInComapctedArr=0;
		for (int idxInCards = 0; idxInCards < cards.length; idxInCards++) {
			if (cards[idxInCards]!=null) {
				compactedArr[idxInComapctedArr++] = cards[idxInCards];
			}
		}
		this.cards = compactedArr;
		this.firstFreeLocation = idxInComapctedArr;
	}
	/**
	 * resets all cards selected status to false
	 */
	protected void resetSelectedCards(){
		for (int i = 0; i < cardsSelected.length; i++) {
			cardsSelected[i]=false;
		}
	}
	
	public boolean isCardSelected(int cardIndex){
		return cardsSelected[cardIndex];
	}
	
	public boolean isAnyCardSelected(){
		for (int i = 0; i < cardsSelected.length; i++) {
			if (cardsSelected[i]== true){
				return true;
			}
		}
		return false;
	}

	public void changeSelectionStateOnCard(int cardIndex) {
		cardsSelected[cardIndex]= ! cardsSelected[cardIndex];
	}
	
	/**
	 * will return true for a human player
	 * @return True iff the player is human
	 */
	abstract public boolean isAwaitingInput();
	
	public String getPlayerName() {
		
		return this.name;
	}
	public boolean getCanDrop(){
		return canDrop;
	}
	public void setCanDrop(boolean canDrop) {
		this.canDrop = canDrop;
	}
	public boolean getCanPickup() {
		return this.canPickup;
	}
	public void setCanPickup(boolean canPickup) {
		this.canPickup = canPickup;
	}
	
	
	


}
