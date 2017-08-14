/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/16.
 * Class is part of the NSense application. It provides support for location pipeline.
 */

package cs.usense.pipelines.location;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import cs.usense.db.NSenseDataSource;


/**
 * It manages the Wi-Fi and also the Wi-Fi P2P to compute
 * the relative distance to others devices.
 * @author Luis Amaral Lopes (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
class RelativePositionWiFiNoConnection implements Runnable {

    /** Used by WiFi P2P to detect only NSense devices */
    static final String SERVICE_TYPE = "_location._tcp";

    /** This TAG is used to debug RelativePositionWiFiNoConnection class */
	private static final String TAG = "WiFiP2PNoConnection";

    /** List with all nsense devices found by the WiFi P2P */
    ArrayList<NSenseDevice> listNSenseDevices = new ArrayList<>();

	/** It uses the WiFi P2P to search for nsense devices and to get their info */
    private WifiServiceSearcher mWifiServiceSearcher;

    /** It uses the WiFi to scan for available APs, based on the info computed by the WifiServiceSearcher*/
    private WifiAccessPoint mWifiAccessPoint;

    private Handler mHandlerDisc = new Handler();

    private int mDiscState = 0;

    /**
     * RelativePositionWiFiNoConnection constructor - It initializes the WifiServiceSearcher and WifiAccessPoint modules
     * and creates a handler to switch between this two modules.
     * @param context - Interface to global information about an application environment.
     */
    RelativePositionWiFiNoConnection(Context context, NSenseDataSource dataSource) {
        Log.i(TAG, "Started");
        mWifiAccessPoint = new WifiAccessPoint(context, this, dataSource);
        mWifiServiceSearcher = new WifiServiceSearcher(context, this, dataSource);
        mWifiAccessPoint.start();
        mWifiServiceSearcher.start();
        mHandlerDisc.post(this);
    }

    @Override
    public void run() {
        Log.i(TAG, "mDiscState is " + mDiscState);
        if (mDiscState == 0) {
            mWifiServiceSearcher.startServiceDiscovery();
            mDiscState = 1;
            mHandlerDisc.postDelayed(this, ((new Random().nextInt(6 - 1) + 15) * 1000));
        } else if (mDiscState == 1) {
            mWifiAccessPoint.stopLocalServices();
            mWifiAccessPoint.createGroup();
            mDiscState = 2;
            mHandlerDisc.postDelayed(this, ((new Random().nextInt(6 - 1) + 7) * 1000));
        } else if (mDiscState == 2) {
            mWifiAccessPoint.removeGroup();
            mWifiServiceSearcher.stopDiscovery();
            mDiscState = new Random().nextInt(2);
            mHandlerDisc.postDelayed(this, 4000);
        }
    }

    /**
     * It stops WifiServiceSearcher and WifiAccessPoint modules
     */
	public void close() {
        if(mWifiAccessPoint != null){
            mWifiAccessPoint.stop();
            mWifiAccessPoint = null;
        }
        if(mWifiServiceSearcher != null){
            mWifiServiceSearcher.stop();
            mWifiServiceSearcher = null;
        }
        mHandlerDisc.removeCallbacks(this);
    }
}