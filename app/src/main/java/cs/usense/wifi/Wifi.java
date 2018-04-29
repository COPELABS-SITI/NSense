/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/7.
 * Class is part of the NSense application.
 */

package cs.usense.wifi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import cs.usense.R;
import cs.usense.exceptions.SensorNotFoundException;
import cs.usense.wifi.legacy.WifiLegacy;
import cs.usense.wifi.p2p.WifiP2p;

/**
 * This class is responsible for discover wifi p2p devices, services
 * and also text records which are being announced.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class Wifi {

    /** This variable is used to debug Wifi class */
    private static final String TAG = "Wifi";

    /** This object is used to instantiate the legacy wifi features */
    private WifiLegacy mWifiLegacy;

    /** This object is used to instantiate the wifi p2p features */
    private WifiP2p mWifiP2p;

    public Wifi(Context context) throws SensorNotFoundException {
        checkWifiFeatures(context);
        mWifiLegacy = new WifiLegacy(context);
        mWifiP2p = new WifiP2p(context);
        mWifiLegacy.start();
        mWifiP2p.start();
    }

    /**
     * This method checks if all wifi features requested are present on the device
     * @throws SensorNotFoundException this exception is triggered when some feature is missing
     */
    private void checkWifiFeatures(Context context) throws SensorNotFoundException {
        PackageManager pm = context.getPackageManager();
        if(!pm.hasSystemFeature(PackageManager.FEATURE_WIFI))
            throw new SensorNotFoundException(context.getString(R.string.sensor_not_found_message, context.getString(R.string.Wi_Fi)));
        if(!pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT))
            throw new SensorNotFoundException(context.getString(R.string.sensor_not_found_message, context.getString(R.string.Wi_Fi_Direct)));
    }

    /**
     * This method closes all features related with this class
     */
    public void close() {
        Log.i(TAG, "Closing Wifi");
        mWifiLegacy.stop();
        mWifiP2p.stop();
    }
}
