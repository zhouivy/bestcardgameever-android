package com.geekadoo.logic;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

//TODO make into singleton!
/**
 * This class is used to determine whose turn it is it will be populated with a
 * cyclic array of hands and will allow asking : who's turn is it and telling it
 * that X's turn is over
 * 
 * @author Elad
 * 
 */
public class Turn<T> implements Serializable {

	/**
	 * 
	 */
	public interface OnTurnStartedListener<T> extends Serializable {
		void onTurnStarted(T currentPlayer);
	}

	/**
	 * Will be called after a full iteration of players has ended i.e. if
	 * player1, opponent1, opponent2, opponent3 have finished playing, this
	 * event will be fired
	 */
	public interface OnRoundEndedListener extends Serializable {
		void onRoundEnded();
	}

	private static final long serialVersionUID = 1L;
	private static final String LOG_TAG = "TURN";
	private ArrayList<T> players;
	private int turnIndex;
	private int roundIdx;
	private transient ArrayList<OnTurnStartedListener<T>> turnStartListenerList;
	private transient ArrayList<OnRoundEndedListener> roundEndedListenerList;

	public Turn() {
		throw new UnsupportedOperationException("cannot init without players");
	}

	public Turn(ArrayList<T> players, int startingPlayerIndex) {
		this.players = players;
		newRound(startingPlayerIndex, true);
	}

	public void newRound(int startingPlayerIndex, boolean isFirstRound) {
		this.turnIndex = startingPlayerIndex;
		
		if (!isFirstRound){
			this.roundIdx++;
		}
		else {
			this.roundIdx = 0;
		}
	}

	public int getCurrentRoundNumber() {
		return roundIdx;
	}

	public T next() {
		Log.v(LOG_TAG, "next player");
		turnIndex = (turnIndex + 1) % players.size();
		if (turnIndex == 0) {
			roundIdx++;
			fireTurnEndedEvent();
		}

		T retVal = players.get(turnIndex);
		fireTurnStartEvent(retVal);
		return retVal;
	}

	public void fireTurnEndedEvent() {
		if (roundEndedListenerList != null) {
			for (OnRoundEndedListener l : roundEndedListenerList) {
				l.onRoundEnded();
			}
		}
	}

	public void fireTurnStartEvent(T startingEntity) {
		if (turnStartListenerList != null) {
			for (OnTurnStartedListener<T> l : turnStartListenerList) {
				l.onTurnStarted(startingEntity);
			}
		}
	}

	public T peek() {
		Log.v(LOG_TAG, "current turn is " + turnIndex);
		return players.get(turnIndex);
	}

	public void addOnTurnEndedListener(OnTurnStartedListener<T> l) {
		if (turnStartListenerList == null) {
			// Since turnEndListenerList has to be transient,
			// we lazy load it to make sure that it exists even
			// if the turn class deserialized and it was not deserialized
			turnStartListenerList = new ArrayList<OnTurnStartedListener<T>>();
		}
		this.turnStartListenerList.add(l);
	}

	public void addOnRoundEndedListener(OnRoundEndedListener l) {
		if (roundEndedListenerList == null) {
			// Since roundEndListenerList has to be transient,
			// we lazy load it to make sure that it exists even
			// if the turn class deserialized and it was not deserialized
			roundEndedListenerList = new ArrayList<OnRoundEndedListener>();
		}
		this.roundEndedListenerList.add(l);
	}

}
