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
	
	/**
	 * Returns the value of the card with some exceptions
	 * J, Q, K are each worth 10
	 * Joker (o) is worth 0
	 * @return the value of this card.
	 */
	public int getIntValue(){
		int retVal = -1;
		try{
			retVal = Integer.parseInt(this.value);
		} catch (NumberFormatException nfe) {
			switch (value.charAt(0)) {
			case 'j':
			case 'q':
			case 'k':
				retVal = 10;
				break;
			case 'o':
				retVal = 0;
			default:
				break;
			}
		}
		
		return retVal;
	}
@Override
public String toString() {
	return getSuit()+getValue();
}
}
