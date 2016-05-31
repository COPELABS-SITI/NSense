/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. It provides support for accelerometer pipeline.
 * @author Saeik Firdose (COPELABS/ULHT)
 *
 */
package cs.usense.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * This class provides some methods to provide the actions to the UsenseService 
 */
public class AccelerSenManager {

	private static final String TAG = "ACCELEROMETER";
	
	/** Interface to global information about an application environment. */
	private static Context aContext = null;
	
	/** This class is to check the type of sensor */
	private static Sensor sensor;
	
	/** This class is to access functionality of Sensor Manager */
	private static SensorManager sensorManager;

	/** you could use an OrientationListener array instead if you plans to use more than one listener */
	private static AccelerometerListener listener;

	/** indicates whether or not Accelerometer Sensor is supported */
	private static Boolean supported;
	
	/** indicates whether or not Accelerometer Sensor is running */
	private static boolean running = false;

	/**
	 * This method start listening to the accelerometer sensor changes
	 * @param context Interface to global information about an application environment. 
	 */	
	public AccelerSenManager(Context context) {
		Log.i(TAG,"Inside the AccelerSenManager constructor");
		aContext = context;

		startListening();
		sensorManager = (SensorManager)context.getSystemService("sensor");    

	}
	
	/**
	 * This method close the listener if it is not listening
	 */
	public void close(){
		if(! "".equals(isListening())){
			stopListening();
		}

	}

	/**
	 * This method set the listener
	 * @param listener To listen the accelerometer sensor orientation changes
	 */
	public void setAccelerListener(AccelerometerListener listener) {
		this.listener = listener;
	}

	/**
	 * This method to checck whether sensor manager class is listening to orientation changes 
	 * @return running true if the manager is listening to orientation changes
	 */
	public static boolean isListening() {
		return running;
	}

	/**
	 * This method Unregisters listeners
	 */
	public void stopListening() {
		running = false;
		try {
			if (sensorManager != null && sensorEventListener != null) {
				sensorManager.unregisterListener(sensorEventListener);
			}
		} catch (Exception e) {}
	}

	/**
	 * This method returns true if at least one Accelerometer sensor is available
	 * @param context Interface to global information about an application environment. 
	 * @return true if Accelerometer sensor is available
	 */
	public static boolean isSupported(Context context) {
		aContext = context;
		if (supported == null) {
			if (aContext != null) {
				sensorManager = (SensorManager) aContext.getSystemService(Context.SENSOR_SERVICE);
				Sensor acceSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);    
				Sensor acceSensor1 = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
				if(acceSensor1 != null){
					supported = true;
				}else if(acceSensor != null){
					supported = true;
				}
			} else 
			{
				supported = false;
			}
		}
		return supported;
	}

	/**
	 * This method registers a listener and start listening
	 * @return running accelerometerListener callback for accelerometer events
	 */
	public void startListening()
	{
		sensorManager = (SensorManager) aContext.getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor sensor1 = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		if(sensor1 != null){
			running = sensorManager.registerListener(sensorEventListener, sensor1,SensorManager.SENSOR_DELAY_FASTEST); 
		} else if (sensor != null){
			running = sensorManager.registerListener(sensorEventListener, sensor,SensorManager.SENSOR_DELAY_FASTEST); 
		}
	}


	/**
	 * This method listen to events from the accelerometer listener
	 */
	private static SensorEventListener sensorEventListener =
			new SensorEventListener() {

		private float x = 0;
		private float y = 0;
		private float z = 0;

		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		public void onSensorChanged(SensorEvent event) {

			x = event.values[0];
			y = event.values[1];
			z = event.values[2];

			listener.updateBuffer(x, y, z);
		}

	};

}
