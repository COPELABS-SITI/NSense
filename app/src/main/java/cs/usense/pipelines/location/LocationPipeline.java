/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/16.
 * Class is part of the NSense application. It provides support for location pipeline.
 */

package cs.usense.pipelines.location;

import java.util.Timer;

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

	/** Manage the Wifi and Wifi P2P to receive user's interests and compute relative distance */
	private RelativePositionWifi mRelativePositionWiFi;

	/** Manage the BT to compute the relative distance */
	private RelativePositionBT mRelativePositionBT;

	/** FusionLocation module - provides my location, lat and long */
	private FusionLocation mFusionLocation;

	/** This variable is used to schedule a task to clear old entries on location table */
	private Timer mTimer = new Timer();

	/**
	 * FusionLocation Pipeline constructor - It initializes the Relative Position module
	 * and a Timer to remove old entries
	 * @param callback NSense Service module
	 * @param dataSource NSense Data base
	 * @throws SensorNotFoundException this exception is triggered when some feature requested for location pipeline is missing
	 */
	public LocationPipeline(NSenseService callback, NSenseDataSource dataSource) throws SensorNotFoundException {
		mRelativePositionWiFi = new RelativePositionWifi(dataSource);
		mRelativePositionBT = new RelativePositionBT(callback.getApplicationContext(), dataSource);
        mFusionLocation = new FusionLocation(callback, dataSource);
		mTimer.scheduleAtFixedRate(new CleanTask(dataSource, this), 180 * 1000, 180 * 1000);
    }

	/**
     * This method stops the location pipeline
     */
    public void close(){
		if (mRelativePositionBT != null) {
			mRelativePositionBT.close();
			mRelativePositionBT = null;
		}
		if (mRelativePositionWiFi != null) {
			mRelativePositionWiFi.close();
			mRelativePositionWiFi = null;
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