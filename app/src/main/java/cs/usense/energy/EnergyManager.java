/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.energy;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


/**
 * This class is used to monitor the battery level.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public class EnergyManager {

    /** This class is used to debug EnergyManager class */
    private static final String TAG = "EnergyManager";

    /** This variable is used to register the BatteryReceiver */
    private BatteryReceiver mBatteryReceiver = new BatteryReceiver();

    /** This variable is used to store the application context */
    private Context mContext;

    /**
     * This method is the constructor of EnergyManager class
     * @param context application context
     */
    public EnergyManager(Context context) {
        Log.i(TAG, "EnergyManager constructor was invoked");
        mContext = context;
        start();
    }

    /**
     * This method register the battery broadcast receiver
     */
    private void start() {
        Log.i(TAG, "BatteryReceiver registered");
        mContext.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    /**
     * This method unregister the battery broadcast receiver
     */
    public void close() {
        Log.i(TAG, "BatteryReceiver unregistered");
        mContext.unregisterReceiver(mBatteryReceiver);
    }

}
