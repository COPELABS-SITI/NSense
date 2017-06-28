/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class extends a IntentService to wake up the device
 * from sleep state.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.energy;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import cs.usense.services.NSenseService;


public class NSenseIntentService extends IntentService {

    /** This variable is used to debug NSenseIntentService class */
    private static final String TAG = "NSenseIntentService";

    /**
     * This method is the constructor of the NSenseIntentService class.
     */
    public NSenseIntentService() {
        super("NSenseIntentService");
        Log.i(TAG, "The NSenseIntentService constructor was invoked");
    }

    /**
     * This method is called when intent service starts.
     * @param intent intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent was invoked");
        NSenseService.getService().doWhenDeviceIsAwake();
    }
}
