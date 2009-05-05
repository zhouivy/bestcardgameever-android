package com.sheepzkeen.yaniv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.R.array;

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

	public TestDecideDropCardsAlgorithm(YanivStrategy strategy){
		this.strategy = strategy;
	}

	public void testCardsCombination(){
		System.out.println("----------- start testing 'decide drop cards algorithm' -----------");
		performTest("qc", "kc", "1c", "7d", "8h"  ,  "qc", "kc", "1c", "00", "00");
		performTest("2c", "3c", "1c", "2d", "1d"  ,  "2c", "3c", "1c", "00", "00");
		performTest("ob", "3c", "1c", "2d", "1d"  ,  "ob", "3c", "1c", "00", "00");
		performTest("ob", "4c", "1c", "or", "8h"  ,  "8h", "00", "00", "00", "00");
		performTest("9c", "or", "ob", "6c", "8h"  ,  "9c", "or", "ob", "6c", "00");
		performTest("jc", "or", "ob", "1c", "8h"  ,  "jc", "or", "ob", "1c", "00");
		performTest("tc", "jc", "or", "ob", "1c"  ,  "tc", "jc", "or", "ob", "1c");
		performTest("1c", "2h", "th", "9s", "2s"  ,  "th", "00", "00", "00", "00");
		performTest("8d", "4c", "4s", "2h", "3d"  ,  "4c", "4s", "00", "00", "00");
		performTest("jd", "6c", "6s", "th", "td"  ,  "th", "td", "00", "00", "00");
		performTest("4d", "4c", "4s", "2d", "3d"  ,  "4c", "4s", "4d", "00", "00");
		performTest("1d", "6c", "4s", "2d", "3d"  ,  "1d", "2d", "3d", "00", "00");
		performTest("kd", "qd", "jd", "td", "ob"  ,  "kd", "qd", "jd", "td", "ob");
		performTest("6c", "5s", "3d", "2d", "1d"  ,  "3d", "2d", "1d", "00", "00");
		performTest("6c", "6s", "3d", "4d", "5d"  ,  "3d", "4d", "5d", "00", "00");
		performTest("6c", "7c", "8c", "4d", "5d"  ,  "6c", "7c", "8c", "00", "00");
		performTest("6c", "7c", "8c", "1c", "00"  ,  "6c", "7c", "8c", "00", "00");
		performTest("qc", "or", "ob", "1c", "00"  ,  "qc", "or", "ob", "1c", "00");
		performTest("4c", "3c", "ob", "1c", "00"  ,  "4c", "3c", "ob", "1c", "00");
		performTest("4c", "3c", "ob", "1c", "qc"  ,  "qc", "1c", "ob", "00", "00");
		System.out.println("----------- end testing 'decide drop cards algorithm' -----------");
	}

	private void performTest(
			String card1,
			String card2,
			String card3,
			String card4,
			String card5,
			String card1ToDrop,
			String card2ToDrop,
			String card3ToDrop,
			String card4ToDrop,
			String card5ToDrop){

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


		PlayingCard[] cardsList = new PlayingCard[5];
		cardsList = createHandForTesting(
				card1Number, card1Suit,
				card2Number, card2Suit,
				card3Number, card3Suit,
				card4Number, card4Suit,
				card5Number, card5Suit);

		
		PlayingCard[] cardsListToDrop = new PlayingCard[5];
		cardsListToDrop = createHandForTesting(
				card1NumberToDrop, card1SuitToDrop,
				card2NumberToDrop, card2SuitToDrop,
				card3NumberToDrop, card3SuitToDrop,
				card4NumberToDrop, card4SuitToDrop,
				card5NumberToDrop, card5SuitToDrop);

		resultCardsList = new ArrayList<PlayingCard>(5);
		strategy.decideDrop(cardsList);

		//just for printing
		List<PlayingCard> origList = Arrays.asList(cardsList);  
		
		//convert array[] to arrayList for comparing
		List<PlayingCard> list = Arrays.asList(cardsListToDrop);  
		ArrayList<PlayingCard> arrayOfCards = new ArrayList<PlayingCard>(list);  

		//remove nulls
		for(int i=arrayOfCards.size();i>0;i--){
			if(arrayOfCards.get(i-1) == null){
				arrayOfCards.remove(i-1);
			}
		}
		
		boolean isAlgorithmOk=true;
		if(arrayOfCards.size() == resultCardsList.size()){
			for (PlayingCard card : resultCardsList) {
				boolean isCardExist=false;
				for(PlayingCard cardToDrop: arrayOfCards){
					if(cardToDrop.compareTo(card)== 0){
						isCardExist=true;
						break;
					}
				}
				if(! isCardExist){
					System.out.println("Error in decide drop cards algorithm. should drop: " + arrayOfCards.toString()+ "  AI drop: "+resultCardsList.toString());
					isAlgorithmOk=false;
					break;
				}
			}
			if(isAlgorithmOk){
				System.out.println("decide algorithm works fine. original cards: "+origList.toString()+"  cards for drop: "+resultCardsList.toString());
			}
		}else{
			System.out.println("Error in decide drop cards algorithm - size are different. expected cards: "+arrayOfCards.toString()+"  AI drop: "+resultCardsList.toString());
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
		// TODO Auto-generated method stub
		return retVal;
	}

	public static void addCardToDropCardList(PlayingCard card) {
		resultCardsList.add(card);
	}
}
