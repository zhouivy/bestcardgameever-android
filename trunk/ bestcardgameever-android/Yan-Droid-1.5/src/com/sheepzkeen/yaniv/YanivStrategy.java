package com.sheepzkeen.yaniv;

/**
 * This is a strategy for the AI player
 * this class' descendants should be passed to player when instance is created
 * @author Elad
 *
 */
public interface YanivStrategy {
	public void decideDrop(PlayingCard[] cards);
	public PlayingCard decidePickUp(PlayingCardsCollection thrown,PlayingCardsCollection deck);
	public boolean decideYaniv(PlayingCard[] cards);
}
