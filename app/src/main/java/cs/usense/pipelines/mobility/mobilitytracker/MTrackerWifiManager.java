/**
 *  Copyright (C) 2013 ULHT
 *  Author(s): jonnahtan.saltarin@ulusofona.pt
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by  the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 * ULOOP Mobility tracking plugin: Mtracker
 *
 * Mtracker is an Android app that collects information concerning visited APs
 * It computes a probingFunctionsManager and then estimates a potential handover - time and target AP
 * v1.0 - pre-prototype, D3.3, July 2012
 * v2.0 - prototype on September 2012 - D3.6
 * v3.0 - prototype on June 2013
 *
 * @author Jonnahtan Saltarin
 * @author Rute Sofia
 * @author Christian da Silva Pereira
 * @author Luis Amaral Lopes
 *
 * @version 3.0
 *
 * @file Contains MTrackerWifiManager. This class provides some methods to provide extended
 * functionality to the android WifiManager.
 *
 */
package cs.usense.pipelines.mobility.mobilitytracker;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.List;


import cs.usense.pipelines.mobility.interfaces.WifiChangeListener;

import static android.os.Looper.getMainLooper;

/**
 * This class provides some methods to provide extended
 * functionality to the android WifiManager.
 *
 * @author Jonnahtan Saltarin (ULHT)
 * @author Rute Sofia (ULHT)
 * @author Christian da Silva Pereira (ULHT)
 * @author Luis Amaral Lopes (ULHT)
 *
 * @version 3.0
 *
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MTrackerWifiManager {
	private static final String TAG = MTrackerWifiManager.class.getSimpleName();

	public static long MINIMUM_CONNEXION_TIME = 10;
	public static int SCANNING_INTERVAL = 10000;
	
	public boolean isScanningActive = false;
	public boolean isWaitingScanResults = false;
	
	private WifiManager androidWifiManager;
	
	private WifiInfo wifiCurrentAP;
	private long wifiCurrentVisitId;
	protected long wifiCurrentAPStart;
	
	private WifiStateChange wifiStateReceiver;
	private WifiConnectionChange wifiConnReceiver;
	private WifiAvailableNetworksChange wifiScanReceiver;
	private WifiChangeListener listener;



	private Handler mHandler = new Handler();
	private Context mContext;
	private Runnable runScan = new Runnable() {
		public void run() {
			if (isScanningActive) {
				if (!isWaitingScanResults && isWifiEnabled()) {
					//Log.d(TAG,"Start scam");
					if (startScan()) {
						//Log.d(TAG,"Scan is initiated");
						isWaitingScanResults = true;
					}
					else {
						//Log.d(TAG,"Start is no initiated");
						//notifyPredictedMoveChange("Scanning rejected");
					}
					
					mHandler.postDelayed(runScan, SCANNING_INTERVAL);
				}
				else if (isWaitingScanResults) {
					//notifyPredictedMoveChange("Still Waiting");
	        		mHandler.postDelayed(runScan, SCANNING_INTERVAL);
	        		isWaitingScanResults = false;
				}
				else {
					// WifiManager not active
				}
			}
			else {
				//notifyPredictedMoveChange("Scanning Stoped");
			}
		}
		
	};
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	MTrackerWifiManager (Context c) {
		androidWifiManager = (WifiManager)c.getSystemService(Context.WIFI_SERVICE);
		wifiStateReceiver = new WifiStateChange();
    	wifiConnReceiver = new WifiConnectionChange();
    	wifiScanReceiver = new WifiAvailableNetworksChange();

		c.registerReceiver(wifiStateReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    	c.registerReceiver(wifiConnReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    	c.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		Log.d(TAG, "WifiManager starts");

		mContext=c;
	}

	public void connectToAP(String networkSSID){

		List<WifiConfiguration> list = androidWifiManager.getConfiguredNetworks();
		for( WifiConfiguration i : list ) {
			Log.d(TAG,"WifiList:Wifi " + i.SSID +" "+ i.priority +" "+ i.status);
			if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
				androidWifiManager.disconnect();
				androidWifiManager.enableNetwork(i.networkId, true);
				androidWifiManager.reconnect();
				Log.d(TAG,"Reconnection to " + networkSSID);
				break;
			}
		}

	}

	public void connectToNewAP(String ssid){
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "\"" + ssid + "\"";
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		androidWifiManager.addNetwork(conf);
		connectToAP(ssid);
	}

	public void close (Context c) {
		this.stopPeriodicScanning();
		c.unregisterReceiver(wifiStateReceiver);
		c.unregisterReceiver(wifiScanReceiver);
		c.unregisterReceiver(wifiConnReceiver);
		
		if (wifiCurrentAP != null) {
			long wifiCurrentAPEnd = System.currentTimeMillis();
			long connectionTime = wifiCurrentAPEnd - wifiCurrentAPStart;
			if (connectionTime > MTrackerWifiManager.MINIMUM_CONNEXION_TIME) {
				listener.onWifiConnectionDown(true, wifiCurrentAP.getBSSID(), wifiCurrentAP.getSSID(), wifiCurrentVisitId, wifiCurrentAPStart, wifiCurrentAPEnd);
			}
			else {
				listener.onWifiConnectionDown(false, null, null, -1, -1, -1);
			}
			
			wifiCurrentAP = null;
			wifiCurrentAPStart = 0;
		}
	}
	
	public void startPeriodicScanning () {
		isScanningActive = true;
		mHandler.removeCallbacks(runScan);
		mHandler.post(runScan);
	}
	
	public void stopPeriodicScanning () {
		isScanningActive = false;
		mHandler.removeCallbacks(runScan);
	}
	
    public void setOnWifiChangeListener (WifiChangeListener listener) {
        this.listener = listener;
    }
    
    public void clearOnWifiChangeListener () {
        this.listener = null;
    }
  
	public void setWifiManager (WifiManager wm) {
		this.androidWifiManager = wm;
	}
	
	public void setWifiManager (Context c) {
		this.androidWifiManager = (WifiManager)c.getSystemService(Context.WIFI_SERVICE);
	}
	
	public boolean isWifiEnabled () {
		return androidWifiManager.isWifiEnabled();
	}
	
	public boolean startScan () {
		return androidWifiManager.startScan();
	}
	
	public List<ScanResult> getLastScanResults () {
		return androidWifiManager.getScanResults();
	}
	
	public int getGatewayIp () {
		return androidWifiManager.getDhcpInfo().gateway;
	}
	
	public void noteOngoingConnection () {
		if (wifiCurrentAP == null && androidWifiManager != null) {
			wifiCurrentAP = androidWifiManager.getConnectionInfo();
			if (wifiCurrentAP != null) {
				if (wifiCurrentAP.getBSSID() != null && wifiCurrentAP.getSSID() != null) {
					System.out.println(wifiCurrentAP.getSSID());
					wifiCurrentAPStart = System.currentTimeMillis();
					wifiCurrentVisitId = listener.onWifiConnectionUp(wifiCurrentAP.getBSSID(), wifiCurrentAP.getSSID(), getLastScanResults());
				}
			}
		}
	}


	public int connectionQuality(){
		WifiInfo wifiInfo= androidWifiManager.getConnectionInfo();
		int numberOfLevels = 5;
		return androidWifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
	}

	public String ipCalculation (){
		android.net.wifi.WifiInfo wifiInfo = androidWifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
	}


	class WifiStateChange extends BroadcastReceiver {
        public void onReceive(Context c, Intent i) {
        	int newState = i.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

        	switch (newState) {
        		case WifiManager.WIFI_STATE_DISABLED:
    				if (wifiCurrentAP != null) {
    					long wifiCurrentAPEnd = System.currentTimeMillis();
    					if ((wifiCurrentAPEnd - wifiCurrentAPStart) > MTrackerWifiManager.MINIMUM_CONNEXION_TIME) {
    						listener.onWifiStateDisabled(true, wifiCurrentAP.getBSSID(), wifiCurrentAP.getSSID(), wifiCurrentVisitId, wifiCurrentAPStart, wifiCurrentAPEnd);
    					}
    					else {
    						listener.onWifiStateDisabled(false, null, null, -1, -1, -1);
    					}
    					
    					wifiCurrentAP = null;
    					wifiCurrentAPStart = 0;
    				}
    				stopPeriodicScanning();
    				break;
        		case WifiManager.WIFI_STATE_ENABLED:
        			listener.onWifiStateEnabled();
        			startPeriodicScanning();
        			break;
        		default:
        			break;
        	}
            
        }      
    }
    
    class WifiConnectionChange extends BroadcastReceiver {
		private final String TAG = WifiConnectionChange.class.getSimpleName();
		private String previosState="";
		private String networkSSID="";
		private String netwotkBSSID="";
        public void onReceive(Context c, Intent i) {
			WifiManager wifiMgr = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        	NetworkInfo netInf = (NetworkInfo) i.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        	if (netInf != null) {

        		NetworkInfo.DetailedState netState = netInf.getDetailedState();
				switch (netState) {

					case OBTAINING_IPADDR:
						Log.d("WifiConnectionChange", "Obtaining ip");
						previosState= NetworkInfo.DetailedState.OBTAINING_IPADDR.toString();
						networkSSID=wifiInfo.getSSID();
						netwotkBSSID=wifiInfo.getBSSID();
						break;
        			case DISCONNECTED:
        				Log.d("WifiConnectionChange", "Disconected");
        				if (wifiCurrentAP != null) {
							Log.d("WifiConnectionChange", "Disconected"+ wifiCurrentAP.getSSID());
        					long wifiCurrentAPEnd = System.currentTimeMillis();
            				long connectionTime = wifiCurrentAPEnd - wifiCurrentAPStart;
        					if (connectionTime > MTrackerWifiManager.MINIMUM_CONNEXION_TIME) {
        						listener.onWifiConnectionDown(true, wifiCurrentAP.getBSSID(), wifiCurrentAP.getSSID(), wifiCurrentVisitId, wifiCurrentAPStart, wifiCurrentAPEnd);
        					}
        					else {
        						listener.onWifiConnectionDown(false, null, null, -1, -1, -1);
        					}
        					
        					wifiCurrentAP = null;
        					wifiCurrentAPStart = 0;
        				}

							if (previosState.equals(NetworkInfo.DetailedState.OBTAINING_IPADDR.toString())) {
								Log.d("wifiSupplicantSate", "REJECTED from " + networkSSID);

								listener.onConnectionRejected(netwotkBSSID,networkSSID);
								networkSSID="";
								previosState = "";
								netwotkBSSID="";
							}


        				stopPeriodicScanning();
        				break;
        			case CONNECTED:
						previosState = "";
						Log.d(TAG, "Connected");
        				if (wifiCurrentAP == null) {
        					wifiCurrentAPStart = System.currentTimeMillis();
        					wifiCurrentAP = ((WifiManager)c.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        					wifiCurrentVisitId = listener.onWifiConnectionUp(wifiCurrentAP.getBSSID(), wifiCurrentAP.getSSID(), getLastScanResults());
        					startPeriodicScanning();
        				}
        				break;
        			default:
        				break;
        		}
        	}
        }      
    }


    
    class WifiAvailableNetworksChange extends BroadcastReceiver {
		private final String TAG = WifiAvailableNetworksChange.class.getSimpleName();
        public void onReceive(Context c, Intent intent) {
			Log.d(TAG, "WifiAvailabeNetworkChange");
        	isWaitingScanResults = false;
            if (wifiCurrentAP != null) {
            	listener.onWifiAvailableNetworksChange(wifiCurrentAP.getSSID(), getLastScanResults());
            	Log.d(TAG, wifiCurrentAP.getSSID());

            }
            else {
            	listener.onWifiAvailableList(getLastScanResults());
				Log.d(TAG, "Scan without connection");
            	// notifyPredictedMoveChange("Not Connected");
            	//PREVIOUS INFO
				//no active connection yet
				//builds initial list of APs, based on scan
				//triggers start of time counting
				//later should be triggered by beacon
            }
        }      
    }
}
