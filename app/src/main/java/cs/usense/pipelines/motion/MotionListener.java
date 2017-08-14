/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/5/26.
 * Class is part of the NSense application. It provides support for accelerometer pipeline.
 */

package cs.usense.pipelines.motion;

/**
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * This interface is used to listen the accelerometer sensor orientation changes.
 * @author Saeik Firdose (COPELABS/ULHT)
 * @version 1.0, 2015
 */
interface MotionListener {
	
	/**
	 * This method updateBuffer when accelerometer sensor orientation changes 
	 * @param x X-axis represent tilting the phone from left - right and vice versa 
	 * @param y Y-axis represent tilting the phone upside down and vice versa
	 * @param z Z-axis represent lifting the phone screen side up and down
	 */
	void updateBuffer(float x, float y, float z);

}