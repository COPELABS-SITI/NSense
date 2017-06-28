/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class is the entry point of FusionLocation Pipeline.
 * When there are nsense devices near (provided by WifiServiceSearcher) it performs a
 * WiFi scan to detect this devices a to compute the respective relative distance.
 * @author Luis Amaral Lopes (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.pipelines.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.usense.activities.MainActivity;
import cs.usense.db.NSenseDataSource;
import cs.usense.pipelines.proximity.BTManager;
import cs.usense.preferences.InterestsPreferences;
import cs.usense.utilities.DateUtils;
import cs.usense.utilities.Utils;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;

class WifiAccessPoint implements WifiP2pManager.ConnectionInfoListener,WifiP2pManager.GroupInfoListener{

    private String TAG = "WifiAccessPoint";
    
    /** WifiAccessPoint Module */
    private WifiAccessPoint that = this;
    /** Interface to global information about an application environment. */
    private Context mContext;
    /** NSense Data base */
    private NSenseDataSource mDataSource;
    /** Android WiFi P2P Manager */
    private WifiP2pManager p2p;
    /** A channel that connects the application to the Wifi p2p framework. */
    private WifiP2pManager.Channel channel;
    /** A class for storing Bonjour service information that is advertised over a Wi-Fi peer-to-peer setup. */
    private WifiP2pDnsSdServiceInfo service;
    /** Base class for code that will receive intents sent by sendBroadcast(). */
    private BroadcastReceiver receiver;
    /** RelativePositionWiFiNoConnection module */
    private RelativePositionWiFiNoConnection mCallBack;
    /** Flag about if it is necessary to do a WiFi scan */
    private Boolean toScan = false;
    /** This class provides the primary API for managing all aspects of Wi-Fi connectivity.*/
    private WifiManager wifiManager = null;
    /** BroadcastReceiver that receives a WiFi scan result. */
    private WifiScanReceiverLocation wifiReceiver = null;

    /**
     * WifiAccessPoint constructor
     * @param context -Interface to global information about an application environment.
     * @param callback - RelativePositionWiFiNoConnection module.
     * @param dataSource - NSense data base.
     */
    WifiAccessPoint(Context context, RelativePositionWiFiNoConnection callback, NSenseDataSource dataSource) {
        mContext = context;
        mCallBack = callback;
        mDataSource = dataSource;
    }

    /**
     * Requests a WiFi scan
     */
    private void doScan() {
    	wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (wifiReceiver != null)
			mContext.unregisterReceiver(wifiReceiver);
		wifiReceiver = new WifiScanReceiverLocation();
		mContext.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		Log.i(TAG, "start Scan");
		wifiManager.startScan();
    }

    /**
     * It initializes the WiFi P2P and register a receiver to receive all 
     * info about the WiFi state and connection state.
     */
    public void start() {
        p2p = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        if (p2p == null) {
            Log.i(TAG,"This device does not support Wi-Fi Direct");
        } else {
            channel = p2p.initialize(mContext, mContext.getMainLooper(), null);
            p2p.clearLocalServices(channel, null);
            p2p.clearServiceRequests(channel, null);
            p2p.removeGroup(channel, null);

            receiver = new AccessPointReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WIFI_P2P_STATE_CHANGED_ACTION);
            filter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mContext.registerReceiver(receiver, filter);
        }
    }
    
    /**
     * Create a Group Owner. It creates a Access point in this device.
     */
    public void createGroup() {
    	p2p.createGroup(channel,new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.i(TAG,"Creating Local Group ");
            }

            public void onFailure(int reason) {
                Log.i(TAG,"Local Group failed, error code " + reason);
                p2p.removeGroup(channel, null);
            }
        });
    }

    /**
     * It cleans the local services registered in WiFi p2p and remove the receiver.
     */
    public void stop() {
        mContext.unregisterReceiver(receiver);
        stopLocalServices();
        removeGroup();
    }

    /**
     * Remove the group created. It stops the access point created in this device.
     */
    public void removeGroup() {
    	toScan = true;
        p2p.removeGroup(channel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.i(TAG,"Cleared Local Group ");
            }

            public void onFailure(int reason) {
                Log.i(TAG,"Clearing Local Group failed, error code " + reason);
            }
        });
    }

    /**
     * Provides information about the Group Owner created (Access Point)
     * This information include the SSID and the MAC address.
     */
    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
        try {
            startLocalService(group.getNetworkName(), BTManager.getBTMACAddress(mContext),
                    InterestsPreferences.readInterestsFromCacheAsString(mContext));
        } catch (NullPointerException e) {
            Log.e(TAG, "onGroupInfoAvailable NullPointerException");
            e.printStackTrace();
        }
    }

    /**
     * Register a local service with the name of the SSID and the 
     * MAC address of the group owner created
     * @param instance - ID that identifies the application
     */
    private void startLocalService(String instance, String btMacAddress, String interests) {
        Map<String, String> record = new HashMap<>();
        if(btMacAddress == null || btMacAddress.isEmpty())
            btMacAddress = "null";
        if(interests == null || interests.isEmpty())
            interests = "null";
        Log.i(TAG, "Interests announcing: " + interests);
        record.put(LocationPipeline.BT_MAC_INFO, btMacAddress);
        record.put(LocationPipeline.INTERESTS_INFO, interests);
        service = WifiP2pDnsSdServiceInfo.newInstance(instance, RelativePositionWiFiNoConnection.SERVICE_TYPE, record);

        p2p.clearLocalServices(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.i(TAG, "Service announced with success");
                p2p.addLocalService(channel, service, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.i(TAG,"Added Local Service");
                    }

                    @Override
                    public void onFailure(int error) {
                        Log.i(TAG,"Failed to add a service " + error);
                    }
                });
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "Service was not announced with success. Error "  + reason);
            }
        });
    }

    /**
     * Removes all local services created.
     */
    void stopLocalServices() {
        p2p.clearLocalServices(channel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.i(TAG,"Cleared local services");
            }

            public void onFailure(int reason) {
                Log.i(TAG,"Clearing local services failed, error code " + reason);
            }
        });
    }

    /**
     * Request group information
     */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        try {
            if (info.isGroupOwner) {
                p2p.requestGroupInfo(channel,this);
            } else {
                Log.i(TAG,"we are client !! group owner address is: " + info.groupOwnerAddress.getHostAddress());
            }
        } catch(Exception e) {
            Log.i(TAG,"onConnectionInfoAvailable, error: " + e.toString());
        }
    }
    
    /**
     * BroadcastReceiver that receives events about WiFi state and WiFi connection state.
     * @author Luis Amaral Lopes (COPELABS/ULHT)
     */
    private class AccessPointReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                } else {
                	stopLocalServices();
                    removeGroup();
                }
            } else if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    Log.i(TAG,"We are connected, will check info now");
                    p2p.requestConnectionInfo(channel, that);
                } else {
                    Log.i(TAG,"We are DIS-connected");
                    if (toScan) {
                    	if (!mCallBack.listNSenseDevices.isEmpty())
                    		doScan();
                    }
                }
            }
        }
    }
       
    /**
     * BroadcastReceiver that receives a WiFi scan result.
     * @author Luis Amaral Lopes (COPELABS/ULHT)
     */
    private class WifiScanReceiverLocation extends BroadcastReceiver {

		@Override
		public void onReceive(Context c, Intent intent) {
			if (!toScan)
				return;
			List<ScanResult> wifiScanList = wifiManager.getScanResults();
			boolean mSomethingFound = false;
            Log.i(TAG, "Need to find");
			for (NSenseDevice nSenseDevice : mCallBack.listNSenseDevices) {
				Log.i(TAG, nSenseDevice.toString());
			}
            Log.i(TAG, "SCAN RESULTS");
			if (!mCallBack.listNSenseDevices.isEmpty()) {
				ArrayList<Integer> devicesLost = new ArrayList<>();
				for (int j = 0; j < mCallBack.listNSenseDevices.size(); j++) {
					NSenseDevice mDevice = mCallBack.listNSenseDevices.get(j);
				    boolean mFound = false;
					for (int i = 0; i < wifiScanList.size(); i++) {
						Log.w(TAG, "SCAN RESULT: " + wifiScanList.get(i).SSID + " " + wifiScanList.get(i).BSSID);
                        if (!wifiScanList.get(i).SSID.equalsIgnoreCase(mDevice.getSSID())) {
                            if (!wifiScanList.get(i).BSSID.equalsIgnoreCase(mDevice.getWifiAPMACAddress())) {
                                if(mDevice.getDeviceName() != null) {
                                    if (!wifiScanList.get(i).SSID.contains(mDevice.getDeviceName())) {
                                        if (checkWiFiP2PMacs(wifiScanList.get(i).BSSID, mDevice.getWifiAPMACAddress())) {
                                            continue;
                                        } else {
                                            Log.i(TAG, "FOUND AP with partial MAC");
                                            Log.e(TAG, "MAC SCAN: " + wifiScanList.get(i).BSSID + " MAC: " + mDevice.getWifiAPMACAddress());
                                        }
                                    } else {
                                        Log.e(TAG, "FOUND AP containing the DEVICE NAME in SSID");
                                        Log.e(TAG, "SSID: " + wifiScanList.get(i).SSID + " Device Name: " + mDevice.getDeviceName());
                                    }
                                }
                            } else {
                                Log.i(TAG, "FOUND AP with the same MAC ADDRESS");
                                if(mDevice.getDeviceName().isEmpty()) {
                                    mDevice.setDeviceName(mDevice.getSSID().split("-")[2]);
                                }
                            }
                        } else {
                            Log.i(TAG, "FOUND AP with the same SSID");
                        }
		            		
		            	/** AP Found */
		            	mSomethingFound = true;
		            	mFound = true;
		            	mDevice.setCountNotFound(0);
		            	mDevice.setWifiAPMACAddress(wifiScanList.get(i).BSSID);
		            	mCallBack.listNSenseDevices.set(j, mDevice);
		            	/** Get the SSID */
		                String ssid = wifiScanList.get(i).SSID; 
		                Double mDistance = calculateDistance(wifiScanList.get(i).level, wifiScanList.get(i).frequency);

                        Utils.appendLogs("WIFIDistance",
                                new String[] {
                                        DateUtils.getTimeNowAsStringSecond(),
                                        String.valueOf(wifiScanList.get(i).level),
                                        String.valueOf(wifiScanList.get(i).frequency),
                                        String.valueOf(mDistance),
                                        mDevice.getDeviceName()
                        });
                        if (mDistance > 100)
                            mDistance = (double) 100;
		                Log.d(TAG,"Distance to " + ssid + ": " + mDistance.toString() + " m.");
                        saveDistance(mDistance, mDevice);
		                break;
	            	}
					
					if (!mFound) {
						if (mDevice.getCountNotFound() < 3) {
							mDevice.incrementCountNotFound();
							Log.w(TAG, "Trying " + mDevice.getCountNotFound() + "/3");
						} else {
							devicesLost.add(j);
							mDevice.setCountNotFound(0);
						}
					}	
	            }
				
				if (!devicesLost.isEmpty()) {
					mSomethingFound = true;
					Log.w(TAG, "Nothing Found - Try Later");
				}
				
				/* remove devices that by 3 times never appear in Wifi scan results */
				int index = 0;
				for (int device : devicesLost) {
					index++;
					Log.w(TAG, "Nothing Found - Try Later");
					/* when a device is removed and no others devices were found, then no scan should be done, instead the AP should be created.*/
					mSomethingFound = true;
				}					
			}

            mContext.unregisterReceiver(wifiReceiver);
            wifiReceiver = null;

            if (!mSomethingFound) {
            	final Handler handler = new Handler();
            	handler.postDelayed(new Runnable() {
            		@Override
            		public void run() {
            			Log.w(TAG, "Repeating SCAN");
            			doScan();
                    }
            	}, 2000);
            } else {
            	Log.i(TAG, "Running thread again");
                toScan = false;
            }
		}
    }

    private boolean checkWiFiP2PMacs(String scanMac, String mac) {
        int checkedParts = 0;
        if(mac != null) {
            String[] splittedScanMac = scanMac.split(":");
            String[] splittedMac = mac.split(":");
            for (int i = 0; i < splittedScanMac.length; i++) {
                if (splittedScanMac[i].equalsIgnoreCase(splittedMac[i])) {
                    checkedParts++;
                }
            }
        }
        return checkedParts <= 3;
    }
    
    /**
     * Computes the distance to a device.
     * @param signalLevelInDb -  The detected signal level in dBm, also known as the RSSI.
     * @param freqInMHz - The frequency in MHz of the channel over which the client is communicating with the access point.
     * @return distance in meters.
     */
    private double calculateDistance(double signalLevelInDb, double freqInMHz) {
       return Math.pow(10, ((Math.abs(signalLevelInDb) - 20 * Math.log10(freqInMHz) + 25))/22);
	}
    
    /**
     * Saves the computed distance to a specific device to the database.
     * @param mDistance -  Computed distance to the device
     * @param mDevice - Device found
     */
    private void saveDistance(double mDistance, NSenseDevice mDevice) {
    	String mDeviceName = mDevice.getDeviceName();
    	String mWiFiDirectMAC = mDevice.getWifiDirectMACAddress();
        String mBTMACAddress = mDevice.getBtMACAddress();
		Log.i(TAG, "Device " + mDeviceName + " Distance computed: " + mDistance);
		if(mDataSource.updateDistanceExpired(mWiFiDirectMAC, mDeviceName)) {
            /** Save Distance into DB */
            if (mDataSource.hasLocationEntry(mWiFiDirectMAC, mDeviceName)) {
                /** Device exists */
                LocationEntry entry = mDataSource.getLocationEntry(mWiFiDirectMAC, mDeviceName);
                if (entry.getDistance() == -1) {
                    entry.setDistance(mDistance);
                } else {
                    entry.setDistance(entry.getDistance() * 0.6 + mDistance * 0.4);
                }
                if (entry.getDeviceName() != null) {
                    if (entry.getDeviceName().isEmpty()) {
                        entry.setDeviceName(mDeviceName);
                    }
                }
                if (entry.getBTMACAddress() != null) {
                    if (entry.getBTMACAddress().isEmpty()) {
                        entry.setBTMACAddress(mBTMACAddress);
                    }
                }
                entry.setLastUpdate(SystemClock.elapsedRealtime());
                mDataSource.updateLocationEntry(entry);
            } else {
                /** New Device */
                mDataSource.registerLocationEntry(new LocationEntry(mDeviceName, mWiFiDirectMAC, mDistance, mBTMACAddress));
            }
        }
	}
}