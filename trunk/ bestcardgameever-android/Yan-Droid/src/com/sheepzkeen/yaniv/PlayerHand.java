package com.sheepzkeen.yaniv;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayerHand extends Hand {

	
	

	public PlayerHand(View container, ImageView[] cards, TextView name) {
		super(container,cards,name);
	}

	@Override
	public void doYaniv() {
		// TODO this
	}

	//should Override
	@Override
	protected void selectCardsToDrop() {
		//Do nothing here, selection was performed by the drop action
		
	}


	@Override
	public boolean shouldCardsBeVisible() {
		// Always true for player
		return true;
	}

	@Override
	public void pickup(PlayingCard card) {
		addCard(card);
	}
	
	

	/**
	 * returns true iff the player is currently in a state when he can pickup a card (from the thrown or deck)
	 * @return True iff the player is currently in a state when he can pickup a card (from the thrown or deck)
	 */
	public boolean canPickup() {
		return (firstFreeLocation != Yaniv.YANIV_NUM_CARDS);
	}
	
	@Override
	public boolean isAwaitingInput() {
		return true;
	}
	

}
