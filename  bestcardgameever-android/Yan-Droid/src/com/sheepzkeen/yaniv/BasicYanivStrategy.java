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

	/* (non-Javadoc)
	 * @see com.sheepzkeen.yaniv.YanivStrategy#decideDrop()
	 */
	@Override
	public void decideDrop(PlayingCard[] cards) {
		ArrayList<PlayingCard> highestValCard = new ArrayList<PlayingCard>();
		ArrayList<PlayingCard> highestValSet = new ArrayList<PlayingCard>();
		ArrayList<PlayingCard> highestValSeries = new ArrayList<PlayingCard>();
		
		// find highest single card
		Arrays.sort(cards);
		highestValCard.add(cards[0]);
		// find highest set (7,7,7 etc.)
		for (PlayingCard card : cards) {
		
		}
		// divide cards by suit and find highest value series
		
		
		//decide which is highest and drop it
		
		

	}

	/* (non-Javadoc)
	 * @see com.sheepzkeen.yaniv.YanivStrategy#decidePickUp()
	 */
	@Override
	public PlayingCard decidePickUp() {
		return null;
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.sheepzkeen.yaniv.YanivStrategy#decidePickUp()
	 */
	@Override
	public boolean decideYaniv(PlayingCard[] cards) {
		return true;
	}

}
