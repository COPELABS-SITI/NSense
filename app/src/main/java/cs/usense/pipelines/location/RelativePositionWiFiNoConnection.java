/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * It manages the Wi-Fi and also the Wi-Fi P2P to compute 
 * the relative distance to others devices.
 * @author Luis Amaral Lopes (COPELABS/ULHT)
 */

package cs.usense.pipelines.location;

/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import cs.usense.db.NSenseDataSource;

class RelativePositionWiFiNoConnection {

    /** Used by WiFi P2P to detect only NSense devices */
    static final String SERVICE_TYPE = "_location._tcp";

    /** This TAG is used to debug RelativePositionWiFiNoConnection class */
	private static final String TAG = "WiFiP2PNoConnection";

	/** It uses the WiFi P2P to search for nsense devices and to get their info */
    private WifiServiceSearcher mWifiServiceSearcher = null;

    /** It uses the WiFi to scan for available APs, based on the info computed by the WifiServiceSearcher*/
    private WifiAccessPoint mWifiAccessPoint = null;

    /** List with all nsense devices found by the WiFi P2P */
    ArrayList<NSenseDevice> listNSenseDevices = new ArrayList<>();

    private Handler mHandlerDisc = new Handler();
    private int mDiscState = 0;

    private Runnable mThread = new Runnable() {

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
				mDiscState = 0;
                mHandlerDisc.postDelayed(this, 1000);
			}
		}
    };

    /**
     * RelativePositionWiFiNoConnection constructor - It initializes the WifiServiceSearcher and WifiAccessPoint modules
     * and creates a handler to switch between this two modules.
     * @param context - Interface to global information about an application environment.
     * @param dataSource - NSense data base.
     */
    RelativePositionWiFiNoConnection(Context context, NSenseDataSource dataSource) {
        Log.i(TAG, "Started");
        mWifiAccessPoint = new WifiAccessPoint(context, this, dataSource);
        mWifiServiceSearcher = new WifiServiceSearcher(context, this, dataSource);
        mWifiAccessPoint.start();
        mWifiServiceSearcher.start();
        mHandlerDisc.post(mThread);
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
        mHandlerDisc.removeCallbacks(mThread);
    }
}