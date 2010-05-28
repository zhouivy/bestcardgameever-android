package com.geekadoo.logic;

import com.geekadoo.logic.ai.YanivStrategy;


/**
 * Descendant of the Hand class @see {@link Hand}
 * This is a hand that is controlled by artificial intelligence.
 * @author Elad
 */
public class OpponentHand extends Hand {
	private static final long serialVersionUID = 1L;

	public OpponentHand(YanivStrategy basicYanivStrategy) {
		super();
		// Start the game with cards hidden
		setShouldCardsBeShown(false);
		this.strategy = basicYanivStrategy;
	}

	@Override
	protected void selectCardsToDrop() {
		// Here the hand needs to decide what to do - which card to drop - highest value X (series, sameVal or card)
		strategy.decideDrop();
	}

	@Override
	public boolean isAwaitingInput() {
		return false;
	}

	@Override
	protected boolean shouldYaniv() {
		return strategy.decideYaniv();
	}
}