package com.sheepzkeen.yaniv;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * very basic strategy
 * drop: will drop highest value card,set,or series each time, never throwing jokers
 * pickup: if card on table < average of available cards values (6.7)
 * yaniv: if possible, perform.
 * @author Elad
 *
 */
public class BasicYanivStrategy implements YanivStrategy {

	private static final Integer PICKUP_THRESHOLD = 7;

	/* (non-Javadoc)
	 * @see com.sheepzkeen.yaniv.YanivStrategy#decideDrop()
	 */
	@Override
	public void decideDrop(PlayingCard[] cards) {
		ArrayList<PlayingCard> cardsToDrop;
		ArrayList<PlayingCard> highestValCard = new ArrayList<PlayingCard>();
		int highestValCardWorth=-1;
		ArrayList<PlayingCard> highestValSet = new ArrayList<PlayingCard>();
		int highestValSetWorth=-1;
		ArrayList<PlayingCard> highestValSeries = new ArrayList<PlayingCard>();
		int highestValSeriesWorth=-1;
		//remove nulls
		ArrayList<PlayingCard> tmpCards = new ArrayList<PlayingCard>();
		for (PlayingCard card : cards) {
			if (card != null) {
				tmpCards.add(card);
			}
		}
		PlayingCard[] cardsWithoutNulls = new PlayingCard[tmpCards.size()];
		tmpCards.toArray(cardsWithoutNulls);
		
		// find highest single card
		Arrays.sort(cardsWithoutNulls);
		highestValCard.add(cardsWithoutNulls[0]);
		highestValCardWorth = highestValCard.get(0).getCountValue();
		
		
		// find highest set (7,7,7 etc.)
		// first go over all cards and add 1 in the appropriate bucket for each card found
		int[] buckets = new int[14];
		for (int i = 0; i < cardsWithoutNulls.length; i++) {
			PlayingCard card = cardsWithoutNulls[i];
			int location = card.getIntegerValue()==null ? 0:card.getIntegerValue();
			buckets[location]++;
		}
		// now find the highest value set by multiplying the value with the number of occurrences
		int highestValSetCardVal=-1;
		for (int i = 1; i < buckets.length; i++) {
			int numCardsInBucket = buckets[i];
			int tmpValSetWorth = numCardsInBucket*i;
			if(numCardsInBucket > 1 &&  tmpValSetWorth > highestValSetWorth){
				highestValSetWorth = tmpValSetWorth;
				highestValSetCardVal = i;
			}
		}
		// now add all the cards that have a value which is the same as the highestValSetCardVal
		// (assuming a set was found)
		if(highestValSetCardVal != -1){
			for (PlayingCard card : cardsWithoutNulls) {
				if(card.getIntegerValue() != null && card.getIntegerValue()== highestValSetCardVal){
					highestValSet.add(card);
				}
			}
		}
		
		
		// divide cards by suit and find highest value series
		ArrayList<PlayingCard> clubs = new ArrayList<PlayingCard>();
		ArrayList<PlayingCard> spades = new ArrayList<PlayingCard>();		
		ArrayList<PlayingCard> hearts = new ArrayList<PlayingCard>();		
		ArrayList<PlayingCard> diamonds = new ArrayList<PlayingCard>();
		ArrayList<PlayingCard> jokers = new ArrayList<PlayingCard>();

		for (PlayingCard card : cardsWithoutNulls) {
			switch (card.getSuit()) {
			case PlayingCard.CLUBS:
				clubs.add(card);
				break;
			case PlayingCard.SPADES:
				spades.add(card);
				break;
			case PlayingCard.HEARTS:
				hearts.add(card);
				break;
			case PlayingCard.DIAMOND:
				diamonds.add(card);
				break;
			case PlayingCard.RED_SUIT:
			case PlayingCard.BLACK_SUIT:
				jokers.add(card);
				break;
			default:
				break;
			}
			
		}
		//for each suit - get the set that is possible to make from it
		ArrayList<PlayingCard> spadesSeries = checkSeriesInSuit(jokers, spades);
		ArrayList<PlayingCard> heartsSeries = checkSeriesInSuit(jokers, hearts);
		ArrayList<PlayingCard> diamondsSeries = checkSeriesInSuit(jokers, diamonds);
		ArrayList<PlayingCard> clubsSeries = checkSeriesInSuit(jokers, clubs);
		ArrayList<ArrayList<PlayingCard>> seriesArr = new ArrayList<ArrayList<PlayingCard>>();
		seriesArr.add(spadesSeries);
		seriesArr.add(heartsSeries);
		seriesArr.add(diamondsSeries);
		seriesArr.add(clubsSeries);
		highestValSeriesWorth=-1; 
		for (int i = 0; i < seriesArr.size(); i++) {
			ArrayList<PlayingCard> currentSeries = seriesArr.get(i);
			int tmpHighestValSeriesWorth = countSeries(currentSeries);
			if (tmpHighestValSeriesWorth>highestValSeriesWorth) {
				highestValSeriesWorth=tmpHighestValSeriesWorth;
				highestValSeries = currentSeries;
			}
		}
		
		//decide best option
		if(highestValSeriesWorth> highestValSetWorth
				&& highestValSeriesWorth > highestValCardWorth){
			//series
			cardsToDrop = highestValSeries;
		}else if(highestValSetWorth>highestValCardWorth){
			//set
			cardsToDrop = highestValSet;
		}else{
			//high card
			cardsToDrop = highestValCard;
		}
		//and mark as selected
		for (PlayingCard card : cardsToDrop) {
			card.setSelected(true);
		}
	}

	private int countSeries(ArrayList<PlayingCard> currentSeries) {
		int retVal = -1;
		if(currentSeries.size()>=3){
			retVal=0;
			for (PlayingCard card : currentSeries) {
				retVal+=card.getIntegerValue();
			}
		}
		return retVal;
	}

	
	//TODO: this method does not drop QKA types of series because does not recognize 'A' as 14 - need to fix!
	/**
	 * returns an {@link ArrayList} of cards which represents a series of cards that can be thrown
	 * if there is none, returns an empty {@link ArrayList} (not null) object
	 * @param cards the playing cards in the hand
	 * @param jokers the jokers in the hand
	 * @param suitedCardList cards of the same suit
	 * @return
	 */
	private ArrayList<PlayingCard> checkSeriesInSuit(ArrayList<PlayingCard> jokers,
			ArrayList<PlayingCard> suitedCardList) {
		//the return value
		ArrayList<PlayingCard> tmpHighestValSeries = new ArrayList<PlayingCard>();
		int jokerIndex=0;
		int numAvailableJokers = jokers.size();
		//first check if the series is possible - cards + jokers >=3
		if (suitedCardList.size()+jokers.size()>=3){
			//go over the cards
			for (int i = 1; i < suitedCardList.size(); i++) {
				PlayingCard currentCard = suitedCardList.get(i);
				PlayingCard previousSuitedCard = suitedCardList.get(i-1);

				int diff = previousSuitedCard.getIntegerValue() - currentCard.getIntegerValue();
				//check if this card and the one before it are separated by the number of jokers (can be a series)
				if(diff  <= 1 + numAvailableJokers){
					//add the previous card (if not added before) to the list
					if(!tmpHighestValSeries.contains(previousSuitedCard)){
						tmpHighestValSeries.add(previousSuitedCard);
					}
					//if the difference is more than one, add the jokers to the list and remove appropriate amount of jokers from numAvailableJokers
					if(diff > 1){
						for (int j = 0; j < diff-1; j++) {
							numAvailableJokers--;
							tmpHighestValSeries.add(jokers.get(jokerIndex++));
						}
						
					}
					//then add the current card
					tmpHighestValSeries.add(currentCard);
					
				}
			}
			//add jokers to ends - because this is a basic strategy - this is allowed, otherwise - it would be stupid to drop a joker for the next player to pick up
			if(tmpHighestValSeries.size()==2){
					if(numAvailableJokers>0){
						tmpHighestValSeries.add(jokers.get(jokerIndex));//TODO KEEP DEBUG ON! possible bug here!
					}else{
						//series is size 2 and not enough jokers to make it 3 - reset it
						tmpHighestValSeries = new ArrayList<PlayingCard>();
					}
			}
		}
		return tmpHighestValSeries;
	}

	/* (non-Javadoc)
	 * @see com.sheepzkeen.yaniv.YanivStrategy#decidePickUp()
	 */
	@Override
	public PlayingCard decidePickUp(PlayingCardsCollection thrown, PlayingCardsCollection deck) {
		if (thrown.peekTopCard().getIntegerValue()<PICKUP_THRESHOLD){
			return thrown.popTopCard();
		}else{
			return deck.popTopCard();
		}
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.sheepzkeen.yaniv.YanivStrategy#decidePickUp()
	 */
	@Override
	public boolean decideYaniv(PlayingCard[] cards) {
		//basic strategy, always do yaniv. (in more elaborate strategies, will count turns or other people's card count and decide according to that)
		return true;
	}

}
