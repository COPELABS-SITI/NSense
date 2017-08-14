/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/21.
 * Class is part of the NSense application. It provides support for location pipeline.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.usense.db.NSenseDataSource;
import cs.usense.pipelines.proximity.BTManager;
import cs.usense.preferences.InterestsPreferences;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;

/**
 * When there are NSense devices near (provided by WifiServiceSearcher) it performs a
 * WiFi scan to detect this devices a to compute the respective relative distance.
 * @author Luis Amaral Lopes (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
class WifiAccessPoint implements WifiP2pManager.ConnectionInfoListener,WifiP2pManager.GroupInfoListener{

    /** This variable is used to debug WifiAccessPoint class */
    private static final String TAG = "WifiAccessPoint";

    /** This variable represents the WI-FI flag on DB */
    private static final int WIFI_UPDATE_FLAG = 1;

    /** A class for storing Bonjour mWifiP2pDnsSdServiceInfo information that is advertised over a Wi-Fi peer-to-peer setup. */
    private WifiP2pDnsSdServiceInfo mWifiP2pDnsSdServiceInfo;

    /** BroadcastReceiver that receives a WiFi scan result. */
    private WifiScanReceiverLocation mWifiScanReceiverLocation;

    /** RelativePositionWiFiNoConnection module */
    private RelativePositionWiFiNoConnection mCallBack;

    /** Base class for code that will receive intents sent by sendBroadcast(). */
    private BroadcastReceiver mBroadCastReceiver;

    /** A mChannel that connects the application to the Wifi mWifiP2pManager framework. */
    private WifiP2pManager.Channel mChannel;

    /** Android WiFi P2P Manager */
    private WifiP2pManager mWifiP2pManager;

    /** This class provides the primary API for managing all aspects of Wi-Fi connectivity.*/
    private WifiManager mWifiManager;

    /** NSense Data base */
    private NSenseDataSource mDataSource;

    /** Interface to global information about an application environment. */
    private Context mContext;

    /** This variable stores how many WI-FI scans the application does */
    private int mNumberOfScans = 2;


    /**
     * WifiAccessPoint constructor
     * @param context -Interface to global information about an application environment.
     * @param callback - RelativePositionWiFiNoConnection module.
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
    	mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (mWifiScanReceiverLocation == null) {
            mWifiScanReceiverLocation = new WifiScanReceiverLocation();
            mContext.registerReceiver(mWifiScanReceiverLocation, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }
		Log.i(TAG, "start Scan");
		mWifiManager.startScan();
    }

    /**
     * It initializes the WiFi P2P and register a mBroadCastReceiver to receive all
     * info about the WiFi state and connection state.
     */
    public void start() {
        mWifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        if (mWifiP2pManager == null) {
            Log.i(TAG,"This device does not support Wi-Fi Direct");
        } else {
            mChannel = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), null);
            mWifiP2pManager.clearLocalServices(mChannel, null);
            mWifiP2pManager.clearServiceRequests(mChannel, null);
            mWifiP2pManager.removeGroup(mChannel, null);

            mBroadCastReceiver = new AccessPointReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WIFI_P2P_STATE_CHANGED_ACTION);
            filter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mContext.registerReceiver(mBroadCastReceiver, filter);
        }
    }
    
    /**
     * Create a Group Owner. It creates a Access point in this device.
     */
    void createGroup() {
    	mWifiP2pManager.createGroup(mChannel,new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.i(TAG,"Creating Local Group ");
            }

            public void onFailure(int reason) {
                Log.i(TAG,"Local Group failed, error code " + reason);
                mWifiP2pManager.removeGroup(mChannel, null);
            }
        });
    }

    /**
     * It cleans the local services registered in WiFi mWifiP2pManager and remove the mBroadCastReceiver.
     */
    void stop() {
        mContext.unregisterReceiver(mBroadCastReceiver);
        if(mWifiScanReceiverLocation != null) {
            mContext.unregisterReceiver(mWifiScanReceiverLocation);
            mWifiScanReceiverLocation = null;
        }
        stopLocalServices();
        removeGroup();
    }

    /**
     * Remove the group created. It stops the access point created in this device.
     */
    void removeGroup() {
        mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
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
     * Register a local mWifiP2pDnsSdServiceInfo with the name of the SSID, the
     * MAC address and the interests of the group owner created
     * @param instance ID that identifies the application
     * @param btMacAddress Bluetooth MAC
     * @param interests Interests
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
        mWifiP2pDnsSdServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(
                instance, RelativePositionWiFiNoConnection.SERVICE_TYPE, record);
        mWifiP2pManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.i(TAG, "Service announced with success");
                mWifiP2pManager.addLocalService(mChannel, mWifiP2pDnsSdServiceInfo, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.i(TAG,"Added Local Service");
                    }

                    @Override
                    public void onFailure(int error) {
                        Log.i(TAG,"Failed to add a mWifiP2pDnsSdServiceInfo " + error);
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
        mWifiP2pManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
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
     * @param info group information
     */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        try {
            if (info.isGroupOwner) {
                mWifiP2pManager.requestGroupInfo(mChannel,this);
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
                    mWifiP2pManager.requestConnectionInfo(mChannel, WifiAccessPoint.this);
                } else {
                    Log.i(TAG,"We are DIS-connected");
                    if (!mCallBack.listNSenseDevices.isEmpty())
                        doScan();
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
		public void onReceive(Context context, Intent intent) {
			List<ScanResult> wifiScanList = mWifiManager.getScanResults();
            Log.i(TAG, "Need to find");
			for (NSenseDevice nSenseDevice : mCallBack.listNSenseDevices) {
				Log.i(TAG, nSenseDevice.toString());
			}
            Log.i(TAG, "SCAN RESULTS");
			if (!mCallBack.listNSenseDevices.isEmpty()) {
				for (NSenseDevice device : mCallBack.listNSenseDevices) {
                    for (ScanResult scanResult : wifiScanList) {
                        Log.w(TAG, "SCAN RESULT: " + scanResult.SSID + " " + scanResult.BSSID + " " + scanResult.level);
                        if (!scanResult.SSID.equalsIgnoreCase(device.getSsid())) {
                            if (!scanResult.BSSID.equalsIgnoreCase(device.getWifiApMac())) {
                                if (device.getDeviceName() != null) {
                                    if (!scanResult.SSID.contains(device.getDeviceName())) {
                                        if (checkWiFiP2PMacs(scanResult.BSSID, device.getWifiApMac())) {
                                            continue;
                                        } else {
                                            Log.i(TAG, "FOUND AP with partial MAC");
                                            Log.i(TAG, "SSID: " + scanResult.SSID + " MAC SCAN: " + scanResult.BSSID + " MAC: " + device.getWifiApMac() + " RSSI: " + scanResult.level);
                                        }
                                    } else {
                                        Log.i(TAG, "FOUND AP containing the DEVICE NAME in SSID");
                                        Log.i(TAG, "SSID: " + scanResult.SSID + " MAC SCAN: " + scanResult.BSSID + " MAC: " + device.getWifiApMac() + " RSSI: " + scanResult.level);
                                    }
                                }
                            } else {
                                Log.i(TAG, "FOUND AP with the same MAC ADDRESS");
                                Log.i(TAG, "SSID: " + scanResult.SSID + " MAC SCAN: " + scanResult.BSSID + " MAC: " + device.getWifiApMac() + " RSSI: " + scanResult.level);
                                if (device.getDeviceName().isEmpty()) {
                                    device.setDeviceName(device.getSsid().split("-")[2]);
                                }
                            }
                        } else {
                            Log.i(TAG, "FOUND AP with the same SSID");
                            Log.i(TAG, "SSID: " + scanResult.SSID + " MAC SCAN: " + scanResult.BSSID + " MAC: " + device.getWifiApMac() + " RSSI: " + scanResult.level);
                        }
		            		
		            	/* AP Found */
                        //device.setmNotFoundCounter(0);
                        //device.lastRssi = scanResult.level;
                        device.setWifiApMac(scanResult.BSSID);
		            	/* Get the SSID */
                        String ssid = scanResult.SSID;

                        double distance = DistanceModels.logDistancePathLossModel(scanResult.level, -36, -10);
                        Log.i(TAG, "Device: " + ssid + " RSSI: " + scanResult.level + "dBm - Distance: " + distance);
                        saveDistance(device.getDeviceName(), device.getWifiDirectMac(), device.getBtMac(), distance);
                        break;
                    }
                }

                if (mNumberOfScans > 0) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mNumberOfScans--;
                            Log.w(TAG, "Repeating SCAN");
                            doScan();
                        }
                    }, 2000);
                } else {
                    Log.i(TAG, "Running thread again");
                    mNumberOfScans = 2;
                }
			}

            mContext.unregisterReceiver(mWifiScanReceiverLocation);
            mWifiScanReceiverLocation = null;

		}
    }

    /**
     * There is a part of BSSID which is similar to WI-FI P2P MAC.
     * This method checks tha similarity and evaluate it.
     * @param scanMac BSSID
     * @param mac WI-FI P2P MAC
     * @return if the BSSID is similar to WI-FI P2P MAC returns true, returns false if not
     */
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
     * Saves the computed distance to a specific device to the database.
     * @param deviceName - Device found
     * @param distance -  Computed distance to the device
     */
    private void saveDistance(String deviceName, String wifiDirectMac, String btMac, double distance) {
        if (mDataSource.hasLocationEntry(wifiDirectMac, deviceName)) {
            /* Device exists */
            if(mDataSource.updateDistanceExpired(wifiDirectMac, deviceName, WIFI_UPDATE_FLAG)) {
                Log.i(TAG, "!!! WIFI UPDATE !!!");
                mDataSource.updateLocationEntry(deviceName, btMac, distance, WIFI_UPDATE_FLAG);
            }
        } else {
            /* New Device */
            mDataSource.registerLocationEntry(new LocationEntry(deviceName, wifiDirectMac, distance, btMac));
        }
	}
}