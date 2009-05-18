package com.sheepzkeen.yaniv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestDecideDropCardsAlgorithm {

	private YanivStrategy strategy;
	private static ArrayList<PlayingCard> resultCardsList;

	private char card1Number;
	private char card2Number;
	private char card3Number;
	private char card4Number;
	private char card5Number;

	private char card1Suit;
	private char card2Suit;
	private char card3Suit;
	private char card4Suit;
	private char card5Suit;


	private char card1NumberToDrop;
	private char card2NumberToDrop;
	private char card3NumberToDrop;
	private char card4NumberToDrop;
	private char card5NumberToDrop;

	private char card1SuitToDrop;
	private char card2SuitToDrop;
	private char card3SuitToDrop;
	private char card4SuitToDrop;
	private char card5SuitToDrop;

	private char thrownCardNumber;
	private char thrownCardSuit;

	private GameData gameData;
	private ThrownCards thrown;
	private Hand hand;


	public TestDecideDropCardsAlgorithm(YanivStrategy strategy){
		this.strategy = strategy;

		// Get the current hand playing from the turn
		gameData = GameData.getInstance();
		hand = gameData.getTurn().peek();

		//get the thrown cards.
		thrown = gameData.getThrownCards();
	}

	//TODO: need to add to each line of test the string for thrown card
	public void testCardsCombination(){
		System.out.println("----------- start testing 'decide drop cards algorithm' -----------");
		performTest("01",  "qc", "kc", "1c", "7d", "8h"  ,  "qc", "kc", "1c", "00", "00"  ,  "5c");
		performTest("02",  "2c", "3c", "1c", "2d", "1d"  ,  "2c", "3c", "1c", "00", "00"  ,  "qc");
		performTest("03",  "ob", "3c", "1c", "2d", "1d"  ,  "ob", "3c", "1c", "00", "00"  ,  "qc");
		performTest("04",  "ob", "4c", "1c", "or", "8h"  ,  "8h", "00", "00", "00", "00"  ,  "qc");
		performTest("05",  "9c", "or", "ob", "6c", "8h"  ,  "9c", "or", "ob", "6c", "00"  ,  "qc");
		performTest("06",  "jc", "or", "ob", "1c", "8h"  ,  "jc", "or", "ob", "1c", "00"  ,  "5c");
		performTest("07",  "tc", "jc", "or", "ob", "1c"  ,  "tc", "jc", "or", "ob", "1c"  ,  "5c");
		performTest("08",  "1c", "2h", "th", "9s", "2s"  ,  "th", "00", "00", "00", "00"  ,  "5c");
		performTest("09",  "8d", "4c", "4s", "2h", "3d"  ,  "4c", "4s", "00", "00", "00"  ,  "qc");
		performTest("10",  "jd", "6c", "6s", "th", "td"  ,  "th", "td", "00", "00", "00"  ,  "8c");
		performTest("11",  "4d", "4c", "4s", "2d", "3d"  ,  "4c", "4s", "4d", "00", "00"  ,  "8c");
		performTest("12",  "1d", "6c", "4s", "2d", "3d"  ,  "1d", "2d", "3d", "00", "00"  ,  "qc");
		performTest("13",  "kd", "qd", "jd", "td", "ob"  ,  "kd", "qd", "jd", "td", "00"  ,  "5c");
		performTest("14",  "6c", "5s", "3d", "2d", "1d"  ,  "3d", "2d", "1d", "00", "00"  ,  "qc");
		performTest("15",  "6c", "6s", "3d", "4d", "5d"  ,  "3d", "4d", "5d", "00", "00"  ,  "qc");
		performTest("16",  "6c", "7c", "8c", "4d", "5d"  ,  "6c", "7c", "8c", "00", "00"  ,  "qc");
		performTest("17",  "6c", "7c", "8c", "1c", "00"  ,  "6c", "7c", "8c", "00", "00"  ,  "qc");
		performTest("18",  "qc", "or", "ob", "1c", "00"  ,  "qc", "or", "1c", "00", "00"  ,  "5c");
		performTest("19",  "4c", "3c", "ob", "1c", "00"  ,  "4c", "3c", "ob", "1c", "00"  ,  "qc");
		performTest("20",  "4c", "or", "ob", "1c", "qc"  ,  "qc", "or", "1c", "00", "00"  ,  "8c");
		performTest("21",  "4c", "5c", "6d", "6s", "00"  ,  "6d", "6s", "00", "00", "00"  ,  "6c");
		performTest("22",  "4c", "or", "6c", "8s", "00"  ,  "4c", "or", "6c", "00", "00"  ,  "kc");
		performTest("23",  "ts", "7c", "1h", "kc", "9h"  ,  "ts", "00", "00", "00", "00"  ,  "jh");
		performTest("24",  "4h", "or", "5h", "8s", "00"  ,  "8s", "00", "00", "00", "00"  ,  "kh");
		performTest("25",  "4h", "5h", "7h", "00", "00"  ,  "7h", "00", "00", "00", "00"  ,  "6h");
		performTest("26",  "6c", "8s", "2d", "5d", "jd"  ,  "jd", "00", "00", "00", "00"  ,  "8c");
		performTest("27",  "2d", "7d", "6h", "qc", "2h"  ,  "qc", "00", "00", "00", "00"  ,  "8d");
		performTest("28",  "4h", "9h", "th", "3c", "6d"  ,  "6d", "00", "00", "00", "00"  ,  "8h");
		performTest("29",  "ob", "or", "ts", "qs", "1c"  ,  "ts", "ob", "qs", "00", "00"  ,  "8c");
		performTest("30",  "5h", "7d", "9h", "3c", "6d"  ,  "9h", "00", "00", "00", "00"  ,  "kh");
		performTest("31",  "4c", "kc", "3d", "3c", "qc"  ,  "kc", "00", "00", "00", "00"  ,  "qd");
		performTest("32",  "4c", "kc", "6c", "3c", "or"  ,  "3c", "4c", "or", "6c", "00"  ,  "qd");
		performTest("33",  "4c", "kc", "6c", "qc", "or"  ,  "4c", "or", "6c", "00", "00"  ,  "8d");
		performTest("34",  "tc", "jc", "or", "ob", "1c"  ,  "tc", "jc", "or", "ob", "1c"  ,  "5c");
		performTest("35",  "qc", "1c", "or", "ob", "1d"  ,  "qc", "or", "1c", "00", "00"  ,  "5c");
		performTest("36",  "4c", "3c", "or", "1c", "00"  ,  "4c", "3c", "or", "1c", "00"  ,  "qc");
		performTest("37",  "4c", "ob", "or", "1c", "00"  ,  "4c", "ob", "or", "1c", "00"  ,  "8d");
		performTest("38",  "kc", "qc", "4c", "3c", "1c"  ,  "kc", "qc", "1c", "00", "00"  ,  "9c");
		System.out.println("----------- end testing 'decide drop cards algorithm' -----------");
	}

	private void performTest(
			String testNumber,
			String card1,
			String card2,
			String card3,
			String card4,
			String card5,
			String card1ToDrop,
			String card2ToDrop,
			String card3ToDrop,
			String card4ToDrop,
			String card5ToDrop,
			String thrownCard){

		card1Number=card1.charAt(0);
		card1Suit = card1.charAt(1);
		card2Number=card2.charAt(0);
		card2Suit = card2.charAt(1);
		card3Number=card3.charAt(0);
		card3Suit = card3.charAt(1);
		card4Number=card4.charAt(0);
		card4Suit = card4.charAt(1);
		card5Number=card5.charAt(0);
		card5Suit = card5.charAt(1);

		card1NumberToDrop=card1ToDrop.charAt(0);
		card1SuitToDrop = card1ToDrop.charAt(1);
		card2NumberToDrop=card2ToDrop.charAt(0);
		card2SuitToDrop = card2ToDrop.charAt(1);
		card3NumberToDrop=card3ToDrop.charAt(0);
		card3SuitToDrop = card3ToDrop.charAt(1);
		card4NumberToDrop=card4ToDrop.charAt(0);
		card4SuitToDrop = card4ToDrop.charAt(1);
		card5NumberToDrop=card5ToDrop.charAt(0);
		card5SuitToDrop = card5ToDrop.charAt(1);

		thrownCardNumber = thrownCard.charAt(0);
		thrownCardSuit = thrownCard.charAt(1);

		PlayingCard[] cardsList = new PlayingCard[5];
		cardsList = createHandForTesting(
				card1Number, card1Suit,
				card2Number, card2Suit,
				card3Number, card3Suit,
				card4Number, card4Suit,
				card5Number, card5Suit);

		//set the cards in the hand.
		hand.setCards(cardsList);
		
		PlayingCard[] cardsListToDrop = new PlayingCard[5];
		cardsListToDrop = createHandForTesting(
				card1NumberToDrop, card1SuitToDrop,
				card2NumberToDrop, card2SuitToDrop,
				card3NumberToDrop, card3SuitToDrop,
				card4NumberToDrop, card4SuitToDrop,
				card5NumberToDrop, card5SuitToDrop);


		thrown.peekTopCard().setValue(thrownCardNumber);
		thrown.peekTopCard().setSuit(thrownCardSuit);


		resultCardsList = new ArrayList<PlayingCard>(5);

		//TODO: need to fix the next line. decide drop no longer get cardsList. tests will not run until the next line will be fix.
		strategy.decideDrop();

		//just for printing
		List<PlayingCard> origList = Arrays.asList(cardsList);  

		//convert array[] to arrayList for comparing
		List<PlayingCard> list = Arrays.asList(cardsListToDrop);  
		ArrayList<PlayingCard> expectedDropCardsList = new ArrayList<PlayingCard>(list);  

		//remove nulls
		for(int i=expectedDropCardsList.size();i>0;i--){
			if(expectedDropCardsList.get(i-1) == null){
				expectedDropCardsList.remove(i-1);
			}
		}

		boolean isAlgorithmOk=true;
		//check size
		if(expectedDropCardsList.size() == resultCardsList.size()){
			//check that all the cards in resultCardsList that return from DDA, exist in 'expected result array'(arrayOfCards)
			for (PlayingCard card : resultCardsList) {
				boolean isCardExist=false;
				for(PlayingCard cardToDrop: expectedDropCardsList){
					if(cardToDrop.equals(card)){
						isCardExist=true;
						break;
					}
				}
				if(! isCardExist){
					System.err.println("test number: "+testNumber + " - Error in DDA. should drop: " + expectedDropCardsList.toString()+ "  AI dropped: "+resultCardsList.toString());
					isAlgorithmOk=false;
					break;
				}
			}
			if(isAlgorithmOk){
				System.out.println("test number: "+testNumber + " - DDA works fine. original cards: "+origList.toString()+"  should drop: "+expectedDropCardsList +"  AI dropped: "+resultCardsList.toString());
			}
		}else{
			//size are different
			System.err.println("test number: "+testNumber + " - Error in DDA - size are different. expected cards: "+expectedDropCardsList.toString()+"  AI dropped: "+resultCardsList.toString());
		}
	}


	private PlayingCard[] createHandForTesting(char c1, char s1, char c2,
			char s2, char c3, char s3, char c4, char s4, char c5,
			char s5) {
		PlayingCard[] retVal = new PlayingCard[5];

		if(s1 != '0'){
			PlayingCard card1 = new PlayingCard(s1,c1 );
			card1.setSelected(true);
			retVal[0] = card1;
		}
		if(s2 != '0'){
			PlayingCard card2 = new PlayingCard(s2,c2 );
			card2.setSelected(true);
			retVal[1] = card2;
		}
		if(s3 != '0'){
			PlayingCard card3 = new PlayingCard(s3,c3 );
			card3.setSelected(true);
			retVal[2] = card3;
		}
		if(s4 != '0'){
			PlayingCard card4 = new PlayingCard(s4,c4 );
			card4.setSelected(true);
			retVal[3] = card4;
		}
		if(s5 != '0'){
			PlayingCard card5 = new PlayingCard(s5,c5 );
			card5.setSelected(true);
			retVal[4] = card5;
		}

		return retVal;
	}

	public static void addCardToDropCardList(PlayingCard card) {
		resultCardsList.add(card);
	}
}
