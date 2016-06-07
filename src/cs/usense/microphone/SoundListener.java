/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for Microphone pipeline and
 * this class is the listener class to update sound levels in the application.
 * @author Reddy Pallavali (COPELABS/ULHT)
 */

package cs.usense.microphone;

/**
 * This class is the listener class to update sound levels in the application.
 */
public interface SoundListener {
	
	/**
	 * This method called when SoundLevel table has been updated in the database
	 * @param soundLevel SoundLevel object
	 */
	public void updateSoundLevel(SoundLevel soundLevel);
}
