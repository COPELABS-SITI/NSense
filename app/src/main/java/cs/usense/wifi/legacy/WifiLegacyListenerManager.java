/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/11.
 * Class is part of the NSense application.
 */

package cs.usense.wifi.legacy;


import android.net.wifi.ScanResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible to manage the legacy wifi listeners.
 * Register, unregister and notify them.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public abstract class WifiLegacyListenerManager {

    /** This variable is used to debug WifiLegacyListenerManager class */
    private static final String TAG = "WifiLegacyListenerMngr";

    /** This list contains all registered listeners */
    private static List<WifiLegacyListener> listeners = new ArrayList<>();

    /**
     * This method is used to register a listener
     * @param wifiLegacyListener listener to be registered
     */
    public static void registerListener(WifiLegacyListener wifiLegacyListener) {
        Log.i(TAG, "Registering a listener");
        listeners.add(wifiLegacyListener);
    }

    /**
     * This method is used to unregister a listener
     * @param wifiLegacyListener listener to be unregistered
     */
    public static void unregisterListener(WifiLegacyListener wifiLegacyListener) {
        Log.i(TAG, "Unregistering a listener");
        listeners.remove(wifiLegacyListener);
    }

    /**
     * This method is used to notify all ScanResultsAvailable listeners
     */
    static void notifyScanResultsAvailable(List<ScanResult> scanResults) {
        Log.i(TAG, "Notifying ScanResultsAvailable listeners");
        for(WifiLegacyListener listener : listeners) {
            if(listener instanceof WifiLegacyListener.ScanResultsAvailable) {
                ((WifiLegacyListener.ScanResultsAvailable) listener).onScanResultsAvailable(scanResults);
            }
        }
    }

}
