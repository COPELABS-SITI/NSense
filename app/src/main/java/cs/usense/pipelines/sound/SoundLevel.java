/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/5/26.
 * Class is part of the NSense application. It provides support for sound pipeline.
 */

package cs.usense.pipelines.sound;

import cs.usense.utilities.DateUtils;

/**
 * This class extracts the different sound levels such as Quite,
 * Normal, Alert, and Noisy based on the sound frequency levels.
 * @author Reddy Pallavali (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class SoundLevel {
	
	/** This variable stores the data's date */
	private String mSoundDate;

	/** This variable stores the quantity of time relative to a quiet environment */
	private long mQuietTime;

	/** This variable stores the quantity of time relative to a normal environment */
	private long mNormalTime;

	/** This variable stores the quantity of time relative to a alert environment */
	private long mAlertTime;

	/** This variable stores the quantity of time relative to a noisy environment */
	private long mNoisyTime;

	/**
	 * This method is the constructor of SoundLevel class
	 */
	public SoundLevel() {
		mSoundDate = DateUtils.getTodaysDate();
	}

	/**
	 * This method set the date on which the time duration of each sound level 
	 * @param soundDate mSoundDate into SoundLevel object
	 */
	public void setSoundDate(String soundDate) {
		mSoundDate = soundDate;
	}

	/**
	 * This method set the time duration in Quite 
	 * @param quietTime QuiteTime into SoundLevel object
	 */
	public void setQuietTime(long quietTime) {
		mQuietTime = quietTime;
	}

	/**
	 * This method Set the time duration in Normal 
	 * @param normalTime mNormalTime into SoundLevel object
	 */
	public void setNormalTime(long normalTime) {
		mNormalTime = normalTime;
	}

	/**
	 * This method set the time duration in Alert 
	 * @param alertTime mAlertTime into SoundLevel object
	 */
	public void setAlertTime(long alertTime){
		mAlertTime = alertTime;
	}

	/**
	 * This method set the time duration in Noisy 
	 * @param noisyTime mNoisyTime into SoundLevel object
	 */
	public void setNoisyTime(long noisyTime) {
		mNoisyTime = noisyTime;
	}

	/**
	 * This method get the current date
	 * @return mSoundDate the system date
	 */
	public String getSoundDate() {
		return mSoundDate;
	}

	/**
	 * This method get the QUITE Time
	 * @return mQuietTime the time duration of Quite Level
	 */
	public long getQuietTime() {
		return mQuietTime;
	}

	/**
	 * This method get the Normal Time
	 * @return mNormalTime the time duration of Normal Level
	 */
	public long getNormalTime() {
		return mNormalTime;
	}

	/**
	 * This method get the Alert Time
	 * @return mAlertTime the time duration of Alert Level
	 */
	public long getAlertTime() {
		return mAlertTime;
	}

	/**
	 * This method get the Noise Time
	 * @return mNoisyTime the time duration of Noise Level
	 */
	public long getNoisyTime() {
		return mNoisyTime;
	}

	/**
	 * This method return a string containing the mSoundDate, mQuietTime, mNormalTime, mAlertTime and mNoisyTime.
	 * @return string String with rowID, mSoundDate, mQuietTime, mNormalTime, mAlertTime and mNoisyTime values
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("NoiseDate: " + this.mSoundDate + "\n");
		sb.append("mQuietTime: " + this.mQuietTime + "\n");
		sb.append("mNormalTime: " + this.mNormalTime + "\n");
		sb.append("mAlertTime: " + this.mAlertTime + "\n");
		sb.append("mNoisyTime: " + this.mNoisyTime + "\n");
		return sb.toString();
	}

}