/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class is used to store in a log the battery level.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.energy;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import cs.usense.utilities.Utils;

public class BatteryReceiver extends BroadcastReceiver {

    /** This variable is used to debug BatteryReceiver class */
    private static final String TAG = "BatteryReceiver";

    /** Text file name of the battery logs */
    private static final String BATTERY_FILENAME = "Timer";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            Log.i(TAG, "onReceive of BatteryReceiver was invoked");
            int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;
            if (currentLevel >= 0 && scale > 0) {
                level = (currentLevel * 100) / scale;
            }
            Log.i(TAG, "The current battery level is " + level);
            Utils.appendLogs(BATTERY_FILENAME, String.valueOf(level));
        }
    }
}