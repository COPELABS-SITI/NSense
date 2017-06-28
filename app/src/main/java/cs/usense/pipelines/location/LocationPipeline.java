/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class is the entry point of FusionLocation Pipeline.
 * It initializes all necessary modules to compute the 
 * relative distance to other devices.
 * @author Luis Amaral Lopes (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.pipelines.location;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import java.util.Timer;

import cs.usense.db.NSenseDataSource;
import cs.usense.services.NSenseService;

public class LocationPipeline {

	/** This variable is used to represent BT MAC information on txt record */
	static final String BT_MAC_INFO = "0";

	/** This variable is used to represent interests information on txt record */
	static final String INTERESTS_INFO = "1";
	
	/** Relative Position module - manage the WiFi P2P to compute the relative distance */
	private RelativePositionWiFiNoConnection mRelativePositionWiFiNoConnection;
	
	/** FusionLocation module - provides my location, lat and long */
	private FusionLocation mFusionLocation;

	private Context mContext;

	private WifiBroadcastReceiver mWifiBroadcastReceiver;
	
	/** This variable is used to schedule a task to clear old entries on location table */
	private Timer mTimer = new Timer();


	/**
	 * FusionLocation Pipeline constructor - It initializes the Relative Position module
	 * and a Timer to remove old entries
	 * @param callback NSense Service module
	 * @param dataSource NSense Data base
	 */
	public LocationPipeline(NSenseService callback, NSenseDataSource dataSource) {
        dataSource.cleanLocationTable();

		new RelativePositionBT(callback.getApplicationContext(), dataSource, this);

		mContext = callback.getApplicationContext();
		mWifiBroadcastReceiver = new WifiBroadcastReceiver();
		mContext.registerReceiver(mWifiBroadcastReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        mRelativePositionWiFiNoConnection = new RelativePositionWiFiNoConnection(mContext, dataSource);
        mFusionLocation = new FusionLocation(callback, dataSource);
        //Timer that checks every 3 minutes, old entries inside location table (Devices that are no more near the user)
        mTimer.scheduleAtFixedRate(new CleanTask(dataSource, this), 180 * 1000, 180 * 1000);
    }

	/**
     * Close the FusionLocation Pipeline
     * Calling this function will close FusionLocation Pipeline in your app.
     * */
    public void close(){
		mContext.getApplicationContext().unregisterReceiver(mWifiBroadcastReceiver);

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