/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the USense application.
 * It manages the Wi-Fi and also the Wi-Fi P2P to compute 
 * the relative distance to others devices.
 * @author Luis Amaral Lopes (COPELABS/ULHT)
 */
package cs.usense.location;
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

import java.util.ArrayList;
import java.util.Random;

import cs.usense.db.UsenseDataSource;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class RelativePositionWiFiNoConnection {
	
	private String TAG = "WiFiDirectBroadcastReceiverNoConnection";
	
	/** Used by WiFi P2P to detect only Usense devices */
	public static final String SERVICE_TYPE = "_location._tcp";
	/** It uses the WiFi P2P to search for usense devices and to get their info */
    WifiServiceSearcher    mWifiServiceSearcher = null;
    /** It uses the WiFi to scan for available APs, based on the info computed by the WifiServiceSearcher*/
    WifiAccessPoint        mWifiAccessPoint = null;
   
    private Context context;
    private UsenseDataSource dataSource;
    private LocationPipeline callback;
    
    private Handler handlerDisc = new Handler();
    private int mDiscState = 0;
    private Random rand = new Random();
    private Runnable mThread = new Runnable(){

		@Override
		public void run() {
			if (mDiscState == 0) {
				mWifiServiceSearcher.startServiceDiscovery();
				mDiscState = 1;
				handlerDisc.postDelayed(this, ((rand.nextInt(20 - 19) + 19) * 1000));
			} else  if (mDiscState == 1) {
				mWifiAccessPoint.stopLocalServices();
				mWifiAccessPoint.createGroup();
				mDiscState = 2;
				handlerDisc.postDelayed(this, ((rand.nextInt(40 - 20) + 20) * 1000));
			} else if (mDiscState == 2) {
				mWifiAccessPoint.removeGroup();
				mWifiServiceSearcher.stopDiscovery();
				mDiscState = 0;
			}
		}
    	
    };
    
    /** List with all usense devices found by the WiFi P2P */
    ArrayList<UsenseDevice> listUsenseDevices = new ArrayList<UsenseDevice>();

    /**
     * Class that holds all the information about a device found by the WiFi P2P
     * @author Luis Amaral Lopes (COPELABS/ULHT)
     *
     */
    public class UsenseDevice {
    	/** MAC address received from the discover process (Wi-Fi Direct) */
    	public String mWiFiDirectMACAddress;
    	/** MAC address from the AP received from the wifi manager */
    	public String mWiFiAPMACAddress = "";
    	/** Access Point SSID */
    	public String mSSID;
    	/** Device Name */
    	public String mDeviceName;
    	/** Number of times this device was not found at the WiFi scans. */
    	public int mCountNotFound = 0;
    }

    /**
     * RelativePositionWiFiNoConnection constructor - It initializes the WifiServiceSearcher and WifiAccessPoint modules
     * and creates a handler to switch between this two modules.
     * @param context - Interface to global information about an application environment.
     * @param dataSource - Usense data base.
     * @param callback - LocationPipeline module
     */
    protected RelativePositionWiFiNoConnection(Context context, UsenseDataSource dataSource, LocationPipeline callback) {
        this.context = context;
        this.dataSource = dataSource;
        this.callback = callback;

        Log.i(TAG, "Started");

        mWifiAccessPoint = new WifiAccessPoint(this.context, this, this.dataSource);
        mWifiAccessPoint.Start();

        mWifiServiceSearcher = new WifiServiceSearcher(this.context, this, this.dataSource);
        mWifiServiceSearcher.Start();
        
        handlerDisc.post(mThread);
    }
    /**
     * RelativePositionWiFiNoConnection constructor. 
     */
    public RelativePositionWiFiNoConnection() {
		
	}

	/**
     * Notifies a database change to the listeners.
     */
    public void notifyDataBaseChange() {
    	callback.notifyDataBaseChange();
    }
    
    /**
     * It informs the mWifiServiceSearcher to restart the Wifi P2P.
     */
    public void restartDiscover() {
    	mWifiServiceSearcher.restartWifiP2P();
    }
    
    /**
     * Used to schedule another thread
     */
    public void scheduleThread() {
    	handlerDisc.post(mThread);
    }

    /**
     * It stops WifiServiceSearcher and WifiAccessPoint modules
     */
	public void close() {
        
        if(mWifiAccessPoint != null){
            mWifiAccessPoint.Stop();
            mWifiAccessPoint = null;
        }

        if(mWifiServiceSearcher != null){
            mWifiServiceSearcher.Stop();
            mWifiServiceSearcher = null;
        }
    }
}
