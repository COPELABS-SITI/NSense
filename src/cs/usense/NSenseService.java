/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It initialize and controls all the pipelines/modules, database and send the information to the NSenseActivity.
 * @author Saeik Firdose (COPELABS/ULHT), 
 * @author Luis Lopes (COPELABS/ULHT), 
 * @author Waldir Moreira (COPELABS/ULHT), 
 * @author Reddy Pallavali (COPELABS/ULHT)
 */
package cs.usense;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;

import cs.usense.R;
import cs.usense.accelerometer.*;
import cs.usense.bluetooth.BluetoothCore;
import cs.usense.bluetooth.BluetoothCore.socialWeight;
import cs.usense.db.NSenseDataSource;
import cs.usense.inferenceModule.SocialInteraction;
import cs.usense.inferenceModule.SocialityDetails;
import cs.usense.location.LocationEntry;
import cs.usense.location.LocationPipeline;
import cs.usense.microphone.MicrophonePipeline;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.os.Environment;

/**
 * This class provides methods to initialize all the pipelines, data base and get the data from all pipelines  
 */
public class NSenseService extends Service {

	private static final String TAG = "NSENSE";
	
	/** This variable is to check Accelerometer module to be initiated  */
	private static final String Accelerometer = "Accelerometer";
	
	/** This variable is to check Location module to be initiated  */
	private static final String GPS = "GPS";
	
	/** This variable is to check Bluetooth module to be initiated  */
	private static final Object BT = "BT";
	
	/** This variable is to check Microphone module to be initiated  */
	private static final Object Microphone="Microphone";

	/** This variable is used to access the listeners */
	private ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener> ();
	
	/** This class is to access functionality of Sensor Manager from accelerometer pipeline */
	private SensorManager mSensorManager;
	
	/** This class is to access functionality of Location pipeline */
	private LocationPipeline mLocationPipeline;
	
	/** This class is to access core functionality of Bluetooth pipeline */
	private BluetoothCore bluetooth;
	
	/** This class is to access functionality of Accelerometer pipeline */
	private AccelerometerPipeline mAccelerometerPipeline;
	
	/** This class is to access functionality of Microphone pipeline */
	private MicrophonePipeline mIcrophone;
	
	/** This variable to get the timer */
	private static final Timer timer = new Timer();

	/** This variable to get functionality of IBinder */
	private final IBinder mBinder = new LocalBinder();
	
	/** This class is to access functionality of Google Client API*/
	protected GoogleApiClient mGoogleApiClient;
	
	/** This variable to store the detected activity */
	protected  DetectedActivity mdetectdActivity; 
	
	/** This class is to access functionality of NSense Data base */
	private NSenseDataSource dataSource;
	
	/** This class is to access functionality of Social Interaction details */
	private SocialInteraction mSocialInteraction;
	
	/** This class is to access functionality of Intent Filter for battery level */
	IntentFilter batteryLevelFilter;
	
	/** This class is to access functionality of Broadcast Receiver */
	BroadcastReceiver batteryLevelReceiver;	
	
	/** This class is to access functionality of Intent for battery level */
	Intent batteryIntent;

	/** This variable to store the value of battery level */
	int level;

	public NSenseService(){

	}

	public interface NOTIFICATION_ID{
		public static int FOREGROUND_SERVICE = 112;
	}

	/**
	 * This method stores the the battery level and time
	 * @param currentTime Current time
	 * @param batteryLevel Battery level
	 */
	public void appendLogs(String currentTime, String batteryLevel)
	{       
		File logFile = new File(Environment.getExternalStorageDirectory()+File.separator+"Experiment","Timer.txt");
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			} 
			catch (IOException e)
			{

				e.printStackTrace();
			}
		}
		try
		{
			String oneChar = ";";
			/** BufferedWriter for performance, true to set append to file flag */
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.write(currentTime);
			buf.write(oneChar);
			buf.write(batteryLevel);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
	}
	
	/**
	 * This class binds the NSense service
	 *
	 */
	public class LocalBinder extends Binder {
		NSenseService getService() {
			return NSenseService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	public int onStartCommand(Intent intent, int i, int j)
	{
		//database
		dataSource = new NSenseDataSource(this);
		dataSource.openDB(true);

		/**
		 * Creates a file for Bluetooth debugging
		 */
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			/** handle case of no SDCARD present */
		} else {
			String dir = Environment.getExternalStorageDirectory()+File.separator+"Experiment";
			/** create folder */
			File folder = new File(dir); //folder name
			folder.mkdirs();
			Log.i("BT","!!!!!! ESCREVENDO ARQUIVO !!!!!! em " + dir);
			/** create file */
			File file = new File(dir, "ExperimentOutput.txt");
			try {
				if(!file.exists())
					file.createNewFile();
				else
					file.delete();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		/** Run this service as foreground */
		runAsForeground();

		if (intent != null) {
			Bundle bundleObject = intent.getExtras();

			//@SuppressWarnings("unchecked")
			ArrayList<SensorProduct> sensors = (ArrayList<SensorProduct>) bundleObject.get("sensors");

			for (SensorProduct s : sensors)
			{
				String name = s.getSensorName();
				if(name.equals(GPS)){
					if (mLocationPipeline == null) {
						mLocationPipeline = new LocationPipeline(this, dataSource);    						    
					}
				}else if(name.equals(Accelerometer)){
					mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
					if ((mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)){
						mAccelerometerPipeline = new AccelerometerPipeline(this, dataSource);
					}
				}else if (name.equals(BT)){
					bluetooth = new BluetoothCore(this, this, dataSource);
				}else if (name.equals(Microphone)){
					mIcrophone = new MicrophonePipeline(this, dataSource);
				}
			}

			mSocialInteraction=new SocialInteraction(this, bluetooth, mAccelerometerPipeline, dataSource);

			return START_STICKY;
		} else {
			return START_STICKY;
		}
	}

	/**
	 * This method provides the functionality to run the service as Foreground
	 */
	public void runAsForeground(){
		Intent notificationIntent = new Intent(this, NSenseActivity.class);
		PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
				notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(getString(R.string.foreGroundService))
				.setContentIntent(pendingIntent).build();

		startForeground(NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

	}

	/**
	 * This method close the NSense service if it is running on foreground
	 */
	public void closeAll(){

		stopForeground(true);
	}  	

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	public void onCreate()
	{

		Log.i(TAG,"Inside the on SensorManagerService onCreate method");
		batteryLevelReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				level = -1;
				if (currentLevel >= 0 && scale > 0) {
					level = (currentLevel * 100) / scale;
				}

				String level1 = String.valueOf(level); 
				String currentTime = (String) DateFormat.format("dd/MM - HH:mm:ss.sss", Calendar.getInstance().getTime());
				appendLogs(currentTime, level1);
			}
		}; 
		batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
		super.onCreate();	

	}

	/**
	 * This method close all the pipelines from the NSense Service
	 */
	public void onDestroy()
	{
		mLocationPipeline.close();
		bluetooth.close(NSenseService.this);
		mIcrophone.close(NSenseService.this);
		dataSource.closeDB();
		mAccelerometerPipeline.close(NSenseService.this);
		mSocialInteraction.close(NSenseService.this);
		stopForeground(true);
		timer.cancel();
		timer.purge();
		Log.i(TAG,"timer cancled "+timer);

		if (batteryLevelReceiver!=null) {
			this.unregisterReceiver(batteryLevelReceiver);
			batteryLevelReceiver=null;
		}

		super.onDestroy();
	}

	/** Listener Methods of all the pipelines to get data */
	public void setOnStateChangeListener (ChangeListener listener) 
	{
		this.listeners.add(listener);
	}

	public void notifyLocationOutdoor (String newMessage) {
		for (ChangeListener listener : this.listeners) 
		{
			listener.onLocationOutdoorChange(newMessage);
		}
	}

	public void notifyLocationIndoor (ArrayList<LocationEntry> arrayList) {
		for (ChangeListener listener : this.listeners) 
		{
			listener.onLocationIndoorChange(arrayList);
		}
	}
	public void notifyAction (String newMessage) {
		for (ChangeListener listener : this.listeners) 
		{
			listener.onActionChange(newMessage);
		}
	}
	public void notifySocialWeight (ArrayList<socialWeight> arrayList) {
		for (ChangeListener listener : this.listeners) 
		{
			listener.onSocialWeightChange(arrayList);
		}
	}
	public void notifySoundLevel (String newMessage) {
		for (ChangeListener listener : this.listeners) 
		{
			listener.onSoundLevelChange(newMessage);
		}
	}

	public void notifySociality(ArrayList<SocialityDetails> arrayList){
		for (ChangeListener listener : this.listeners) 
		{
			listener.onGetSociabilityChange(arrayList);
		}
	}

	public void notifyAvgSI(double avgSI){
		for (ChangeListener listener : this.listeners) 
		{
			listener.onSocialInteractionChange(avgSI);
		}
	}

	public void notifyAvgProp(double avgProp){
		for (ChangeListener listener : this.listeners) 
		{
			listener.onPropinquityChange(avgProp);
		}
	}

}


