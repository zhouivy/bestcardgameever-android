package com.sheepzkeen.yaniv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayerHand extends Hand {

	
	

	public PlayerHand(View container, ImageView[] cards, TextView name) {
		super(container,cards,name);
		//cards always visible for p1
		setShouldCardsBeShown(true);
	}

	

	//should Override
	@Override
	protected void selectCardsToDrop() {
		//Do nothing here, selection was performed by the drop action
		verifyCardsToDrop();
	}







	private void verifyCardsToDrop() {
		
		//Verify algorithm:
		// 1) remove jokers, add 1 to jokerCount for each joker
		// 2) if there is zero to one card only - allow
		// 3) check, if all cards are of different suits:
		// 3.	1) check for each card, if value = preceding value, allow, else reject 
		// 4) in case they are of the same suit:
		// 4.	1) if cards.count<3 , check for jokers, if exist - continue, else reject
		// 4.	2) remove aces, put aside
		// 4.	3) sort hand from highest to low
		// 4.	4) iterate over it, for each card - compare to next card 
		// 4.	4.	1) if this card and the next card have a difference of exactly 1 - continue, else:
		// 4.	4.	1.	2) (if difference>1) check that difference-1 <= jokerCount, if so, subtract difference-1 from jokerCount
		// 5) see if ace existed, if so, check if it can be attached to end\start (check highest card + jokercount == 14 or lowest card - jokerCount == 1)
		int jokerCount = 0;
		Character currentSuit = null;
		boolean allCardsAreSameSuit = true;
		ArrayList<PlayingCard> cardsToCheck  = (ArrayList<PlayingCard>) Arrays.asList(cards);
		for (PlayingCard card : cardsToCheck) {
			if(card.getIntegerValue() == null){
				jokerCount++;
				cardsToCheck.remove(card);
			}else{
				if(allCardsAreSameSuit && (card.getSuit() == currentSuit || currentSuit == null))
				{
					currentSuit = card.getSuit();
				}else{
					allCardsAreSameSuit = false;
				}
					
			}
			
			
		}
		Collections.sort(cardsToCheck);
		
		
	}



	@Override
	public void pickup(PlayingCard card) {
		addCard(card);
	}
	
	

	/**
	 * returns true iff the player is currently in a state when he can pickup a card (from the thrown or deck)
	 * @return True iff the player is currently in a state when he can pickup a card (from the thrown or deck)
	 */
	public boolean canPickup() {
		return (firstFreeLocation != Yaniv.YANIV_NUM_CARDS);
	}
	
	@Override
	public boolean isAwaitingInput() {
		return true;
	}
	

}
