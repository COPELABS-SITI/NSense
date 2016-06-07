/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class verifies every location entry at the DB and 
 * removes the entries that were updated more than 6 minutes ago.
 * This class is called by a Timer, and it is repeatedly called. 
 * @author Luis Amaral Lopes (COPELABS/ULHT)
 */

package cs.usense.location;

import java.util.Map;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import cs.usense.db.NSenseDataSource;

public class CleanTask extends TimerTask {

	private String TAG = "LocationPipeline";
	
	/** NSense Database */
	private NSenseDataSource dataSource;
	/** Location Pipeline class. Used to notify the location pipeline about a change in DB. */
    private LocationPipeline callback;
    /** Timeout for a location entry to be removed. */
    private int timeout = 360000;
    /** Handler used to notify the callback about a DB change. */
    private Handler notifyDataBaseChangeHandler = new Handler() {
    	public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	callback.notifyDataBaseChange();
        }
    };

    /**
     * Clean Task constructor
     * @param dataSource the NSense data base
     * @param callback the Location Pipeline
     */
    public CleanTask(NSenseDataSource dataSource, LocationPipeline callback) {
        this.dataSource = dataSource;
        this.callback = callback;
    }
    /**
     * Thread core - It checks the last update in every location table entry.
     */
	@Override
	public void run() {
		Map<String, LocationEntry> entries  = dataSource.getAllLocationEntries();
		if (!entries.isEmpty()) {
			for (LocationEntry entry : entries.values()) {
				if ((SystemClock.elapsedRealtime() - entry.getLastUpdate()) > timeout) {
					// Probably the device is gone
					dataSource.removeLocationEntry(entry);
					notifyDataBaseChangeHandler.sendEmptyMessage(0);
					Log.i(TAG, "Clear Task");
				}
			}
		}
	}
}