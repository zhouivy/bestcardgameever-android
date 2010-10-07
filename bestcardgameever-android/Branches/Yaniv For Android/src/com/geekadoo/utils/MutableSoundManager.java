package com.geekadoo.utils;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.Log;

import com.geekadoo.R;

public class MutableSoundManager {

	private SoundPool mSoundPool;
	private HashMap<Integer, Integer> mSoundPoolMap;
	private AudioManager mAudioManager;
	private Context mContext;
	private static MutableSoundManager mSoundManager;
	private static final String LOG_TAG = "MutableSoundManager";

	private MutableSoundManager() {

	}

	// TODO: make proper singleton
	public static synchronized MutableSoundManager getInstance(Context context) {
		Log.v(LOG_TAG, "in getInstance");
		if (mSoundManager == null) {
			Log.v(LOG_TAG, "in getInstance - Initializing for the first time");
			mSoundManager = new MutableSoundManager();
			mSoundManager.initSounds(context);
			mSoundManager.addSound(R.raw.shuffle, R.raw.shuffle);
			mSoundManager.addSound(R.raw.applause, R.raw.applause);
			mSoundManager.addSound(R.raw.ooooh, R.raw.ooooh);
			mSoundManager.addSound(R.raw.damnit, R.raw.damnit);
			mSoundManager.addSound(R.raw.next, R.raw.next);
			mSoundManager.addSound(R.raw.pop, R.raw.pop);
			mSoundManager.addSound(R.raw.yes, R.raw.yes);
		}
		return mSoundManager;
	}

	private void initSounds(Context theContext) {
		mContext = theContext;
		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		mSoundPoolMap = new HashMap<Integer, Integer>();
		mAudioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
	}

	private void addSound(int index, int soundID) {
		mSoundPoolMap.put(index, mSoundPool.load(mContext, soundID, index));
	}

	public void playSound(int index) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		if ((settings.getBoolean(mContext.getString(R.string.enableGameSoundsPref), false))) {
			int streamVolume = mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			mSoundPool.play(mSoundPoolMap.get(index), streamVolume,
					streamVolume, 1, 0, 1f);
		}
	}

	public void playLoopedSound(int index) {

		int streamVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume,
				1, -1, 1f);
	}

}