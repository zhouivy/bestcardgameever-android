package com.geekadoo.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Represents the graphical components of a hand of cards
 * This class dictates the visibility and other UI properties
 * @author Elad
 */
public  class HandGUI {
	private static final long serialVersionUID = 1L;

	private TextView	handLabelView;
	private ImageView[] cardsViews;
	private View container;
	
	public HandGUI(View container, ImageView[] cardsViews, TextView handLabelView) {
		this.container = container;
		this.cardsViews = cardsViews;
		this.handLabelView = handLabelView;
	}
	
	public ImageView[] getCardsViews() {
		return cardsViews;
	}

	public View getContainer() {
		return container;
	}

	public TextView getHandLabelView() {
		return handLabelView;
	}
}