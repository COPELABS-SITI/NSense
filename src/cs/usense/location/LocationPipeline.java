/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the USense application.
 * This class is the entry point of Location Pipeline.
 * It initializes all necessary modules to compute the 
 * relative distance to other devices.
 * @author Luis Amaral Lopes (COPELABS/ULHT)
 */

package cs.usense.location;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cs.usense.UsenseService;
import cs.usense.db.UsenseDataSource;
import android.content.Context;

public class LocationPipeline {
    /** Interface to global information about an application environment. */
	private Context aContext = null;
	/** Usense Service module */
	private UsenseService callback = null;
	/** Usense Data base */
	private UsenseDataSource dataSource;
	/** Relative Position module - manage the WiFi P2P to compute the relative distance */
	private static RelativePositionWiFiNoConnection mRelativePositionWiFiNoConnection = null;
	
	/**
	 * Location Pipeline constructor - It initializes the Relative Position module 
	 * and a Timer to remove old entries
	 * @param callback Usense Service module
	 * @param dataSource Usense Data base
	 */
	public LocationPipeline(UsenseService callback, UsenseDataSource dataSource) {
        this.callback = callback;
        this.aContext = callback.getApplicationContext();
        this.dataSource = dataSource;
        dataSource.cleanLocationTable();
        
        mRelativePositionWiFiNoConnection = new RelativePositionWiFiNoConnection(this.aContext, dataSource, this);
                
        //Timer that checks every 3 minutes, old entries inside location table (Devices that are no more near the user)
        Timer timer = new Timer();
        TimerTask mCleanTask = new CleanTask(dataSource, this);
        timer.scheduleAtFixedRate(mCleanTask, 180 * 1000, 180 * 1000);
    }
	
	/**
     * Close the Location Pipeline
     * Calling this function will close Location Pipeline in your app.
     * */
    public void close(){
		if (mRelativePositionWiFiNoConnection != null) {
			mRelativePositionWiFiNoConnection.close();
			mRelativePositionWiFiNoConnection = null;
		}
    }
	
    /**
     * Notifies a database change to the listeners.
     */
    public void notifyDataBaseChange() {
    	callback.notifyLocationIndoor(new ArrayList<LocationEntry>(dataSource.getAllLocationEntries().values()));
    }
}
