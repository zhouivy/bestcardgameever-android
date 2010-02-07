package com.sheepzkeen.yaniv;

import java.io.Serializable;
import java.util.ArrayList;
//TODO make into singleton!
/**
 * This class is used to determine whose turn it is
 * it will be populated with a cyclic array of hands and will allow asking : who's turn is it
 * and telling it that X's turn is over
 * @author Elad
 *
 */
public class Turn<T> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public interface OnTurnEndedListener<T> {

		void onTurnEnded(T currentPlayer);

	}

	private ArrayList<T> players;
	private int turnIndex;
	private int rounds;
	private ArrayList<OnTurnEndedListener<T>> turnEndListenerList;

	public Turn() {
		throw new UnsupportedOperationException("cannot init without players");
	}
	
	public Turn(ArrayList<T> players, int startingPlayerIndex){
		this.players = players;
		this.turnIndex = startingPlayerIndex;
		this.rounds = 0;
		turnEndListenerList = new ArrayList<OnTurnEndedListener<T>>();
	}
	
	public int getRounds() {
		return rounds;
	}

	public T next() {
		turnIndex = (turnIndex + 1) % players.size();
		if(turnIndex == 0){
			rounds++;
		}
		T retVal = players.get(turnIndex);
		for (OnTurnEndedListener<T> l : turnEndListenerList) {
			l.onTurnEnded(retVal);
		}
		return retVal;
	}
	
	public T peek(){
		return players.get(turnIndex);
	}

	public void addOnTurnEndedListener(OnTurnEndedListener<T> l) {
		this.turnEndListenerList.add(l);
		
	}
}
