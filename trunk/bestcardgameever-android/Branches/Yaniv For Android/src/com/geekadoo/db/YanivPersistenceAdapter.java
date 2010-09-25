package com.geekadoo.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.util.Log;

import com.geekadoo.exceptions.YanivPersistenceException;
import com.geekadoo.logic.GameData;
import com.geekadoo.logic.GameData.GameDifficultyEnum;

public class YanivPersistenceAdapter {
	public static final String GAME_DATA = "gamedata";
	public static final String FILE_NAME = "yanivSaveFile";
	public static final String LOG_TAG = "PERSISTENCE";

	private Context appCtx;

	public YanivPersistenceAdapter(Context appCtx) {
		super();
		this.appCtx = appCtx;

	}

	public GameData getSavedGameData(GameDifficultyEnum difficulty) throws YanivPersistenceException {
		FileInputStream file = null;
		GameData gd = null;
		try {
			try {
				file = appCtx.openFileInput(FILE_NAME);
			} catch (FileNotFoundException e) {
				Log.i(LOG_TAG, "File " + FILE_NAME
						+ " does not exist, probably a first run");
				setSavedGameData(GameData.createNewGame(difficulty));
				Log.i(LOG_TAG, "File " + FILE_NAME + " Created Succesfuly");
				file = appCtx.openFileInput(FILE_NAME);
			}

			ObjectInput input = new ObjectInputStream(file);
			gd = (GameData) input.readObject();
			Log.d(LOG_TAG,"file loaded succesfuly");
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "Could not open file " + FILE_NAME, e);
			throw new YanivPersistenceException(e);
		} catch (StreamCorruptedException e) {
			Log.e(LOG_TAG, "Stream was corrupt", e);
			throw new YanivPersistenceException(e);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Problem with I/O device", e);
			throw new YanivPersistenceException(e);
		} catch (ClassNotFoundException e) {
			Log.e(LOG_TAG, "The class was not found", e);
			throw new YanivPersistenceException(e);
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, "Could not close file " + FILE_NAME, e);
			}
		}
		return gd;
	}

	public void setSavedGameData(GameData savedGameData)
			throws YanivPersistenceException {
		FileOutputStream file = null;
		try {
			file = appCtx.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			ObjectOutput output = new ObjectOutputStream(file);
			output.writeObject(savedGameData);
			Log.d(LOG_TAG,"file saved succesfuly");
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "Could not open file " + FILE_NAME, e);
			throw new YanivPersistenceException(e);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Problem with I/O device", e);
			throw new YanivPersistenceException(e);
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, "Could not close file " + FILE_NAME, e);
			}
		}
	}
	
	public static boolean isSavedGameDataValid(Context appCtx){
		boolean retVal = true;
		try {
			appCtx.openFileInput(FILE_NAME);
		} catch (FileNotFoundException e) {
			retVal = false;
		}
		return retVal;
	}

	public static void deleteSavedGameData(Context appCtx){
		appCtx.deleteFile(FILE_NAME);
	}
}
