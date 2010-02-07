package com.sheepzkeen.yaniv;

import android.util.Log;
import android.widget.TextView;

public class TestDropCardsAlgorithm {

	private static final String YANIV_TEST_TAG = "YAN_TST";

	private Yaniv yaniv;

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
	
	private StringBuffer sb;
	private final String NEXT_LINE="\n"; 


	public TestDropCardsAlgorithm(Yaniv yaniv){
		this.yaniv = yaniv;
		sb = new StringBuffer("---------tests results---------");
	}
	
	/**
	 * 
	 *  c = CLUBS, d = DIAMOND, s = SPADES,h = HEARTS, s = SPADES
	 *  Examples: 2c= 2 clubs, 4d = 4 diamond, 00 = no card, ob = black joker ,js = spades jack
	 */
	public void testCardsCombination(){
		
		//positive tests
		sb.append(NEXT_LINE + "positive tests:");
		
		//one card
		performTest("2c", "00", "00", "00", "00", null);
		performTest("ob", "00", "00", "00", "00", null);
		performTest("2c", "ob", "00", "00", "00", null);
		performTest("2c", "ob", "or", "00", "00", null);
		
		//two cards with the same number
		performTest("2c", "2d", "00", "00", "00", null);
		performTest("1c", "1d", "00", "00", "00", null);
		performTest("2c", "2d", "ob", "00", "00", null);
		performTest("2c", "2d", "ob", "or", "00", null);
		performTest("1c", "1d", "ob", "00", "00", null);
		performTest("1c", "1d", "ob", "or", "00", null);
		
		
		//three cards with the same number
		performTest("2c", "2d", "2s", "00", "00", null);
		performTest("1c", "1d", "1s", "00", "00", null);
		performTest("2c", "2d", "2s", "ob", "00", null);
		performTest("2c", "2d", "2s", "ob", "or", null);
		performTest("1c", "1d", "1s", "ob", "00", null);
		performTest("1c", "1d", "1s", "ob", "or", null);
		
		//four cards with the same number
		performTest("2c", "2d", "2s", "2d", "00", null);
		performTest("1c", "1d", "1s", "1d", "00", null);
		performTest("2c", "2d", "2s", "2h", "or", null);
		performTest("1c", "1d", "1s", "1h", "or", null);
		
		//series of two cards with jokers
		performTest("2c", "ob", "4c", "00", "00", null);
		performTest("2c", "ob", "or", "5c", "00", null);
		performTest("1c", "ob", "2c", "00", "00", null);
		performTest("1c", "ob", "or", "2c", "00", null);
		performTest("qc", "ob", "1c", "00", "00", null);
		performTest("jc", "ob", "oe", "1c", "00", null);
		
		//series of three cards 
		performTest("2c", "3c", "4c", "00", "00", null);
		performTest("2c", "3c", "4c", "ob", "00", null);
		performTest("2c", "3c", "4c", "ob", "or", null);
		performTest("2c", "ob", "4c", "00", "00", null);
		performTest("2c", "ob", "or", "4c", "00", null);
		performTest("1c", "2c", "3c", "00", "00", null);
		performTest("1c", "2c", "3c", "ob", "00", null);
		performTest("1c", "2c", "3c", "ob", "or", null);
		performTest("qc", "kc", "1c", "00", "00", null);
		performTest("qc", "kc", "1c", "ob", "00", null);
		performTest("qc", "kc", "1c", "ob", "or", null);
		
		//series of four cards
		performTest("2c", "3c", "4c", "5c", "00", null);
		performTest("2c", "3c", "4c", "5c", "ob", null);
		performTest("1c", "2c", "3c", "4c", "00", null);
		performTest("1c", "2c", "3c", "4c", "ob", null);
		performTest("jc", "qc", "kc", "1c", "00", null);
		performTest("jc", "qc", "kc", "1c", "ob", null);
		performTest("2c", "3c", "ob", "5c", "6c", null);
		performTest("1c", "2c", "ob", "4c", "5c", null);
		performTest("tc", "jc", "ob", "kc", "1c", null);
		performTest("or", "qc", "ob", "1c", "00", null);
		performTest("1c", "ob", "3c", "or", "00", null);
		
		//series of five cards
		performTest("2c", "3c", "4c", "5c", "6c", null);
		performTest("1c","2c", "3c", "4c", "5c", null);
		performTest("tc","jc", "qc", "kc", "1c", null);
		
		
		//negative tests
		sb.append(NEXT_LINE + "negative tests:");
		performTest("2c","3c","4d","00","00","YE001");
		performTest("2c","3c","4d","ob","00","YE001");
		performTest("2c","3c","4d","ob","or","YE001");
		performTest("1c","2c","3d","00","00","YE001");
		performTest("1c","2c","3d","ob","00","YE001");
		performTest("1c","2c","3d","ob","or","YE001");
		
		performTest("1c","2c","00","00","00","YE002");
		performTest("kc","1c","00","00","00","YE002");
		performTest("2c","3c","00","00","00","YE002");
		
		performTest("2c","ob","5c","00","00","YE003");
		performTest("2c","ob","or","6c","00","YE003");
		
		performTest("1c","3c","4c","5c","00","YE004");
		performTest("1c","3c","4c","5c","6c","YE004");
		performTest("tc","jc","qc","1c","00","YE004");
		performTest("9c","tc","jc","qc","1c","YE004");
		performTest("1c","ob","4c","00","00","YE004");
		performTest("1c","ob","or","5c","00","YE004");
		performTest("jc","ob","1c","00","00","YE004");
		performTest("tc","ob","or","1c","00","YE004");
		
		Log.d(YANIV_TEST_TAG, "*****End of all rules tests*****");
		Log.d(YANIV_TEST_TAG,sb.toString());
	}
	
	private void performTest(	
			String card1,
			String card2,
			String card3,
			String card4,
			String card5,
			String exceptionNumber)
	{
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


		TextView t = new TextView(yaniv);
		t.setText("test");
		PlayerHand p1h = new PlayerHand(null,null,t);
		PlayingCard[] cardsList = new PlayingCard[5];

		try {
			cardsList = createHandForTesting(
					card1Number, card1Suit,
					card2Number, card2Suit,
					card3Number, card3Suit,
					card4Number, card4Suit,
					card5Number, card5Suit);

				p1h.cards = cardsList;
				p1h.selectCardsToDrop();
			
			sb.append(NEXT_LINE+("success on Hand test: [" +
									card1Number + card1Suit +" "+
									card2Number + card2Suit +" "+
									card3Number + card3Suit +" "+
									card4Number + card4Suit +" "+
									card5Number + card5Suit +" "+
									exceptionNumber + "]"));
		
			
		} catch (InvalidDropException iye) {
			if(iye.getMessage().contains(exceptionNumber))
			{
				sb.append(NEXT_LINE+("success on Hand test: [" +
						card1Number + card1Suit +" "+
						card2Number + card2Suit +" "+
						card3Number + card3Suit +" "+
						card4Number + card4Suit +" "+
						card5Number + card5Suit +" "+
						exceptionNumber + "]"));
			}
			else
			{
				sb.append(NEXT_LINE+("fail on Hand test: [" +
						card1Number + card1Suit +" "+
						card2Number + card2Suit +" "+
						card3Number + card3Suit +" "+
						card4Number + card4Suit +" "+
						card5Number + card5Suit +" "+
						exceptionNumber + "]"));
				
				String en = iye.getMessage().substring(1, 5);
				Log.e(YANIV_TEST_TAG,"expected for exception number: "+exceptionNumber +" and got number: "+en,iye);
			}
		}
		catch(Exception e){
			//failer in algo....
			Log.e(YANIV_TEST_TAG,"Failer in algorithm",e);
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
}