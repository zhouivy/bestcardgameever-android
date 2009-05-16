package com.sheepzkeen.yaniv;

import android.widget.ImageView;

public class CardAndViewPair {
	protected PlayingCard card;
	protected ImageView view;
	
	public CardAndViewPair(PlayingCard card, ImageView view) {
		this.card = card;
		this.view = view;
	}

}
