/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class verifies every location entry at the DB and 
 * removes the entries that were updated more than 6 minutes ago.
 * This class is called by a Timer, and it is repeatedly called. 
 * @author Luis Amaral Lopes (COPELABS/ULHT)
 */

package cs.usense.pipelines.location;

import android.os.SystemClock;
import android.util.Log;

import java.util.Map;
import java.util.TimerTask;

import cs.usense.db.NSenseDataSource;

public class CleanTask extends TimerTask {

	/** This TAG is used to debug CleanTask class */
	private static final String TAG = "CleanTask";

	/** Timeout for a location entry to be removed. */
	private static final int TIMEOUT = 360000;

	/** NSense Database */
	private NSenseDataSource dataSource;


    /**
     * Clean Task constructor
     * @param dataSource the NSense data base
     * @param callback the LocationPipeline Pipeline
     */
    public CleanTask(NSenseDataSource dataSource, LocationPipeline callback) {
        this.dataSource = dataSource;
    }
    /**
     * Thread core - It checks the last update in every location table entry.
     */
	@Override
	public void run() {
		Map<String, LocationEntry> entries  = dataSource.getAllLocationEntries();
		if (!entries.isEmpty()) {
			Log.i(TAG, "Checking entries in the database");
			for (LocationEntry entry : entries.values()) {
				Log.i(TAG, entry.getDeviceName() + " " + SystemClock.elapsedRealtime() + " - " + entry.getLastUpdate() + " > " + TIMEOUT);
				if ((SystemClock.elapsedRealtime() - entry.getLastUpdate()) > TIMEOUT) {
					// Probably the device is gone
					Log.i(TAG, "I will delete this entry from the database \n" + entry.toString());
					dataSource.removeLocationEntry(entry);
					Log.i(TAG, "Clear Task");
				}
			}
		} else {
			Log.i(TAG, "There is no entries in the database to check");
		}
	}
}