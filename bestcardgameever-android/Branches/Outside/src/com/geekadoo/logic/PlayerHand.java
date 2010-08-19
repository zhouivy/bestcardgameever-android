package com.geekadoo.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.geekadoo.exceptions.InvalidDropException;

/**
 * Descendant of the Hand class @see {@link Hand}
 * This is a hand that is controlled by a human player
 * @author Elad
 */
public class PlayerHand extends Hand {
	private static final long serialVersionUID = 1L;

	public PlayerHand() {
		super();
		// The cards always visible for p1
		setShouldCardsBeShown(true);
		// 
		
	}


	@Override
	/** 
	 * Overrides the computer AI logic
	 */
	protected void selectCardsToDrop() throws InvalidDropException {
		// No need to select cards - the player did that for us, just verify
		verifyCardsToDrop();
	}

	private void verifyCardsToDrop() throws InvalidDropException {
		// Verify algorithm:
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

		//0 - initialize
		int jokerCount = 0;
		ArrayList<PlayingCard> jokers = new ArrayList<PlayingCard>();
		boolean suitsAreDifferent = false;
		PlayingCard aceInCards = null;
		List<PlayingCard> cardsToCheck = new ArrayList<PlayingCard>();
		for (PlayingCard card : cards) {
			if(card != null && card.isSelected()){
				cardsToCheck.add(card);
			}
		}
		//1 - jokers - existence
		for (PlayingCard card : cardsToCheck) {
			if(card.getIntegerValue() == null){
				jokerCount++;
				jokers.add(card);
			}
		}
		// jokers removal
		if(jokerCount>0){
			cardsToCheck.removeAll(jokers);
		}
		//2 - single card?
		if(cardsToCheck.size()<=1){
			//allow
			return;
		}
		
		//3	different suits?
		char suit = cardsToCheck.get(0).getSuit(); // get the suit of the first card
		for (PlayingCard card : cardsToCheck) 
		    {
			    if(suit != card.getSuit())
				{
					suitsAreDifferent = true;
					break;
			    }
			}
		
		//3.1 check same value
		PlayingCard lastCardChecked = null;
		if(suitsAreDifferent){
			for (PlayingCard card : cardsToCheck) {
				if(lastCardChecked != null && lastCardChecked.getIntegerValue() != card.getIntegerValue()){
					//Reject
					throw new InvalidDropException("(YE001)",
							"Cards of different suits must have same value!");
				}
				lastCardChecked = card;
			}
		}else{
			//4 check series
			//4.1 not enough cards
			if(cardsToCheck.size() < 3 && jokerCount == 0){ //todo:change to cardstochek.size()+jokercount<3
				//Reject
				throw new InvalidDropException("(YE002)",
						"Cards have same suit, but are not enough to complete a series! " +
						"(You need at least 3 cards to complete a series but only " + cardsToCheck.size() + " cards dropped and no jokers)");
			}
			//4.2 ace removal
			
			for (PlayingCard card : cardsToCheck) {
				if(card.getIntegerValue() == Character.digit(PlayingCard.ACE, 10)){
					aceInCards = card;
					
				}
			}
			if(aceInCards !=null){
				cardsToCheck.remove(aceInCards);
			}
			//4.3 sort from high to low
			Collections.sort(cardsToCheck);
			//4.4 - check descending series
			for (int i = 0; i < cardsToCheck.size()-1; i++) {
				int differenceBetweenThisAndNextCardVal =
					cardsToCheck.get(i).getIntegerValue() - cardsToCheck.get(i+1).getIntegerValue();
				//4.4.1
				if( differenceBetweenThisAndNextCardVal != 1){
					//4.4.1.2
					if(differenceBetweenThisAndNextCardVal - 1 > jokerCount){
						//reject
						throw new InvalidDropException("(YE003)","Cards have same suit but do not complete a series - " +
								"the difference between them is too big"+ (jokerCount>0? ", even with your jokers. ":".") +
								"(cards: "+ cardsToCheck.get(i) + " and " + cardsToCheck.get(i+1)+")");
					}else{
						jokerCount = jokerCount - (differenceBetweenThisAndNextCardVal - 1);
					}
				}
					
			}
		}		
		//5
		if(aceInCards != null){
					//in case of:   highest card = Q , jokerCount = 1
			if(     (cardsToCheck.get(0).getIntegerValue() + jokerCount == 13) ||
					//in case of:   highest card = Q , jokerCount = 2
					(cardsToCheck.get(0).getIntegerValue() + jokerCount == 14)||
					//in case of:   lowest card = 3 , jokerCount = 1
					(cardsToCheck.get(cardsToCheck.size()-1).getIntegerValue() - jokerCount == 2) ||
					//in case of:   lowest card = 3 , jokerCount = 2
					(cardsToCheck.get(cardsToCheck.size()-1).getIntegerValue() - jokerCount == 1) ||
					//in case of:   highest card = K
					(cardsToCheck.get(0).getIntegerValue() == 13) ||
					//in case of:   lowest card = 2
					(cardsToCheck.get(cardsToCheck.size()-1).getIntegerValue() == 2)){
				//OK
			}else{
				//reject
				throw new InvalidDropException("(YE004)","Cards have at least one ace that cannot be attached to series.");
			}
			
		}
	}

	/**
	 * returns true iff the player is currently in a state when he can pickup a card (from the thrown or deck)
	 * @return True iff the player is currently in a state when he can pickup a card (from the thrown or deck)
	 */
	public boolean canPickup() {
		return (firstFreeLocation != GameData.YANIV_NUM_CARDS);
	}
	
	@Override
	public boolean isHumanPlayer() {
		return true;
	}

	@Override
	protected boolean shouldYaniv() {
		// depends on the player's judgment, therefore always true
		return true;
	}

	@Override
	public void reset(){
		super.reset();
		setCanDrop(true);
		setCanPickup(true);
		setCardVisibility(true);
	}
}