package com.geekadoo.logic.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import android.util.Log;

import com.geekadoo.logic.GameData;
import com.geekadoo.logic.Hand;
import com.geekadoo.logic.PickupMethod;
import com.geekadoo.logic.PlayingCard;
import com.geekadoo.logic.SingleDeck;
import com.geekadoo.logic.ThrownCards;


/**
 * very stupid strategy - will make mistakes 75% of the time
 * drop: 50% of the time, will drop highest value card,set,or series each time, never throwing jokers
 * pickup: from deck
 * yaniv: perform 75% of times.
 * @author Elad
 */
public class VeryStupidYanivStrategy implements YanivStrategy ,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String LOG_TAG = "VeryStupidYanivStrategy";
	
	private PickupMethod pickUpFrom = null;
	ThrownCards thrown;
	ArrayList<PlayingCard> cardsToDrop;


	/* (non-Javadoc)
	 * @see com.sheepzkeen.yaniv.YanivStrategy#decideDrop()
	 */
	@Override
	public void decideDrop() {
		GameData gameData = GameData.getInstance();

		// Get the current hand playing from the turn
		Hand hand = gameData.getTurn().peek();

		//get the thrown cards.
		thrown = gameData.getThrownCards();

		//get the cards from hand.
		PlayingCard[] cards = hand.getCards();

		
		//make a copy from 'cards'(original cards)
		ArrayList<PlayingCard> copyOfOriginalCards = new ArrayList<PlayingCard>();
		for (int i = 0; i < cards.length; i++) {
			copyOfOriginalCards.add(cards[i]);
		}

		// First drop option - find the cards to drop regardless the thrown card.
		ArrayList<PlayingCard> cardsToDropWithOutConsideringThrownCard = findBestDropOption(cards);

			cardsToDrop = cardsToDropWithOutConsideringThrownCard;
			pickUpFrom = PickupMethod.fromDeck;


		// mark the cards of the best drop option as selected
		for (PlayingCard card : cardsToDrop) {
			card.setSelected(true);
		}
	}



	/**
	 * Convert array of playing cards to arrayList of playing cards
	 * @param arrayToConvert
	 * @return arrayList of playing cards
	 */
	private ArrayList<PlayingCard> arrayToArrayList(PlayingCard[] arrayToConvert){
		ArrayList<PlayingCard> arrayListOfCards = new ArrayList<PlayingCard>();
		for (PlayingCard card : arrayToConvert) {
			arrayListOfCards.add(card);
		}
		return arrayListOfCards;
	}


	private ArrayList<PlayingCard> findBestDropOption(PlayingCard[] cards) {
		ArrayList<PlayingCard> cardsToDrop;
		ArrayList<PlayingCard> highestValCard = new ArrayList<PlayingCard>();
		ArrayList<PlayingCard> highestValSet = new ArrayList<PlayingCard>();
		ArrayList<PlayingCard> highestValSeries = new ArrayList<PlayingCard>();

		//remove nulls 
		PlayingCard[] cardsWithoutNulls = removeNulls(cards);

		// find highest single card
		highestValCard.add(findHighestSingleCard(cardsWithoutNulls));

		// find highest set (7,7,7 etc.)
		highestValSet = findHighestSet(cardsWithoutNulls);

		// divide cards by suit and make array of series by suits.
		ArrayList<ArrayList<PlayingCard>> seriesArr = divideCardsBySuits(cardsWithoutNulls);

		//find highest series
		highestValSeries = findHighestSeries(seriesArr);

		//decide best option
		cardsToDrop = decideBestDropOption(highestValSeries, highestValSet, highestValCard);
		return cardsToDrop;
	}

	private PlayingCard[] removeNulls(PlayingCard[] cards) {
		ArrayList<PlayingCard> tmpCards = new ArrayList<PlayingCard>();
		for (PlayingCard card : cards) {
			if (card != null) {
				tmpCards.add(card);
			}
		}

		PlayingCard[] cardsWithoutNulls = new PlayingCard[tmpCards.size()];
		tmpCards.toArray(cardsWithoutNulls);
		return cardsWithoutNulls;
	}

	private PlayingCard findHighestSingleCard(PlayingCard[] cardsWithoutNulls){
		Arrays.sort(cardsWithoutNulls);
		if(cardsWithoutNulls.length == 0){
			Log.e(LOG_TAG, "cardswithoutnulls is empty, gonna be an exception");
		}
		return cardsWithoutNulls[0];
	}

	private ArrayList<PlayingCard> decideBestDropOption(ArrayList<PlayingCard> highestValSeries, ArrayList<PlayingCard> highestValSet, ArrayList<PlayingCard> highestValCard){
		int highestValSeriesWorth = countSeries(highestValSeries);
		int highestValSetWorth = countCards(highestValSet);
		int highestValCardWorth = highestValCard.get(0).getCountValue();


		// Adding stupidity:
		if(new Random().nextInt(100)>75){
			if(highestValSeriesWorth>= highestValSetWorth
					&& highestValSeriesWorth >= highestValCardWorth){
				//series
				return highestValSeries;
			}else if(highestValSetWorth>=highestValCardWorth){
				//set
				return highestValSet;
			}else{
				//high card
				return highestValCard;
			}
		}else{
			// 75% of the time, it will make a stupid decision
			return highestValCard;
		}
	}

	private ArrayList<PlayingCard> findHighestSeries(ArrayList<ArrayList<PlayingCard>> seriesArr){
		int highestValSeriesWorth=-1;
		ArrayList<PlayingCard> highestValSeries = new ArrayList<PlayingCard>();

		for (int i = 0; i < seriesArr.size(); i++) {
			ArrayList<PlayingCard> currentSeries = seriesArr.get(i);
			int tmpHighestValSeriesWorth = countSeries(currentSeries);
			if (tmpHighestValSeriesWorth>highestValSeriesWorth) {
				highestValSeriesWorth=tmpHighestValSeriesWorth;
				highestValSeries = currentSeries;
			}
		}
		return highestValSeries;
	}

	private ArrayList<ArrayList<PlayingCard>> divideCardsBySuits(PlayingCard[] cardsWithoutNulls){
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

		return seriesArr;
	}

	private ArrayList<PlayingCard> findHighestSet(PlayingCard[] cardsWithoutNulls){
		int highestValSetWorth=-1;
		ArrayList<PlayingCard> highestValSet = new ArrayList<PlayingCard>(); 

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

		return highestValSet;
	}

	private int countSeries(ArrayList<PlayingCard> currentSeries) {
		int retVal = -1;
		if(currentSeries.size()>=3){
			retVal=0;
			for (PlayingCard card : currentSeries) {
				retVal+=card.getCountValue();
			}
		}
		return retVal;
	}

	private int countCards(ArrayList<PlayingCard> cards){
		int sum = 0;
		if(cards.size() > 0){
			for (PlayingCard card : cards) {
				sum += card.getCountValue();
			}
		}
		return sum;
	}

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
		
		Collections.sort(suitedCardList);
		PlayingCard aceInCards = null;


		
		
		//Mark ace for future use
		for (PlayingCard card : suitedCardList) {
			if(card.getIntegerValue() == Character.digit(PlayingCard.ACE, 10)){
				aceInCards = card;
			}
		}
		
		ArrayList<PlayingCard> tmpHighestValSeries = new ArrayList<PlayingCard>();
		ArrayList<PlayingCard> tmpHighestValSeries14 = new ArrayList<PlayingCard>();
		ArrayList<PlayingCard> retVal = new ArrayList<PlayingCard>();		
		//first check if the series is possible - cards + jokers >=3
		if (suitedCardList.size()+jokers.size()>=3){
			tmpHighestValSeries = findSeriesForSuitedList(jokers,suitedCardList, true,aceInCards);
			if(aceInCards!= null){
				tmpHighestValSeries14 = findSeriesForSuitedList(jokers, suitedCardList, false,aceInCards);
				retVal = countSeries(tmpHighestValSeries)>countSeries(tmpHighestValSeries14)? tmpHighestValSeries:tmpHighestValSeries14;
			}else{
				retVal = tmpHighestValSeries;
			}
		}
		return retVal;
	}
	private ArrayList<PlayingCard> findSeriesForSuitedList(ArrayList<PlayingCard> jokers, ArrayList<PlayingCard> suitedCardList, boolean isAceOne,PlayingCard aceInCards) {
		//the return value
		ArrayList<PlayingCard> tmpHighestValSeries = new ArrayList<PlayingCard>();
		int jokerIndex=0;
		int numAvailableJokers = jokers.size();
		
		PlayingCard aceAsFourTeen = new PlayingCard(suitedCardList.get(0).getSuit() , PlayingCard.ACE_AS_FOURTEEN);

		if(!isAceOne){
			
			
			//replace the ace whose integer value is 1 with an ace who is 14
			suitedCardList.remove(aceInCards);
			suitedCardList.add(aceAsFourTeen);
			//then re-sort the cards so the ace will be first
			Collections.sort(suitedCardList);
		}
		
		
		
		
		

		//go over the cards
		for (int i = 1; i < suitedCardList.size(); i++) {

			PlayingCard currentCard = suitedCardList.get(i);
			PlayingCard previousSuitedCard = suitedCardList.get(i-1);

			if(suitedCardList.size() > 1){
				//calc diff between the remaining cards

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
				}else{
					//The difference was too big - if the cards so far are => 3 then we stop here, else we clean the list of cards and continue
					if (tmpHighestValSeries.size()>=3){
						break;
					}else{
						tmpHighestValSeries = new ArrayList<PlayingCard>(); 
					}
				}
			}else{
				tmpHighestValSeries.add(suitedCardList.get(0));
			}
		}

		if(tmpHighestValSeries.size()==2){
			//number of cards is 2 - not enough to make series - reset it
			tmpHighestValSeries = new ArrayList<PlayingCard>();
		}
		if(!isAceOne){
			if ( tmpHighestValSeries.contains(aceAsFourTeen))
			{
				tmpHighestValSeries.remove(aceAsFourTeen);
				tmpHighestValSeries.add(aceInCards);
			}
			if ( suitedCardList.contains(aceAsFourTeen))
			{
				suitedCardList.remove(aceAsFourTeen);
				suitedCardList.add(aceInCards);
			}
		
		}
		return tmpHighestValSeries;
	}


	/* (non-Javadoc)
	 * @see com.sheepzkeen.yaniv.YanivStrategy#decidePickUp()
	 */
	@Override
	public PlayingCard decidePickUp() {
		// Algorithm that decide where to pickup from, according this rules:
		// In case that the thrown card is joker/ace/2 , need to pickup the joker/ace/2 regardless the value of the pickUpFrom variable
		// In case that the the value of the pickUpFrom variable is 'fromThrown' (the DDA decided to pickup from table), need to pickup from table
		// In case that the the value of the pickUpFrom variable is 'fromDeck', the pickUp algorithm need to decide if is better to pickup from
		//  table or deck.(need to consider: value of all the cards that in the hand, round number, value of thrown card etc.)
		GameData gameData = GameData.getInstance();

		SingleDeck deck = gameData.getDeck();

		// Get the current hand playing from the turn
		Hand hand = gameData.getTurn().peek();

		//get the cards from hand.
		PlayingCard[] cards = hand.getCards();

		//get round number
		int roundNumber  = gameData.getTurn().getCurrentRoundNumber();


		//clac the hand after we throw the cards that the DDA decided to drop.
		int handCount = countCards(arrayToArrayList(removeNulls(cards)));
		int thrownCardValue = thrown.peekTopCard().getCountValue();

		//the thrown card is joker or ace or 2
		if(thrown.peekTopCard().getCountValue() <= 2){
			return thrown.popTopCard();
		}else{
			//DDA decided that the thrown card is needed.
			if(pickUpFrom.equals(PickupMethod.fromThrown)){
				return thrown.popTopCard();
			}else{
				//DDA decided doesn't need the thrown card
				if((roundNumber <= 3) && (handCount + thrownCardValue <= 7) || 
						(roundNumber <= 5 &&  (handCount + thrownCardValue <= 5) ) ||
						(roundNumber > 5 && (handCount + thrownCardValue <= 3)) ){
					return thrown.popTopCard();
				}
			}
		}
		return deck.popTopCard();
	}

	/* (non-Javadoc)
	 * @see com.sheepzkeen.yaniv.YanivStrategy#decidePickUp()
	 */
	@Override
	public boolean decideYaniv() {
		// stupid strategy, do yaniv in 75% of the times
		return new Random().nextInt(100)<75;
	}

}
