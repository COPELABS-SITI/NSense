/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/5/26.
 * Class is part of the NSense application. It provides support for accelerometer pipeline.
 */

package cs.usense.pipelines.motion;

/**
 * This class has some global variables related with the motion pipeline
 * @author Saeik Firdose (COPELABS/ULHT)
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
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