package com.geekadoo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class MutableMediaPlayer {
	private static final String PREFS_NAME = "YANIV_PREFS";
	private static final String SILENT_MODE_PROPERTY = "silentMode";

	public static void play(Context context, int resId) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		boolean silent = settings.getBoolean(SILENT_MODE_PROPERTY, false);

		if (!silent) {
			MediaPlayer.create(context,  resId).start();
		}
	}

}
