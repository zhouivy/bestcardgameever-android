package com.sheepzkeen.yaniv;

import java.lang.reflect.Field;


public class PlayingCard {
	
	private String suit;
	private String value;
	private boolean isVisible;
	
	public boolean isVisible() {
		return isVisible;
	}
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public void setSuit(String suit) {
		this.suit = suit;
	}
	public String getSuit() {
		return suit;
	}

	public String getPngName() {
		return new String(suit+value);
	}
	
	public PlayingCard(String suit, String value) {
		super();
		this.suit = suit;
		this.value = value;
	}
	
	public int getImageResourceId(){
		Field f;
		int id = -1;
		try {
			f = R.drawable.class.getDeclaredField(getPngName());
			id = f.getInt(null);
			//TODO: Add logging!
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return id;
	}
@Override
public String toString() {
	return getSuit()+getValue();
}
}
