/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/16.
 * Class is part of the NSense application. It provides support for location pipeline.
 */

package cs.usense.pipelines.location;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;

import java.util.Timer;

import cs.usense.R;
import cs.usense.db.NSenseDataSource;
import cs.usense.exceptions.SensorNotFoundException;
import cs.usense.services.NSenseService;


/**
 * This class instantiates all necessary modules to compute
 * geographical position and relative distance to other devices
 * @author Luis Amaral Lopes (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class LocationPipeline {

	/** This variable is used to represent BT MAC information on txt record */
	static final String BT_MAC_INFO = "0";

	/** This variable is used to represent interests information on txt record */
	static final String INTERESTS_INFO = "1";
	
	/** Relative Position module - manage the WiFi P2P to compute the relative distance */
	private RelativePositionWiFiNoConnection mRelativePositionWiFiNoConnection;

	/** This object is used to turn on WI-FI automatically */
	private WifiBroadcastReceiver mWifiBroadcastReceiver;

	/** Relative Position module - manage the BT to compute the relative distance */
	private RelativePositionBT mRelativePositionBT;
	
	/** FusionLocation module - provides my location, lat and long */
	private FusionLocation mFusionLocation;

	/** This variable is used to schedule a task to clear old entries on location table */
	private Timer mTimer = new Timer();

	/** This object contains the application context */
	private Context mContext;

	/**
	 * FusionLocation Pipeline constructor - It initializes the Relative Position module
	 * and a Timer to remove old entries
	 * @param callback NSense Service module
	 * @param dataSource NSense Data base
	 * @throws SensorNotFoundException this exception is triggered when some feature requested for location pipeline is missing
	 */
	public LocationPipeline(NSenseService callback, NSenseDataSource dataSource) throws SensorNotFoundException {
		mContext = callback.getApplicationContext();
		checkIfLocationPipelineCanBeInstantiated();
        dataSource.cleanLocationTable();

		mRelativePositionBT = new RelativePositionBT(mContext, dataSource);
		mWifiBroadcastReceiver = new WifiBroadcastReceiver();
		mContext.registerReceiver(mWifiBroadcastReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        mRelativePositionWiFiNoConnection = new RelativePositionWiFiNoConnection(mContext, dataSource);
        mFusionLocation = new FusionLocation(callback, dataSource);
		//Timer that checks every 3 minutes, old entries inside location table (Devices that are no more near the user)
		mTimer.scheduleAtFixedRate(new CleanTask(dataSource, this), 180 * 1000, 180 * 1000);
    }

	/**
	 * This method checks if all requested features are present on the device
	 * @throws SensorNotFoundException this exception is triggered when some feature is missing
	 */
	private void checkIfLocationPipelineCanBeInstantiated() throws SensorNotFoundException {
		PackageManager pm = mContext.getPackageManager();
		if(!pm.hasSystemFeature(PackageManager.FEATURE_WIFI))
			throw new SensorNotFoundException(mContext.getString(R.string.sensor_not_found_message, mContext.getString(R.string.Wi_Fi)));
		if(!pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT))
			throw new SensorNotFoundException(mContext.getString(R.string.sensor_not_found_message, mContext.getString(R.string.Wi_Fi_Direct)));
		if(!pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
			throw new SensorNotFoundException(mContext.getString(R.string.sensor_not_found_message, mContext.getString(R.string.Bluetooth)));
	}

	/**
     * This method stops the location pipeline
     */
    public void close(){
		mContext.unregisterReceiver(mWifiBroadcastReceiver);
		if (mRelativePositionBT != null) {
			mRelativePositionBT.close();
			mRelativePositionBT = null;
		}
		if (mRelativePositionWiFiNoConnection != null) {
			mRelativePositionWiFiNoConnection.close();
			mRelativePositionWiFiNoConnection = null;
		}
		if (mFusionLocation != null) {
			mFusionLocation.close();
			mFusionLocation = null;
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
    }
	
}