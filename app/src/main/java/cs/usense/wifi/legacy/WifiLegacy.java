/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/7.
 * Class is part of the NSense application.
 */

package cs.usense.wifi.legacy;


import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;

/**
 * This class is used to instantiate legacy wifi features
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class WifiLegacy {

    /** This variable is used to debug WifiLegacy class */
    private static final String TAG = "WifiLegacy";

    /** This object is used to receive the broadcasts related with the legacy wifi */
    private WifiLegacyReceiver mWifiLegacyReceiver;

    /** This object references the application context */
    private Context mContext;

    public WifiLegacy(Context context) {
        mContext = context;
        mWifiLegacyReceiver = new WifiLegacyReceiver(context);
    }

    /**
     * This method starts the legacy wifi features
     */
    public void start() {
        mContext.registerReceiver(mWifiLegacyReceiver, buildIntent());
        Log.i(TAG, "WifiLegacyReceiver registered successfully");
    }

    /**
     * This method stops the legacy wifi features
     */
    public void stop() {
        mContext.unregisterReceiver(mWifiLegacyReceiver);
        Log.i(TAG, "WifiLegacyReceiver unregistered successfully");
    }

    /**
     * This method builds an IntentFilter which will be used to register the
     * legacy wifi broadcast receiver
     * @return intent filter
     */
    private IntentFilter buildIntent() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(SCAN_RESULTS_AVAILABLE_ACTION);
        return intentFilter;
    }

}
