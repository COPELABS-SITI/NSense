/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/5/26.
 * Class is part of the NSense application. It provides support for accelerometer pipeline.
 */

package cs.usense.pipelines.motion;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


/**
 * This class instantiates the accelerometer sensor and provide it's values
 * though updateBuffer method.
 * @author Saeik Firdose (COPELABS/ULHT)
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
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
	 * This method is the constructor of MotionManager class
	 * @param context application context
	 * @param listener used to notify the manager with the accelerometer data
	 */
	MotionManager(Context context, MotionListener listener) {
		Log.i(TAG, "MotionManager constructor was invoked");
		mListener = listener;
		start(context);
	}

	/**
	 * This method checks if accelerometer is linear type
	 * @return true if accelerometer is linear
     */
	boolean isAccelerometerLinear() {
		return mSensorName.equals(TYPE_LINEAR_ACCELERATION);
	}

	/**
	 * This method register the accelerometer and put it running
	 * @param context application context
	 */
	private void start(Context context) {
		Log.i(TAG, "start was invoked");
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mIsRunning = mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);
		mSensorName = TYPE_LINEAR_ACCELERATION;
		if(!mIsRunning) {
			mIsRunning = mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
			mSensorName = TYPE_ACCELEROMETER;
		}
		Log.i(TAG, "The type of accelerometer instantiated is " + mSensorName);
	}

	/**
	 * This method provides the accelerometer data
	 * @param event accelerometer data
	 */
	@Override
	public void onSensorChanged(final SensorEvent event) {
		mListener.updateBuffer(event.values[0], event.values[1], event.values[2]);
	}

	/**
	 * This method is triggered when the accelerometer's accuracy changes
	 * @param sensor sensor which changed it's accuracy
	 * @param accuracy new accuracy
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.i(TAG, "The accelerometer accuracy has been changed");
	}

	/**
	 * This method stops and unregisters the accelerometer
	 */
	private void stopListening() {
		Log.i(TAG, "stopListening was invoked");
		mSensorManager.unregisterListener(this);
		mIsRunning = false;
	}

	/**
	 * This method checks if the accelerometer is running, if it's running stops it
	 */
	public void close(){
		Log.i(TAG, "close was invoked");
		if(mIsRunning){
			stopListening();
		}
	}

}