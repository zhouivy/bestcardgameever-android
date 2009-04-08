package com.sheepzkeen.yaniv;

public class PlayerHand extends Hand {

	private boolean isP1Turn;
	
	

	public PlayerHand() {
		super();
		isP1Turn = true;//TODO!!!
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
	public void pickup() {
		// TODO this is already implemented in the UI, but in order to play a round, this should be implemented here somehow
		
	}
	
	

	/**
	 * returns true iff the player is currently in a state when he can pickup a card (from the thrown or deck)
	 * @return True iff the player is currently in a state when he can pickup a card (from the thrown or deck)
	 */
	public boolean canPickup() {
		// TODO need to activate the turn param
		return (isP1Turn && firstFreeLocation != 5);
	}
	

}
