package com.sheepzkeen.yaniv;

import java.util.ArrayList;

/**
 * This class is used to determine whose turn it is
 * it will be populated with a cyclic array of hands and will allow asking : who's turn is it
 * and telling it that X's turn is over
 * @author Elad
 *
 */
public class Turn<T> {
	
	public interface OnTurnEndedListener<T> {

		void onTurnEnded(T currentPlayer);

	}

	private ArrayList<T> players;
	private int turnIndex;
	private OnTurnEndedListener<T> turnEndListener;

	public Turn() {
		throw new UnsupportedOperationException("cannot init without hands");
	}
	
	public Turn(ArrayList<T> players){
		this.players = players;
		this.turnIndex = 0;
	}
	
	public T next() {
		turnIndex = (turnIndex + 1) % players.size();
		T retVal = players.get(turnIndex);
		turnEndListener.onTurnEnded(retVal);
		return retVal;
	}
	
	public T peek(){
		return players.get(turnIndex);
	}

	public void setOnTurnEndedListener(OnTurnEndedListener<T> l) {
		this.turnEndListener = l;
		
	}

}
