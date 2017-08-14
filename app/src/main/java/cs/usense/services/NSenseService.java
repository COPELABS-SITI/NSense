/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/25.
 * Class is part of the NSense application.
 */


package cs.usense.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import cs.usense.R;
import cs.usense.activities.MainActivity;
import cs.usense.db.NSenseDataSource;
import cs.usense.energy.EnergyManager;
import cs.usense.energy.WakeFullReceiver;
import cs.usense.exceptions.SensorNotFoundException;
import cs.usense.inferenceModule.SocialDetail;
import cs.usense.inferenceModule.SocialInteraction;
import cs.usense.map.MapActivityListener;
import cs.usense.pipelines.location.LocationPipeline;
import cs.usense.pipelines.motion.MotionPipeline;
import cs.usense.pipelines.proximity.BluetoothCore;
import cs.usense.pipelines.sound.SoundPipeline;
import cs.usense.reports.ReportManager;


/**
 * It initialize and controls all the pipelines/modules,
 * database and send the information to the MapActivity.
 * @author Saeik Firdose (COPELABS/ULHT),
 * @author Luis Lopes (COPELABS/ULHT),
 * @author Waldir Moreira (COPELABS/ULHT),
 * @author Reddy Pallavali (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class NSenseService extends Service {

	/** This TAG is used to debug NSenseService class */
	private static final String TAG = "NSenseService";

	/** This is the foreground id */
	private static final int FOREGROUND_SERVICE = 112;

	/** This variable is used to set the time between wake ups */
	private static final int TIME_BETWEEN_WAKE_UPS = 40 * 1000;

	/** This variable stores the information about the NSenseService */
	private static NSenseService sNsenseService;

	/** This variable to get functionality of IBinder */
	private final IBinder mBinder = new LocalBinder();

	/** This variable is used to access the mListeners */
	private ArrayList<MapActivityListener> mListeners = new ArrayList<>();

	/** This handler is used to schedule the next wake-up */
	private Handler mHandler = new Handler();

	/** This variable is used to monitor the battery of the device */
	private EnergyManager mEnergyManager;

	/** This class is to access functionality of FusionLocation pipeline */
	private LocationPipeline mLocationPipeline;

	/** This class is to access core functionality of Bluetooth pipeline */
	private BluetoothCore mBluetooth;

	/** This class is to access functionality of Microphone pipeline */
	private SoundPipeline mMicrophone;

	/** This class is to access functionality of Accelerometer pipeline */
	private MotionPipeline mAccelerometerPipeline;

	/** This class is to access functionality of Social Interaction details */
	private SocialInteraction mSocialInteraction;

	/** This object is used to run report tasks */
	private ReportManager mReportManager;

	/** This class is to access functionality of NSense Data base */
	private NSenseDataSource mDataSource;

	/** This variable is used to wake up the device */
	private PowerManager.WakeLock mWakeLock;


	/** The code inside of this Runnable runs when the device is waked-up */
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			Log.i(TAG, "The next wake was scheduled");
			sendBroadcast(new Intent(getApplicationContext(), WakeFullReceiver.class));
			mHandler.postDelayed(this, TIME_BETWEEN_WAKE_UPS);
		}
	};

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate was invoked");
		super.onCreate();
		sNsenseService = this;
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mWakeLock()");
	}

	/**
	 * This method checks if the service is running right now.
	 * @param nsenseService  service to check
	 * @param mContext application context
	 * @return true if is running, if not returns false
	 */
	public static boolean isMyServiceRunning(Class<NSenseService> nsenseService, Context mContext) {
		Log.i(TAG, "isMyServiceRunning was invoked");
		boolean result = false;
		ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (service.service.getClassName().endsWith(".NSenseService")) {
				Log.i(TAG, "Inside running" + nsenseService.getClass().getName());
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * The code inside of this method runs when the device is awake
	 */
	public void doWhenDeviceIsAwake() {
		Log.i(TAG, "doWhenDeviceIsAwake was invoked");
		if(mWakeLock.isHeld()) {
			mWakeLock.release();
		} else {
			mWakeLock.acquire();
		}
	}

	public static NSenseService getService() {
		return sNsenseService;
	}

	/**
	 * This class binds the NSense service
	 */
	public class LocalBinder extends Binder {
		public NSenseService getService() {
			return NSenseService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int i, int j) {
		Log.i(TAG, "onStartCommand was invoked");
		try {
			mDataSource = NSenseDataSource.getInstance(this);

			/* Enable the notification icon */
			runAsForeground();

			/* Initializing the sensors */
			mLocationPipeline = new LocationPipeline(this, mDataSource);
			mBluetooth = new BluetoothCore(this, this, mDataSource);
			mMicrophone = new SoundPipeline(this, mDataSource);
			mAccelerometerPipeline = new MotionPipeline(this, mDataSource);
			mSocialInteraction = new SocialInteraction(this, mDataSource);
			//mEnergyManager = new EnergyManager(this);

			/* Intializing ReportManager class */
			mReportManager = new ReportManager(this, mDataSource);

			mHandler.post(mRunnable);
		} catch (SensorNotFoundException e) {
			e.showDialogError(this);
		}

		return START_STICKY;
	}


	/**
	 * This method provides the functionality to run the service as Foreground
	 */
	public void runAsForeground(){
		Log.i(TAG, "runAsForeground was invoked");
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_nsense))
				.setContentTitle(getString(R.string.app_name))
				.setContentText(getString(R.string.NSense_Service))
				.setContentIntent(pendingIntent)
				.build();

		startForeground(FOREGROUND_SERVICE, notification);
	}

	/**
	 * This method close all the pipelines from the NSense Service
	 */
	public void close() {
		Log.i(TAG, "close was invoked");
		if(mBluetooth != null)
			mBluetooth.close(NSenseService.this);
		if(mMicrophone != null)
			mMicrophone.close();
		if(mHandler != null)
			mHandler.removeCallbacks(mRunnable);
		if(mAccelerometerPipeline != null)
			mAccelerometerPipeline.close();
		if(mLocationPipeline != null)
			mLocationPipeline.close();
		if(mSocialInteraction != null)
			mSocialInteraction.close();
		if(mEnergyManager != null)
			mEnergyManager.close();
		if(mReportManager != null)
			mReportManager.close();
		if(mDataSource != null)
			mDataSource.closeDB();
		stopForeground(true);
	}

	/** Listener Methods of all the pipelines to get data */
	public void setOnStateChangeListener(MapActivityListener listener)  {
		mListeners.clear();
		mListeners.add(listener);
	}

	public void notifySociability(ArrayList<SocialDetail> arrayList) {
		Log.i(TAG, "notifySociability");
		for (MapActivityListener listener : mListeners) {
			listener.onSociabilityChange(arrayList);
		}
	}

	public void notifyLocation(Location location) {
		Log.i(TAG, "notifyLocation");
		for (MapActivityListener listener : mListeners) {
			listener.onLocationChange(location);
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy was invoked");
		close();
		super.onDestroy();
	}

}
