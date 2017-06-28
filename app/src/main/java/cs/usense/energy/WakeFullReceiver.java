/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class is used to instantiate an IntentService.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.energy;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class WakeFullReceiver extends WakefulBroadcastReceiver {

    /** This variable is used to debug WakeFullReceiver class */
    private static final String TAG = "WakeFullReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "WakeFullReceiver");
        startWakefulService(context, new Intent(context, NSenseIntentService.class));
    }
}
