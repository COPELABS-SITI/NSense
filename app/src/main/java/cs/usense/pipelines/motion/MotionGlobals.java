/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for accelerometer pipeline.
 * @author Saeik Firdose (COPELABS/ULHT)
 */

package cs.usense.pipelines.motion;

/**
 * This class provides global variables
 */
public abstract class MotionGlobals {

	/** Activity types of SoundGlobals object */
	public static final String ACTIVITY_TYPES[] = {"STATIONARY", "MOVING"};

	/** Activity types of SoundGlobals object */
	public static final String TIME_FRAME_TYPES[] = {"Weekend", "WeekDay"};

	/** Accelerometer buffer capacity of SoundGlobals object */
	static final int ACCELEROMETER_BUFFER_CAPACITY = 2048;
	
	/** Accelerometer block capacity of SoundGlobals object */
	static final int ACCELEROMETER_BLOCK_CAPACITY = 64;

}