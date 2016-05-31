/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. This class provides support for accelerometer pipeline, and 
 * it used to listen the accelerometer sensor orientation changes.
 * @author Saeik Firdose (COPELABS/ULHT)
 *
 */

package cs.usense.accelerometer;

/**
 * This class provides an interface to updateBuffer 
 */
public interface AccelerometerListener {
	
	/**
	 * This method updateBuffer when accelerometer sensor orientation changes 
	 * @param x X-axis represent tilting the phone from left - right and vice versa 
	 * @param y Y-axis represent tilting the phone upside down and vice versa
	 * @param z Z-axis represent lifting the phone screen side up and down
	 */

	public void updateBuffer(float x, float y, float z);
	
}
