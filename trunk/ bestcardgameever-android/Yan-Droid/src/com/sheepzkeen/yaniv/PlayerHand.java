package com.sheepzkeen.yaniv;

import java.util.ArrayList;

public class PlayerHand extends Hand {

	protected boolean[] cardsSelected = {false,false,false,false,false};
	private ArrayList<PlayingCard> cardsToDrop;
	private boolean isP1Turn;
	
	

	public PlayerHand() {
		super();
		cardsToDrop = new ArrayList<PlayingCard>();
		isP1Turn = true;//TODO!!!
	}
	






	@Override
	public void doYaniv() {
		// TODO this
		
	}

	@Override
	public void drop() {
		dropSelected();		
	}

	//will move selected cards to a temporary array, so that they can be moved to the thrown cards deck
	private void dropSelected() {
		for (int cardIndex = 0; cardIndex < cards.length; cardIndex++) {
			if(cardsSelected[cardIndex]){
				cardsToDrop.add(cards[cardIndex]);
				cards[cardIndex]=null;
			}
		}
		compactHand();
		resetSelectedCards();
	}

	@Override
	public boolean shouldCardsBeVisible() {
		// Always true for player
		return true;
	}

	@Override
	public void pickup(PlayingCardsCollection deck) {
		// TODO this is already implemented, but in order to play a round, this should be implemented
		
	}
	
	public void changeSelectionStateOnCard(int cardIndex) {
		cardsSelected[cardIndex]= ! cardsSelected[cardIndex];
		
	}
	
	/**
	 * resets all cards selected status to false
	 */
	public void resetSelectedCards(){
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

	/**
	 * returns true iff the player is currently in a state when he can pickup a card (from the thrown or deck)
	 * @return True iff the player is currently in a state when he can pickup a card (from the thrown or deck)
	 */
	public boolean canPickup() {
		// TODO need to activate the turn param
		return (isP1Turn && firstFreeLocation != 5);
	}
	
	/**
	 * returns an array of cards selected to drop and clears the selection
	 * @return
	 */
	public PlayingCard[] getCardsToDrop() {
		PlayingCard[] dropCardsArr = new PlayingCard[cardsToDrop.size()];
			cardsToDrop.toArray(dropCardsArr);
			cardsToDrop.clear();
		return dropCardsArr;
	}
}
