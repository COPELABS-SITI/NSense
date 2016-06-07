/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for Microphone pipeline and
 * this class extracts the different sound levels such as Quite, Normal, Alert, and Noisy based on the sound frequency levels.
 * @author Reddy Pallavali (COPELABS/ULHT)
 */

package cs.usense.microphone;

/**
 * This class extracts the different sound levels such as Quite, Normal, Alert, and Noisy based on the sound frequency levels.
 */
public class SoundLevel {
	
	/** RowID variable of the SoundLevel object */
	public int RowID;
	
	/** SoundDate variable of the SoundLevel object */
	public long SoundDate;
	
	/** QuietTime variable of the SoundLevel object */
	public long QuietTime;
	
	/** NormalTime variable of the SoundLevel object */
	public long NormalTime;
	
	/** AlertTime variable of the SoundLevel object */
	public long AlertTime;
	
	/** NoiseTime variable of the SoundLevel object */
	public long NoiseTime;



	/**
	 * This class Construct the SoundLevel
	 */
	public SoundLevel() {
		super();
	}

	/**
	 * This method return a string containing the RowID, SoundDate, QuietTime, NormalTime, AlertTime and NoiseTime.
	 * @return string String with RowID, SoundDate, QuietTime, NormalTime, AlertTime and NoiseTime values
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("KEY_ROWID: " + this.RowID + "\n");
		sb.append("NoiseDate: " + this.SoundDate + "\n");
		sb.append("QuietTime: " + this.QuietTime + "\n");
		sb.append("NormalTime: " + this.NormalTime+ "\n");
		sb.append("AlertTime: " + this.AlertTime+ "\n");
		sb.append("NoiseTime: " + this.NoiseTime + "\n");


		return sb.toString();
	}

	/**
	 * This method get the rowID
	 * @return RowID the rowID
	 */
	public int getRowID() {
		return RowID;
	}

	/**
	 * This method set the row ID 
	 * @param rowID RowId into SoundLevel object
	 */
	public void setRowID(int rowID) {
		RowID = rowID;
	}

	/**
	 * This method set the date on which the time duration of each sound level 
	 * @param l SoundDate into SoundLevel object
	 */
	public void setSoundDate(int l) {
		SoundDate = l;

	}

	/**
	 * This method set the time duration in Quite 
	 * @param quietTime QuiteTime into SoundLevel object
	 */

	public void setQuietTime(long quietTime) {
		QuietTime = quietTime;

	}

	/**
	 * This method Set the time duration in Normal 
	 * @param normalTime NormalTime into SoundLevel object
	 */
	public void setNormalTime(long normalTime) {
		NormalTime = normalTime;

	}

	/**
	 * This method set the time duration in Alert 
	 * @param alertTime AlertTime into SoundLevel object
	 */
	public void setAlertTime(long alertTime){
		AlertTime = alertTime;
	}

	/**
	 * This method set the time duration in Noisy 
	 * @param noiseTime NoiseTime into SoundLevel object
	 */
	public void setNoiseTime(long noiseTime) {
		NoiseTime = noiseTime;

	}

	/**
	 * This method get the current date
	 * @return SoundDate the system date 
	 */
	public long getSoundDate() {

		return SoundDate;
	}

	/**
	 * This method get the QUITE Time
	 * @return QuietTime the time duration of Quite Level
	 */
	public long getQuietTime() {

		return QuietTime;
	}

	/**
	 * This method get the Normal Time
	 * @return NormalTime the time duration of Normal Level
	 */
	public long getNormalTime() {

		return NormalTime;
	}

	/**
	 * This method get the Alert Time
	 * @return AlertTime the time duration of Alert Level
	 */
	public long getAlertTime() {
		return AlertTime;

	}

	/**
	 * This method get the Noise Time
	 * @return NoiseTime the time duration of Noise Level
	 */
	public long getNoiseTime() {

		return NoiseTime;
	}


}
