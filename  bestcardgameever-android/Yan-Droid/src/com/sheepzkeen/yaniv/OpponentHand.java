package com.sheepzkeen.yaniv;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class OpponentHand extends Hand {

	public OpponentHand(View container, ImageView[] cards, TextView name) {
		super(container,cards,name);
	}


	@Override
	public void pickup(PlayingCard card) {
		if(card != null){
			addCard(card);
		}else{
			//no card given, need to decide which card to pickup
		}
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

	@Override
	public boolean isAwaitingInput() {
		return false;
	}
}
