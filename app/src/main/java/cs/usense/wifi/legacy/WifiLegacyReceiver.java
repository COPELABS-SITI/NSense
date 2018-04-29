/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/7.
 * Class is part of the NSense application.
 */

package cs.usense.wifi.legacy;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.util.Log;

import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;

/**
 * This class is responsible for manage the scans and notify the listeners
 * with the scan results.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class WifiLegacyReceiver extends BroadcastReceiver implements Runnable {

    /** This variable is used to debug WifiLegacyReceiver class */
    private static final String TAG = "WifiLegacyReceiver";

    /** This variable represents how many time exists between the wifi scans */
    private static final int TIME_BETWEEN_SCANS = 2000;

    /** This variable represents the number of scans to be done */
    private static final int NUMBER_OF_SCANS = 3;

    /** This object is used to do wifi scans */
    private WifiManager mWifiManager;

    /** This variable stores how many scans was actually done */
    private int mScansConcluded = 0;

    /** This object is used to schedule the next scan */
    private Handler mHandler;


    public WifiLegacyReceiver(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mHandler = new Handler();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (!networkInfo.isConnected()) {
                Log.i(TAG,"We are DIS-connected, let's do a wifi scan");
                mScansConcluded = 0;
                run();
            }
        } else if (SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            WifiLegacyListenerManager.notifyScanResultsAvailable(mWifiManager.getScanResults());
        }
    }

    @Override
    public void run() {
        if(NUMBER_OF_SCANS > mScansConcluded) {
            mWifiManager.startScan();
            mScansConcluded++;
            mHandler.postDelayed(this, TIME_BETWEEN_SCANS);
        }
    }
}
