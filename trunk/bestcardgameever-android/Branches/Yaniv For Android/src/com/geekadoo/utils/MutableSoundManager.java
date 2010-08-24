package com.geekadoo.utils;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.geekadoo.R;

public class MutableSoundManager {

	private SoundPool mSoundPool;
	private HashMap<Integer, Integer> mSoundPoolMap;
	private AudioManager mAudioManager;
	private Context mContext;
	private static MutableSoundManager mSoundManager;
	private static final String PREFS_NAME = "YANIV_PREFS";
	private static final String SILENT_MODE_PROPERTY = "silentMode";

	private MutableSoundManager() {

	}

	// TODO: make proper singleton
	public static synchronized MutableSoundManager getInstance(Context context) {

		if (mSoundManager == null) {
			mSoundManager = new MutableSoundManager();
			mSoundManager.initSounds(context);
			mSoundManager.addSound(R.raw.applause, R.raw.applause);
			mSoundManager.addSound(R.raw.ooooh, R.raw.ooooh);
			mSoundManager.addSound(R.raw.damnit, R.raw.damnit);
			mSoundManager.addSound(R.raw.next, R.raw.next);
			mSoundManager.addSound(R.raw.pop, R.raw.pop);
			mSoundManager.addSound(R.raw.shuffle, R.raw.shuffle);
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

		if (!(mContext.getSharedPreferences(PREFS_NAME,0).getBoolean(SILENT_MODE_PROPERTY, false))) {
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