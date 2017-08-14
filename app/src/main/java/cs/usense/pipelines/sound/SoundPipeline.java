/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/5/26.
 * Class is part of the NSense application. It provides support for sound pipeline.
 */


package cs.usense.pipelines.sound;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import cs.usense.R;
import cs.usense.db.NSenseDataSource;
import cs.usense.exceptions.SensorNotFoundException;
import cs.usense.services.NSenseService;
import cs.usense.utilities.DateUtils;
import cs.usense.utilities.Utils;

/**
 * This class is contains the core functionalities of
 * the application relating to Microphone. The SoundManager provides all the information from
 * Microphone adapter so this class can perform the analysis over Environmental Sound prior to
 * storing the required information in the database.
 * Some of the code is adapted from Google's NoiseAlert application code.
 * @author Reddy Pallavali (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */

public class SoundPipeline implements SoundManagerListener {

	/** TAG to debug the SoundPipeline class */
	private static final String TAG = "SoundPipeline";

	/** Microphone filename */
	private static final String MICROPHONE_FILENAME = "Microphone";

	/** Time between samples. By default, three samples for each SI Computation */
	private static final int TIME_BETWEEN_SAMPLES = 40 * 1000;

	/** All db values above this value are high */
	private static final int HIGH_SOUND_LEVEL = 50;

	/** Noisy db value */
	private static final int NOISY_SOUND_LEVEL = 89;

	/** Quiet db value */
	private static final int QUIET_SOUND_LEVEL = 20;

	/** This object is used to get sound samples */
	private SoundManager mSoundManager;

	/** This class is to access functionality of NSense Data base */
	private NSenseDataSource mDataSource;

	/** This class is to use send or process message */
	private Handler mHandler = new Handler();

	/** This object stores the application context */
	private Context mContext;


	/** This Runnable fetches sound levels */
	private Runnable mThread = new Runnable() {

		@Override
		public void run() {
			mSoundManager.getSoundInDB();
			mHandler.postDelayed(this, TIME_BETWEEN_SAMPLES);
			Log.i(TAG, "It was scheduled a new recording process after " + TIME_BETWEEN_SAMPLES + " seconds");
		}
	};

	/**
	 * This method constructs the SoundPipeline
	 * @param callback Supply NSenseService object for MainActivity to use
	 * @param dataSource NSenseDataSource to access various methods and information of the USense Data base
	 * @throws SensorNotFoundException this exception is triggered when the microphone sensor is missing
	 */
	public SoundPipeline(NSenseService callback, NSenseDataSource dataSource) throws SensorNotFoundException {
		Log.i(TAG, "The SoundPipeline constructor was invoked");
		mContext = callback.getApplicationContext();
		checkIfSoundPipelineCanBeInstantiated();
		mSoundManager = new SoundManager(this, callback.getApplicationContext());
		mDataSource = dataSource;
		start();
	}

	/**
	 * This method checks if the device has a microphone, if not throws an exception
	 * @throws SensorNotFoundException this exception is triggered when the microphone sensor is missing
	 */
	private void checkIfSoundPipelineCanBeInstantiated() throws SensorNotFoundException {
		PackageManager pm = mContext.getPackageManager();
		if(!pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
			throw new SensorNotFoundException(mContext.getString(R.string.sensor_not_found_message, mContext.getString(R.string.Microphone)));
	}

	/**
	 * This method evaluates the sound received.
	 * @param soundDB sound level in decibels
	 * @param soundLevel sound level object
     * @return sound level evaluated
     */
	private SoundLevel evaluateSoundLevel(long soundDB, SoundLevel soundLevel) {
		Log.i(TAG, "evaluateSoundLevel method was invoked");
		String evaluatedSound = "NA";
		if (soundDB > 0) {
			if (soundDB > HIGH_SOUND_LEVEL) {
				if (soundDB > NOISY_SOUND_LEVEL) {
					soundLevel.setNoisyTime(soundLevel.getNoisyTime() + TIME_BETWEEN_SAMPLES);
					evaluatedSound = "NOISY";
				} else {
					soundLevel.setAlertTime(soundLevel.getAlertTime() + TIME_BETWEEN_SAMPLES);
					evaluatedSound = "ALERT";
				}
			} else {
				if (soundDB < QUIET_SOUND_LEVEL) {
					soundLevel.setQuietTime(soundLevel.getQuietTime() + TIME_BETWEEN_SAMPLES);
					evaluatedSound = "QUIET";
				} else {
					soundLevel.setNormalTime(soundLevel.getNormalTime() + TIME_BETWEEN_SAMPLES);
					evaluatedSound = "NORMAL";
				}
			}
		}
		Log.i(TAG, "Sound evaluated: " + evaluatedSound);
		Utils.appendLogs(MICROPHONE_FILENAME, evaluatedSound);
		mDataSource.insertSoundRegistry(DateUtils.getTimeNowAsStringSecond(), evaluatedSound);
		return soundLevel;
	}

	/**
	 * This method provides four different levels of sound and their duration Based on the captured sound decibels
	 * @param soundDB soundLevel in decibels
	 */
	private void updateSoundLevel(long soundDB) {
		Log.i(TAG, "updateSoundLevel method was invoked");
		if(mDataSource.hasSoundLevel(DateUtils.getTodaysDate())){
			mDataSource.updateSoundLevel(evaluateSoundLevel(soundDB, mDataSource.getSoundLevel(DateUtils.getTodaysDate())));
		} else {
			mDataSource.registerNewSoundLevel(evaluateSoundLevel(soundDB, new SoundLevel()));
		}
	}

	/**
	 * This method initialize the sound manager class which start Capturing sound waves from microphone adapter.
	 */
	private void start() {
		Log.i(TAG, "start method was invoked");
		mHandler.post(mThread);
	}

	/**
	 * This method receives the sound level in decibels.
	 * @param soundLevel sound level in decibels
     */
	@Override
	public void onReceiveSound(long soundLevel) {
		Log.i(TAG, "onReceiveSound method was invoked");
		Log.i(TAG, "Sound level on is " + soundLevel);
		updateSoundLevel(soundLevel);
	}

	/**
	 * This method close this pipeline
	 */
	public void close() {
		Log.i(TAG, "close method was invoked");
		mHandler.removeCallbacks(mThread);
	}
}