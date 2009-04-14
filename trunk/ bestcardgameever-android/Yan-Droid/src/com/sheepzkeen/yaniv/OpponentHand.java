package com.sheepzkeen.yaniv;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class OpponentHand extends Hand {

	public OpponentHand(View container, ImageView[] cards, TextView name) {
		super(container,cards,name);
		//start the game with cards hidden
		setShouldCardsBeShown(false);
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
	protected void selectCardsToDrop() {
		//TODO:AI
	}

	@Override
	public boolean isAwaitingInput() {
		return false;
	}
}
