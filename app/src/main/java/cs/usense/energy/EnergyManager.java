/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class is used to monitor the battery level.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.energy;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class EnergyManager {

    /** This class is used to debug EnergyManager class */
    private static final String TAG = "EnergyManager";

    /** This variable is used to register the BatteryReceiver */
    private BatteryReceiver mBatteryReceiver = new BatteryReceiver();

    /** This variable is used to store the application context */
    private Context mContext;


    public EnergyManager(Context context) {
        Log.i(TAG, "EnergyManager constructor was invoked");
        mContext = context;
        start();
    }

    private void start() {
        Log.i(TAG, "BatteryReceiver registered");
        mContext.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void close() {
        Log.i(TAG, "BatteryReceiver unregistered");
        mContext.unregisterReceiver(mBatteryReceiver);
    }

}
