package com.sheepzkeen.yaniv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	protected void selectCardsToDrop() throws InvalidYanivException {
		//Do nothing here, selection was performed by the drop action
		verifyCardsToDrop();
		System.out.println("success on " + this.toString());
	}







	private void verifyCardsToDrop() throws InvalidYanivException {
		
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

		//0 - initialize
		int jokerCount = 0;
		ArrayList<PlayingCard> jokers = new ArrayList<PlayingCard>();
		boolean suitsAreDifferent = true;
		Set<Character> suitsInCards = new HashSet<Character>();
		PlayingCard aceInCards = null;
		List<PlayingCard> cardsToCheck = new ArrayList<PlayingCard>();
		for (PlayingCard card : cards) {
			if(card != null && card.isSelected()){
				cardsToCheck.add(card);
			}
		}
		//1 - jokers - existance
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
		for (PlayingCard card : cardsToCheck) {
			if(suitsInCards.contains(card.getSuit())){
				//this suit has already appeared once
				suitsAreDifferent = false;
			}else{
				suitsInCards.add(card.getSuit());//TODO: Bug here - change to count after inserts - if less in set than in list - there are duplicates -> different suits
			}
		}
		
		//3.1 check same value
		PlayingCard lastCardChecked = null;
		if(suitsAreDifferent){
			for (PlayingCard card : cardsToCheck) {
				if(lastCardChecked != null && lastCardChecked.getIntegerValue() != card.getIntegerValue()){
					//Reject
					throw new InvalidYanivException("Cards of different suits must have same value!");
				}
				lastCardChecked = card;
			}
		}else{
			//4 check series
			//4.1 not enough cards
			if(cardsToCheck.size() < 3 && jokerCount == 0){ //todo:change to cardstochek.size()+jokercount<3
				//Reject
				throw new InvalidYanivException("Cards have same suit, but are not enough to complete a series! " +
						"(only " + cardsToCheck.size() + " cards dropped and no jokers)");
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
						throw new InvalidYanivException("Cards have same suit, " +
								"but the difference between them is too big"+ (jokerCount>0? ", even with your jokers. ":".") +
								"(cards: "+ cardsToCheck.get(i) + " and " + cardsToCheck.get(i+1)+")");
					}else{
						jokerCount = jokerCount - differenceBetweenThisAndNextCardVal - 1;
					}
				}
					
			}
		}		
		//5
		if(aceInCards != null){
			//highest\lowest 2\13
			if((cardsToCheck.get(0).getIntegerValue() + jokerCount == 13) ||
					(cardsToCheck.get(cardsToCheck.size()-1).getIntegerValue() - jokerCount == 2)){
				//OK
			}else{
				//reject
				throw new InvalidYanivException("Cards have at least one ace that cannot be attached to series.");
			}
		}
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
