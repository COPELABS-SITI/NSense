/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for Microphone pipeline and
 * this class extracts the different sound levels such as Quite, Normal, Alert, and Noisy based on the sound frequency levels.
 * @author Reddy Pallavali (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.pipelines.sound;

import cs.usense.utilities.DateUtils;

/**
 * This class extracts the different sound levels such as Quite, Normal, Alert, and Noisy based on the sound frequency levels.
 */
public class SoundLevel {
	
	/** soundDate variable of the SoundLevel object */
	private String soundDate;
	
	/** quietTime variable of the SoundLevel object */
	private long quietTime;
	
	/** normalTime variable of the SoundLevel object */
	private long normalTime;
	
	/** alertTime variable of the SoundLevel object */
	private long alertTime;
	
	/** noisyTime variable of the SoundLevel object */
	private long noisyTime;

	/**
	 * SoundLevel class constructor
	 */
	public SoundLevel() {
		soundDate = DateUtils.getTodaysDate();
	}

	/**
	 * This method return a string containing the soundDate, quietTime, normalTime, alertTime and noisyTime.
	 * @return string String with rowID, soundDate, quietTime, normalTime, alertTime and noisyTime values
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("NoiseDate: " + this.soundDate + "\n");
		sb.append("quietTime: " + this.quietTime + "\n");
		sb.append("normalTime: " + this.normalTime + "\n");
		sb.append("alertTime: " + this.alertTime + "\n");
		sb.append("noisyTime: " + this.noisyTime + "\n");
		return sb.toString();
	}


	/**
	 * This method set the date on which the time duration of each sound level 
	 * @param soundDate soundDate into SoundLevel object
	 */
	public void setSoundDate(String soundDate) {
		this.soundDate = soundDate;
	}

	/**
	 * This method set the time duration in Quite 
	 * @param quietTime QuiteTime into SoundLevel object
	 */

	public void setQuietTime(long quietTime) {
		this.quietTime = quietTime;
	}

	/**
	 * This method Set the time duration in Normal 
	 * @param normalTime normalTime into SoundLevel object
	 */
	public void setNormalTime(long normalTime) {
		this.normalTime = normalTime;
	}

	/**
	 * This method set the time duration in Alert 
	 * @param alertTime alertTime into SoundLevel object
	 */
	public void setAlertTime(long alertTime){
		this.alertTime = alertTime;
	}

	/**
	 * This method set the time duration in Noisy 
	 * @param noisyTime noisyTime into SoundLevel object
	 */
	public void setNoisyTime(long noisyTime) {
		this.noisyTime = noisyTime;
	}

	/**
	 * This method get the current date
	 * @return soundDate the system date
	 */
	public String getSoundDate() {
		return soundDate;
	}

	/**
	 * This method get the QUITE Time
	 * @return quietTime the time duration of Quite Level
	 */
	public long getQuietTime() {
		return quietTime;
	}

	/**
	 * This method get the Normal Time
	 * @return normalTime the time duration of Normal Level
	 */
	public long getNormalTime() {
		return normalTime;
	}

	/**
	 * This method get the Alert Time
	 * @return alertTime the time duration of Alert Level
	 */
	public long getAlertTime() {
		return alertTime;
	}

	/**
	 * This method get the Noise Time
	 * @return noisyTime the time duration of Noise Level
	 */
	public long getNoisyTime() {
		return noisyTime;
	}

}