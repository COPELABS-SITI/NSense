/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. It provides support for accelerometer pipeline.
 * @author Saeik Firdose (COPELABS/ULHT)
 */
package cs.usense.accelerometer;

/**
 * This class provides global variables
 */
public class Globals {
	/** Accelerometer buffer capacity of Globals object */
	public static final int ACCELEROMETER_BUFFER_CAPACITY = 2048;
	
	/** Accelerometer block capacity of Globals object */
	public static final int ACCELEROMETER_BLOCK_CAPACITY = 64;
	
	/** Activity types of Globals object */
	public static final String ACTIVITY_TYPES[] = {
		"Standing", "Walking", "Running", 
	};

}
