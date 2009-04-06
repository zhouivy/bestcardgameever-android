package com.sheepzkeen.yaniv;

public class OpponentHand extends Hand {

	public OpponentHand() {
		super();
	}

	@Override
	public void drop() {
		// TODO AI

	}

	@Override
	public void pickup(PlayingCardsCollection deck) {
		// TODO AI

	}

	@Override
	public void doYaniv() {
		// TODO AI
		
	}

	@Override
	public boolean shouldCardsBeVisible() {
		// TODO when game ends, should change to visible
		return false;
	}
	@Override
	/**
	 * no supported here 
	 */
	public boolean isCardSelected(int cardIndex) {
		return false;
	}
	@Override
	/**
	 * no supported here 
	 */
	public boolean isAnyCardSelected() {
		return false;
	}

}
