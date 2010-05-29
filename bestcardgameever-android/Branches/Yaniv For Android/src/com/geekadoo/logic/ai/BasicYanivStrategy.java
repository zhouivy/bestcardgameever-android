package com.geekadoo.logic.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.util.Log;

import com.geekadoo.logic.GameData;
import com.geekadoo.logic.Hand;
import com.geekadoo.logic.PickupMethod;
import com.geekadoo.logic.PlayingCard;
import com.geekadoo.logic.SingleDeck;
import com.geekadoo.logic.ThrownCards;


/**
 * very basic strategy
 * drop: will drop highest value card,set,or series each time, never throwing jokers
 * pickup: if card on table < average of available cards values (6.7)
 * yaniv: if possible, perform.
 * @author Elad
 */
public class BasicYanivStrategy implements YanivStrategy ,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Integer PICKUP_THRESHOLD = 7;
	private PickupMethod pickUpFrom = null;
	ThrownCards thrown;
	private boolean haveMoreCards = false;
	private ArrayList<PlayingCard> highestSetOrSeriesWithThrownCard ;
	ArrayList<PlayingCard> cardsToDrop;


	public BasicYanivStrategy(){
		highestSetOrSeriesWithThrownCard = new ArrayList<PlayingCard>();

	}
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

		
		//		// Try to get the next player
		//		ArrayList<Hand> pio= gameData.getPlayersInOrder();
		//		int myLocationInPio = pio.indexOf(hand);
		//		Hand handAfterMe = pio.get(myLocationInPio+1 % pio.size());
		//		// If the last card was picked up from the thrownCards deck, look at it (to decide if you want to throw it again)
		//		if(handAfterMe.isLastPickedUpFromDeck == false){
		//			PlayingCard c = handAfterMe.getLastCardPickedUp();
		//		}




		//make a copy from 'cards'(original cards)
		ArrayList<PlayingCard> copyOfOriginalCards = new ArrayList<PlayingCard>();
		for (int i = 0; i < cards.length; i++) {
			copyOfOriginalCards.add(cards[i]);
		}

		//TODO: remove the next 3 lines
//		System.err.println("---------------vvvvvvvvvv-----------------------");
//		System.out.println("original cards in hand: "+copyOfOriginalCards);
//		System.out.println("card on table : " + thrown.peekTopCard());

		// First drop option - find the cards to drop regardless the thrown card.
		ArrayList<PlayingCard> cardsToDropWithOutThrownCard = findBestDropOption(cards);

		// Second drop option - find the cards to drop with thrown card.
		ArrayList<PlayingCard> cardsToDropWithThrownCard = findBestDropOptionWithThrownCard(cards);

		// Third drop option(switch)
		// find the cards to drop with thrown card after switch the cards that the DDA decided to drop regardless the thrown card, with the thrown card itself.
		ArrayList<PlayingCard> cardsToDropAfterSwitchWithTrownCard = findBestDropOptionAfterSwitch(copyOfOriginalCards, cardsToDropWithOutThrownCard);

		// Calculate all three drop options
		// TODO: Read and verify 2 & 3 make sense
		int firstDropChoice =  countCards(cardsToDropWithOutThrownCard);
		int secondDropChoice = (countCards(highestSetOrSeriesWithThrownCard) + countCards(cardsToDropWithThrownCard));
		int	thirdDropChoice = countCards(cardsToDropAfterSwitchWithTrownCard) + countCards(cardsToDropWithOutThrownCard);

		//TODO: remove the next 3 lines.
//		System.out.println("first choice(DDA) = " + cardsToDropWithOutTrownCard + " = " +firstDropChoice);
//		System.out.println("second choice(best set/series) = drop this round: " +  cardsToDropWithThrownCard+ " + drop next round: " + highestSetOrSeriesWithThrownCard+ " = " + secondDropChoice);
//		System.out.println("third choice(Switch) = drop this round" + cardsToDropWithOutTrownCard  + " + drop next round" +  cardsToDropAfterSwitchWithTrownCard + " = "+thirdDropChoice);

		//choose the best drop option
		if(thirdDropChoice > secondDropChoice && thirdDropChoice > firstDropChoice){
			cardsToDrop = cardsToDropWithOutThrownCard;
			pickUpFrom = PickupMethod.fromThrown;
//			System.out.println("third choice was selected");
		}else if(secondDropChoice > thirdDropChoice && secondDropChoice > firstDropChoice){
			cardsToDrop = cardsToDropWithThrownCard;
			pickUpFrom = PickupMethod.fromThrown;
//			System.out.println("second choice was selected");
		}else if(secondDropChoice == thirdDropChoice && secondDropChoice > firstDropChoice && thirdDropChoice > firstDropChoice ){
			if(countCards(cardsToDropWithThrownCard) > countCards(cardsToDropWithOutThrownCard)){
				cardsToDrop = cardsToDropWithThrownCard;
//				System.out.println("third choice was selected");
			}else{
				cardsToDrop = cardsToDropWithOutThrownCard;
//				System.out.println("third choice was selected");
			}
			pickUpFrom = PickupMethod.fromThrown;
		}else{
			cardsToDrop = cardsToDropWithOutThrownCard;
			pickUpFrom = PickupMethod.decidePickup;
//			System.out.println("first choice was selected");
		}


//		TODO: remove the next 3 lines
//		System.out.println("cardsToDrop : "+cardsToDrop);
//		System.out.println("pickup from : " + pickUpFrom);
//		System.err.println("-----------^^^^^^^^^^^------------------");


		// mark the cards of the best drop option as selected
		for (PlayingCard card : cardsToDrop) {
			card.setSelected(true);


			//for testing only!!
//			TestDecideDropCardsAlgorithm.addCardToDropCardList(card);
		}
	}

	/**
	 * ----Switch option-----
	 * find the best drop option after switch the cards the the DDA decided to drop regardless the thrown card with thrown card itself.
	 * After the switch we execute DDA again on the cards that in the hand after the switch.
	 * @param copyOfOriginalCards
	 * @param cardsToDropWithOutTrownCard
	 * @return
	 */
	private ArrayList<PlayingCard> findBestDropOptionAfterSwitch(ArrayList<PlayingCard> copyOfOriginalCards, ArrayList<PlayingCard> cardsToDropWithOutTrownCard) {
		PlayingCard[] cards;

		//add the thrown card to the original cards
		copyOfOriginalCards.add(thrown.peekTopCard());

		//remove the cards that DDA decided to drop regardless the thrown card
		for (PlayingCard card : cardsToDropWithOutTrownCard) {
			for (int i = copyOfOriginalCards.size()-1; i >=0 ; i--) {
				if(copyOfOriginalCards.get(i)!= null && copyOfOriginalCards.get(i).equals(card)){
					copyOfOriginalCards.remove(i);
				}
			}
		}

		// send the cards after the switch was done, to the DDA to find Best Drop Option
		cards = new PlayingCard[5];
		copyOfOriginalCards.toArray(cards);
		haveMoreCards = true;

		//first we check if the thrown card will help to make set/series after the switch
		//remove null
		cards = removeNulls(cards);

		// find highest set  after switch (7,7,7 etc.)
		boolean isThrownCardHelpToMakeSetAfterSwitch = (countCards(findHighestSet(cards)) > 0) &&  (findHighestSet(cards).contains(thrown.peekTopCard()));

		// divide cards by suit and make array of series by suits.
		ArrayList<ArrayList<PlayingCard>> seriesArr = devideCardsBySuits(cards);

		//find highest series after switch
		boolean isThrownCardHelpToMakeSeriesAfterSwitch = (countCards(findHighestSeries(seriesArr)) > 0) && (findHighestSeries(seriesArr).contains(thrown.peekTopCard()));

		// now we check if the thrown really help to make set or series after the switch.
		ArrayList<PlayingCard> cardsToDropAfterSwitchWithTrownCard;
		if(isThrownCardHelpToMakeSetAfterSwitch || isThrownCardHelpToMakeSeriesAfterSwitch ){
			cardsToDropAfterSwitchWithTrownCard = findBestDropOption(cards);
		}else{
			// the thrown card doesn't help to make set or series.
			// reset cardsToDropAfterSwitchWithTrownCard.
			cardsToDropAfterSwitchWithTrownCard = new ArrayList<PlayingCard>();
		}
		return cardsToDropAfterSwitchWithTrownCard;
	}

	/**
	 * Find the best drop option with thrown card.
	 * @param cards
	 * @return cards to drop
	 */
	private ArrayList<PlayingCard> findBestDropOptionWithThrownCard(PlayingCard[] cards) {
		//check if the thrown card can help to make set or series
		ArrayList<PlayingCard> newCards = checkIfThrownCardCanHelpToMakeSetOrSeries(arrayToArrayList(cards));


		// in case that i need to pickup from deck or the thrown card is joker/ace/2  - use the original cards for DDA, other wise use the newCards.
		if (pickUpFrom.equals(PickupMethod.decidePickup) || isLowestCard(thrown.peekTopCard())){
			//decide drop algorithm will use the original cards.
		}else{
			// decide drop algorithm will use the new cards
			cards = new PlayingCard[5];
			newCards.toArray(cards);
		}

		// find the cards to drop with thrown card
		ArrayList<PlayingCard> cardsToDropWithThrownCard = findBestDropOption(cards);

		// After we found the highest set/series, we need to remove the card/cards that the we want to throw this round.
		for (PlayingCard card : cardsToDropWithThrownCard) {
			for (int i=highestSetOrSeriesWithThrownCard.size()-1; i >=0 ; i--) {
				if(highestSetOrSeriesWithThrownCard.get(i).equals(card)){
					highestSetOrSeriesWithThrownCard.remove(i);
				}
			}
		}

		return cardsToDropWithThrownCard;
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
		ArrayList<ArrayList<PlayingCard>> seriesArr = devideCardsBySuits(cardsWithoutNulls);

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
			Log.e("EXCEP", "cardswithoutnulls is empty, gonna be an exception");
		}
		return cardsWithoutNulls[0];
	}

	private ArrayList<PlayingCard> decideBestDropOption(ArrayList<PlayingCard> highestValSeries, ArrayList<PlayingCard> highestValSet, ArrayList<PlayingCard> highestValCard){
		int highestValSeriesWorth = countSeries(highestValSeries);;
		int highestValSetWorth = countCards(highestValSet);
		int highestValCardWorth = highestValCard.get(0).getCountValue();



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

	private ArrayList<PlayingCard> checkIfThrownCardCanHelpToMakeSetOrSeries(ArrayList<PlayingCard> cardsToCheck){
		GameData gameData = GameData.getInstance();


		//ArrayList<PlayingCard> newCards = cardsToCheck;

		//remove null
		for (int i = cardsToCheck.size(); i > 0 ; i--) {
			if(cardsToCheck.get(i-1) == null){
				cardsToCheck.remove(i-1);
			}
		}

		int numberOfCardsInCopyOfCards = cardsToCheck.size();

		// in case that the thrown card is" joker or ace or 2 - tell the pickUp algorithm to take from table
		ThrownCards thrown = gameData.getThrownCards();
		int thrownCardValue = thrown.peekTopCard().getCountValue();
		//PlayingCard thrownCard = new PlayingCard(thrown.popTopCard().getSuit(), thrown.popTopCard().getValue());

		if (thrownCardValue <= 2){
			pickUpFrom = PickupMethod.fromThrown;
		}else{// in case that the thrown card is higher than 2

			//remove joker or ace or 2 and try to make set/series with the card that left and with thrown card.
			for (int i = cardsToCheck.size(); i > 0; i--) {
				if(cardsToCheck.get(i-1).getCountValue() <= 2){
					cardsToCheck.remove(cardsToCheck.get(i-1));
				}
			}

			//get the new set/series with the thrown card , or null in case that we can't create set/series with thrown card.
			ArrayList<PlayingCard> cardsThatHelpToMakeSeries = new ArrayList<PlayingCard>();
			ArrayList<PlayingCard> cardsThatHelpToMakeSet = new ArrayList<PlayingCard>();

			cardsThatHelpToMakeSeries = checkForSeries(cardsToCheck, thrown.peekTopCard() );
			cardsThatHelpToMakeSet = checkForSet(cardsToCheck, thrown.peekTopCard());

			// select the highest between the series and set
			ArrayList<PlayingCard> highestSetOrSeries = new ArrayList<PlayingCard>();
			if(countSeries(cardsThatHelpToMakeSeries) > countSeries(cardsThatHelpToMakeSet)){
				highestSetOrSeries = cardsThatHelpToMakeSeries;
			}else{
				highestSetOrSeries = cardsThatHelpToMakeSet;
			}

			//save the highestSetOrSeries in case its higher that the current
			if(countCards(highestSetOrSeries) > countCards(highestSetOrSeriesWithThrownCard)){
				highestSetOrSeriesWithThrownCard = highestSetOrSeries;	
			}


			//check if the thrown card can help to make set or series.
			if(highestSetOrSeries.size() > 0){

				//remove joker  , ace , 2 from original hand
				for (int i = cardsToCheck.size(); i > 0; i--) {
					if(cardsToCheck.get(i-1).getCountValue() <=2){
						cardsToCheck.remove(cardsToCheck.get(i-1));
					}
				}

				//remove from the original hand the cards that use the thrown card to make set/series.
				for (PlayingCard card : highestSetOrSeries) {
					cardsToCheck.remove(card);
				}

				//check if there is at least one card that left in the hand
				if(cardsToCheck.size() > 0){
					//need to execute the drop algorithm on the new cards.
					pickUpFrom = PickupMethod.fromThrown;

				}else if(haveMoreCards){
					//no card left on the hand, but i have more cards on the hand that i remove earlier.
					pickUpFrom = PickupMethod.fromThrown;

					for (PlayingCard card : highestSetOrSeries) {
						cardsToCheck.add(card);
					}
					cardsToCheck.remove(thrown.peekTopCard());

					// If the thrown card use all the cards to make series/set and there are at least 4 cards, 
					//  this code will decide which card to drop from the series,
					// The '+1' is for the thrown card
				}else if(numberOfCardsInCopyOfCards + 1 == highestSetOrSeries.size() && numberOfCardsInCopyOfCards >= 3){
					// in case that the thrown card make series with all the cards in the hand, and the hand has more that 3 cards  
					pickUpFrom = PickupMethod.fromThrown;

					//the location of the thrown card is at the top of the series (the highest card), 
					//  drop the last card on the series(the lower card)
					if(highestSetOrSeries.get(0).equals(thrown.peekTopCard())){
						cardsToCheck.add(highestSetOrSeries.get(highestSetOrSeries.size() - 1));
					}else{
						//the location of the thrown card is at the end of the series (the lower card) or at the middle,
						// drop the highest card on the series(the first card)
						cardsToCheck.add(highestSetOrSeries.get(0));
					}

				}else{
					//the thrown card helped to make set/series but i don't have other card to drop instead the thrown card.
					pickUpFrom = PickupMethod.decidePickup;
				}
			}else{
				//the thrown card doesn't help to make set or series
				pickUpFrom = PickupMethod.decidePickup;
			}
		}

		return cardsToCheck;
	}

	private ArrayList<ArrayList<PlayingCard>> devideCardsBySuits(PlayingCard[] cardsWithoutNulls){
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


		
		
//		if(aceInCards !=null){
//			suitedCardList.remove(aceInCards);
//		}
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
		
//		ArrayList<PlayingCard> copyOfSuitedCardList = new ArrayList<PlayingCard>();
//		for (PlayingCard card : suitedCardList) {
//			copyOfSuitedCardList.add(card);
//		}
//		
		
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


	private void addJokers(ArrayList<PlayingCard> tmpHighestValSeries , ArrayList<PlayingCard> jokers, int numOfJokersToAdd){
		//jokers are needed. Add them if they not already there
		for (int i=0; i<numOfJokersToAdd; i++) {
			if(!tmpHighestValSeries.contains(jokers.get(i))){
				tmpHighestValSeries.add(jokers.get(i));
			}
		}
	}
	
	private ArrayList<PlayingCard> verifyThatThereIsSeries(ArrayList<PlayingCard> tmpHighestValSeries, PlayingCard ace) {
		//verify that the return array contains series.
		ArrayList<PlayingCard> numberOfUsedJokers = new ArrayList<PlayingCard>();

		//move all the joker to other arrayList
		for (int i = tmpHighestValSeries.size()-1; i > 0; i--) {
			if(tmpHighestValSeries.get(i).getValue() == PlayingCard.JOKER){
				numberOfUsedJokers.add(tmpHighestValSeries.get(i));
				tmpHighestValSeries.remove(i);
			}
		}

		//count available jokers
		int numOfAvailableJokers = numberOfUsedJokers.size();
		int numOfAces = ace != null? 1 : 0 ;

		//check for "holes" between 2 cards
		for(int j=0; j<tmpHighestValSeries.size()-1; j++){
			//check that we have enough cards + jokers to make series
			if(tmpHighestValSeries.size() + numberOfUsedJokers.size() + numOfAces >= 3){
				int currentCard = tmpHighestValSeries.get(j).getIntegerValue();
				int nextCard = tmpHighestValSeries.get(j+1).getIntegerValue();
				int diff = currentCard - nextCard;
				boolean isSeries = true;

				if(diff ==1){
					// move to the next pair of cards
				}else{
					// have available joker 
					if(numOfAvailableJokers > 0){
						// if the available jokers can help to "fill a hole" between 2 cards
						if(diff - numOfAvailableJokers <= 1){
							//update available jokers according the jokers we use to "fill a hole"
							numOfAvailableJokers = diff-1;
						}else{
							// can't "fill a hole" with the available jokers 
							isSeries = false;
						}
					}else{
						//not enough jokers to "fill a hole" 
						isSeries = false;
					}
				}
				//check that all the current cards are part of the same series.
				if( ! isSeries){
					// Remove the first card, init the numOfAvailableJokers 
					//  set j to be -1 so that in the 'for loop' it will be 0 again.
					// Now try to make series with the cards that left
					tmpHighestValSeries.remove(0);
					j=-1;
					numOfAvailableJokers = numberOfUsedJokers.size();
				}
			}else{
				//not enough cards to make series
				tmpHighestValSeries = new ArrayList<PlayingCard>();
				return tmpHighestValSeries;
			}
		}
		//add back the jokers.
		for (PlayingCard joker : numberOfUsedJokers) {
			tmpHighestValSeries.add(joker);
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
		int roundNumber  = gameData.getTurn().getRounds();


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

	private boolean isLowestCard(PlayingCard thrownCard){
		// check if the card in the table is joker or ace or 2
		if(thrownCard.getCountValue() == 0 || thrownCard.getCountValue() == 1 || thrownCard.getCountValue() == 2){
			return true;
		}
		return false;
	}

	private ArrayList<PlayingCard> checkForSet(ArrayList<PlayingCard> cards,PlayingCard thrownCard){
		ArrayList<PlayingCard> cardsThatHelpToMakeSet = new ArrayList<PlayingCard>();

		for (PlayingCard card : cards) {
			if(card.getIntegerValue() == thrownCard.getIntegerValue()){
				cardsThatHelpToMakeSet.add(card);
			}
		}
		// if there is any card that help to make set with the thrown card, add the thrown card to the set
		if(cardsThatHelpToMakeSet.size() > 0 ){
			cardsThatHelpToMakeSet.add(thrownCard);
		}

		return cardsThatHelpToMakeSet;
	}

	private ArrayList<PlayingCard> checkForSeries(ArrayList<PlayingCard> cards ,PlayingCard thrownCard){
		cards.add(thrownCard);

		ArrayList<PlayingCard> cardsThatHelpToMakeSeries = new ArrayList<PlayingCard>();
		PlayingCard[] cardsWithThrownCard = new PlayingCard[cards.size()];
		cards.toArray(cardsWithThrownCard);

		ArrayList<ArrayList<PlayingCard>> seriesArr = devideCardsBySuits(cardsWithThrownCard);

		for (int i = 0; i < seriesArr.size(); i++) {
			ArrayList<PlayingCard> currentSeries = seriesArr.get(i);
			int sumOfCurrentSeries = countSeries(currentSeries);
			if( sumOfCurrentSeries != -1){
				cardsThatHelpToMakeSeries=currentSeries;;
			}
		}

		cards.remove(thrownCard);

		// in case that the thrown card help to make series, and the hand doesn't already contain series (without help from the thrown card).
		if(cardsThatHelpToMakeSeries.size() > 0 && cardsThatHelpToMakeSeries.contains(thrownCard)){
			// the thrown card helped to make series this that cards that in the hand.
		}else{
			// need to reset cardsThatHelpToMakeSeries because the thrown card doesn't help to make series.
			cardsThatHelpToMakeSeries = new ArrayList<PlayingCard>();
		}

		return cardsThatHelpToMakeSeries;
	}

	/* (non-Javadoc)
	 * @see com.sheepzkeen.yaniv.YanivStrategy#decidePickUp()
	 */
	@Override
	public boolean decideYaniv() {
		//basic strategy, always do yaniv. (in more elaborate strategies, will count turns or other people's card count and decide according to that)
		return true;
	}

}
