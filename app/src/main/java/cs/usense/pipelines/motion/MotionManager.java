/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for accelerometer pipeline.
 * @author Saeik Firdose (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.pipelines.motion;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import cs.usense.services.NSenseService;


/**
 * This class provides some methods to provide the actions to the NSenseService
 */
class MotionManager implements SensorEventListener {

	/** This constant variable stores the name of the linear accelerometer sensor */
	private static final String TYPE_LINEAR_ACCELERATION = "TYPE_LINEAR_ACCELERATION";

	/** This constant variable stores the name of the accelerometer sensor */
	private static final String TYPE_ACCELEROMETER = "TYPE_ACCELEROMETER";

	/** This variable is used to debug MotionManager class */
	private static final String TAG = "ACCELEROMETER";

	/** Indicates whether or not Accelerometer Sensor is running */
	private boolean mIsRunning = false;

	/** This class is to access functionality of Sensor Manager */
	private SensorManager mSensorManager;

	/** you could use an OrientationListener array instead if you plans to use more than one mListener */
	private MotionListener mListener;

	/** This variable stores the sensor name */
	private String mSensorName;

	/**
	 * This method start mIsRunning to the accelerometer sensor changes
	 * @param callback Interface to global information about an application environment.
	 */
	MotionManager(NSenseService callback, MotionListener listener) {
		Log.i(TAG, "MotionManager constructor was invoked");
		mListener = listener;
		start(callback.getApplicationContext());
	}

	/**
	 * This method checks if accelerometer is linear type
	 * @return true if accelerometer is linear
     */
	boolean isAccelerometerLinear() {
		return mSensorName.equals(TYPE_LINEAR_ACCELERATION);
	}

	/**
	 * This method close the mListener if it is not mIsRunning
	 */
	public void close(){
		Log.i(TAG, "close was invoked");
		if(mIsRunning){
			stopListening();
		}
	}

	/**
	 * This method Unregisters mListeners
	 */
	private void stopListening() {
		Log.i(TAG, "stopListening was invoked");
		mSensorManager.unregisterListener(this);
		mIsRunning = false;
	}

	/**
	 * This method registers a mListener and start mIsRunning
	 * running accelerometerListener callback for accelerometer events
	 */
	private void start(Context mContext) {
		Log.i(TAG, "start was invoked");
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		mIsRunning = mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);
		mSensorName = TYPE_LINEAR_ACCELERATION;
		if(!mIsRunning) {
			mIsRunning = mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
			mSensorName = TYPE_ACCELEROMETER;
		}
		Log.i(TAG, "The type of accelerometer instantiated is " + mSensorName);
	}

	@Override
	public void onSensorChanged(final SensorEvent event) {
		mListener.updateBuffer(event.values[0], event.values[1], event.values[2]);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.i(TAG, "The accelerometer accuracy has been changed");
	}

}