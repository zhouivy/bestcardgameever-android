package com.sheepzkeen.yaniv;

public class OpponentHand extends Hand {

	public OpponentHand() {
		super();
	}


	@Override
	public void pickup() {
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
	protected void selectCardsToDrop() {
		//TODO:AI
	}

}
