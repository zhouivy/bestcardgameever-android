package com.geekadoo.logic;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;
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
	public interface OnTurnStartedListener<T> extends Serializable{
		void onTurnStarted(T currentPlayer);
	}
	
	private static final long serialVersionUID = 1L;
	private static final String TURN_TAG = "TURN";
	private ArrayList<T> players;
	private int turnIndex;
	private int rounds;
	private transient ArrayList<OnTurnStartedListener<T>> turnStartListenerList;

	public Turn() {
		throw new UnsupportedOperationException("cannot init without players");
	}
	
	public Turn(ArrayList<T> players, int startingPlayerIndex){
		this.players = players;
		this.turnIndex = startingPlayerIndex;
		this.rounds = 0;
	}
	
	public int getRounds() {
		return rounds;
	}

	public T next() {
		Log.v(TURN_TAG,"next player");
		turnIndex = (turnIndex + 1) % players.size();
		if(turnIndex == 0){
			rounds++;
		}
		T retVal = players.get(turnIndex);
		for (OnTurnStartedListener<T> l : turnStartListenerList) {
			l.onTurnStarted(retVal);
		}
		return retVal;
	}
	
	public T peek(){
		Log.v(TURN_TAG, "current turn is "+turnIndex);
		return players.get(turnIndex);
	}

	public void addOnTurnEndedListener(OnTurnStartedListener<T> l) {
		if(turnStartListenerList == null){
			// Since turnEndListenerList has to be transient, 
			//we lazy load it to make sure that it exists even 
			//if the turn class deserialized and it was not deserialized
			turnStartListenerList = new ArrayList<OnTurnStartedListener<T>>();
		}
		this.turnStartListenerList.add(l);
	}

	
}
