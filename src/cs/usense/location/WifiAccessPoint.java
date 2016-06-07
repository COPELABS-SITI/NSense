/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class is the entry point of Location Pipeline.
 * When there are nsense devices near (provided by WifiServiceSearcher) it performs a
 * WiFi scan to detect this devices a to compute the respective relative distance.
 * @author Luis Amaral Lopes (COPELABS/ULHT)
 */

package cs.usense.location;

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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.usense.db.NSenseDataSource;
import cs.usense.location.RelativePositionWiFiNoConnection.NSenseDevice;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;

public class WifiAccessPoint implements WifiP2pManager.ConnectionInfoListener,WifiP2pManager.GroupInfoListener{

    private String TAG = "WifiAccessPoint";
    
    /** WifiAccessPoint Module */
    private WifiAccessPoint that = this;
    /** Interface to global information about an application environment. */
    private Context context;
    /** NSense Data base */
    private NSenseDataSource dataSource;
    /** Android WiFi P2P Manager */
    private WifiP2pManager p2p;
    /** A channel that connects the application to the Wifi p2p framework. */
    private WifiP2pManager.Channel channel;
    /** SSID of the AP created on this device (Group Owner) */
    private String mNetworkName = "";
    /** MAC Address of the AP created on this device (Group Owner) */
    private String mMACAddress = "";
    /** A class for storing Bonjour service information that is advertised over a Wi-Fi peer-to-peer setup. */
    private WifiP2pDnsSdServiceInfo service;
    /** Base class for code that will receive intents sent by sendBroadcast(). */
    private BroadcastReceiver receiver;
    /** Structured description of Intent values to be matched. */
    private IntentFilter filter;
    /** RelativePositionWiFiNoConnection module */
    private RelativePositionWiFiNoConnection callback;
    /** Flag about if it is necessary to do a WiFi scan */
    private Boolean toScan = false;
    /** This class provides the primary API for managing all aspects of Wi-Fi connectivity.*/
    private WifiManager wifiManager = null;
    /** BroadcastReceiver that receives a WiFi scan result. */
    private WifiScanReceiverLocation wifiReceiver = null;
    /** Flag to inform if a request was already performed and it is still waiting for a result. */
    private boolean mRequestConnectionInfo = false;
    
    /**
     * WifiAccessPoint constructor
     * @param Context -Interface to global information about an application environment.
     * @param callback - RelativePositionWiFiNoConnection module.
     * @param dataSource - NSense data base.
     */
    public WifiAccessPoint(Context Context, RelativePositionWiFiNoConnection callback, NSenseDataSource dataSource) {
        this.context = Context;
        this.callback = callback;
        this.dataSource = dataSource;
    }
    
    /**
     * Sends a request to the RelativePositionWiFiNoConnection to restart the WiFi p2p discover mechanism.
     */
    public void restartDiscover() {
    	if (callback != null)
    		callback.restartDiscover();
    }
    
    /**
     * Requests a WiFi scan
     */
    public void doScan() {
    	wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiReceiver != null)
			context.unregisterReceiver(wifiReceiver);
		wifiReceiver = new WifiScanReceiverLocation();
		context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		Log.i(TAG, "Start Scan");
		wifiManager.startScan();
    }
    
    /**
     * It initializes the WiFi P2P and register a receiver to receive all 
     * info about the WiFi state and connection state.
     */
    public void Start() {

        p2p = (WifiP2pManager) this.context.getSystemService(Context.WIFI_P2P_SERVICE);

        if (p2p == null) {
            Log.i(TAG,"This device does not support Wi-Fi Direct");
        } else {
            channel = p2p.initialize(this.context, this.context.getMainLooper(), null);
            p2p.clearLocalServices(channel, null);
            p2p.clearServiceRequests(channel, null);
            p2p.removeGroup(channel, null);
            
            receiver = new AccessPointReceiver();
            filter = new IntentFilter();
            filter.addAction(WIFI_P2P_STATE_CHANGED_ACTION);
            filter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);
            this.context.registerReceiver(receiver, filter);
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
    public void Stop() {
        this.context.unregisterReceiver(receiver);
        stopLocalServices();
        removeGroup();
    }

    /**
     * Remove the group created. It stops the access point created in this device.
     */
    public void removeGroup() {
    	toScan = true;
        p2p.removeGroup(channel,new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.i(TAG,"Cleared Local Group ");
            }

            public void onFailure(int reason) {
                Log.i(TAG,"Clearing Local Group failed, error code " + reason);
            }
        });
    }

    /**
     * Provides the Group owner MAC address (Wifi P2P interface created for the group owner)
     * @return the Group owner MAC address
     */
    public String getWFAPMacAddress(){
    	try {
    	    List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
    	    for (NetworkInterface ntwInterface : interfaces) {

    	        if (ntwInterface.getName().equalsIgnoreCase("p2p-wlan0-0")) {
    	            byte[] byteMac = ntwInterface.getHardwareAddress();
    	            if (byteMac==null){
    	                return null;
    	            }
    	            StringBuilder strBuilder = new StringBuilder();
    	            for (int i=0; i<byteMac.length; i++) {
    	                strBuilder.append(String.format("%02X:", byteMac[i]));
    	            }

    	            if (strBuilder.length()>0){
    	                strBuilder.deleteCharAt(strBuilder.length()-1);
    	            }

    	            return strBuilder.toString();
    	        }

    	    }
    	} catch (Exception e) {
    	    Log.d(TAG, e.getMessage());
    	}
    	return "";
    }
    
    /**
     * Provides information about the Group Owner created (Access Point)
     * This information include the SSID and the MAC address.
     */
    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
        try {
            if(mNetworkName.equals(group.getNetworkName())){
                Log.i(TAG,"Already have local service for " + mNetworkName);
            }else {
                mNetworkName = group.getNetworkName();
                mMACAddress = getWFAPMacAddress();
 
                startLocalService(group.getNetworkName(), mMACAddress);
            }
        } catch(Exception e) {
            Log.i(TAG,"onGroupInfoAvailable, error: " + e.toString());
        }
        mRequestConnectionInfo = false;
    }

    /**
     * Register a local service with the name of the SSID and the 
     * MAC address of the group owner created
     * @param instance - ID that identifies the application
     * @param mMACAddress - the Group Owner MAC address
     */
    public void startLocalService(String instance, String mMACAddress) {
        
        Map<String, String> record = new HashMap<String, String>();
        record.put("mMACAddress", mMACAddress);
        
        p2p.clearLocalServices(channel, null);
        
        service = WifiP2pDnsSdServiceInfo.newInstance(
                instance, RelativePositionWiFiNoConnection.SERVICE_TYPE, record);
        p2p.addLocalService(channel, service, new ActionListener() {

            @Override
            public void onSuccess() {
                Log.i(TAG,"Added Local Service");
            }

            @Override
            public void onFailure(int error) {
                Log.i(TAG,"Failed to add a service");
            }
        });
    }

    /**
     * Removes all local services created.
     */
    public void stopLocalServices() {
        mNetworkName = "";
        mMACAddress = "";

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
     *
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
            }  else if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                	if (!mRequestConnectionInfo) {
                		Log.i(TAG,"We are connected, will check info now");
                		mRequestConnectionInfo = true;
                    	p2p.requestConnectionInfo(channel, that);
                	}
                } else{
                    Log.i(TAG,"We are DIS-connected");
                   
                    if (toScan) {
                    	if (!callback.listNSenseDevices.isEmpty())
                    		doScan();
                    	else
                    		callback.scheduleThread();
                    }
                }
            }
        }
    }
       
    /**
     * BroadcastReceiver that receives a WiFi scan result.
     * @author Luis Amaral Lopes (COPELABS/ULHT)
     *
     */
    private class WifiScanReceiverLocation extends BroadcastReceiver {

		@Override
		public void onReceive(Context c, Intent intent) {
			if (!toScan)
				return;
			List<ScanResult> wifiScanList = wifiManager.getScanResults();
			boolean mSomethingFound = false;
			Log.i(TAG, "SCAN RESULTS");
			for (NSenseDevice teste: callback.listNSenseDevices) {
				Log.i(TAG,teste.mSSID + " " + teste.mDeviceName +" MACWIFIDIRECT: " + teste.mWiFiDirectMACAddress + " MACAP: "+ teste.mWiFiAPMACAddress);
			}
			if (!callback.listNSenseDevices.isEmpty()) {
				
				ArrayList<Integer> devicesLost = new ArrayList<Integer>();
				
				for (int j = 0; j < callback.listNSenseDevices.size(); j++) {
					NSenseDevice mDevice = callback.listNSenseDevices.get(j);
					boolean mFound = false; 
					for (int i = 0; i < wifiScanList.size(); i++) {
						Log.w(TAG, "SCAN RESULT: " + wifiScanList.get(i).SSID + " " + wifiScanList.get(i).BSSID);
		            	if (!wifiScanList.get(i).SSID.equals(mDevice.mSSID)) {
		            		if (!wifiScanList.get(i).BSSID.equalsIgnoreCase(mDevice.mWiFiAPMACAddress))
		            			continue;
		            		else  Log.i(TAG, "FOUND AP with the same MAC ADDRESS");
		            	} else Log.i(TAG, "FOUND AP with the same SSID");
		            		
		            	/** AP Found */
		            	mSomethingFound = true;
		            	mFound = true;
		            	mDevice.mCountNotFound = 0;
		            	mDevice.mWiFiAPMACAddress = wifiScanList.get(i).BSSID;
		            	callback.listNSenseDevices.set(j, mDevice);
		            	/** Get the SSID */
		                String ssid = wifiScanList.get(i).SSID; 
		                Double mDistance = calculateDistance(wifiScanList.get(i).level, wifiScanList.get(i).frequency);
		                if (mDistance > 100) mDistance = (double) 100;
		                Log.d(TAG,"Distance to " + ssid + ": " + mDistance.toString() + " m.");
		                saveDistance(mDistance, mDevice.mWiFiDirectMACAddress, mDevice.mDeviceName);
		                break;
	            	}
					
					if (!mFound) {
						if (mDevice.mCountNotFound < 10) {
							mDevice.mCountNotFound++;
							Log.w(TAG, "Trying " + mDevice.mCountNotFound + "/10");
						} else {
							devicesLost.add(j);
							mDevice.mCountNotFound = 0;
						}
					}	
	            }		
			}
            
            try {
            	context.unregisterReceiver(wifiReceiver);
            	wifiReceiver = null;
            } catch (IllegalArgumentException e) {
            	Log.e(TAG, "Wifi Receiver already unregister.");
            }
            if (!mSomethingFound) {
            	final Handler handler = new Handler();
            	handler.postDelayed(new Runnable() {
            		@Override
            		public void run() {
            			Log.w(TAG, "Repeating SCAN");
            			doScan();
                    }
            	}, 500);
            } else {
            	Log.i(TAG, "Running thread again");
                toScan = false;
                callback.scheduleThread();
            }
		}
    }
    
    /**
     * Computes the distance to a device.
     * @param signalLevelInDb -  The detected signal level in dBm, also known as the RSSI.
     * @param freqInMHz - The frequency in MHz of the channel over which the client is communicating with the access point.
     * @return
     */
    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
		return Math.pow(10, ((Math.abs(signalLevelInDb) - 20*Math.log10(freqInMHz) - 3 + 28))/22);
	}
    
    /**
     * Saves the computed distance to a specific device to the database.
     * @param mDistance -  Computed distance to the device
     * @param mWiFiDirectMAC - MAC Address of the device
     * @param mDeviceName - Device Name
     */
    private void saveDistance(double mDistance, String mWiFiDirectMAC, String mDeviceName) {
		Log.i(TAG, "Device " + mDeviceName + " Distance computed: " + mDistance);
		
		/** Save Distance into DB */
        if (dataSource.hasLocationEntry(mWiFiDirectMAC)){
        	/** Device exists */
        	LocationEntry entry = dataSource.getLocationEntry(mWiFiDirectMAC);
        	if (entry.getDistance() == -1)
        		entry.setDistance(mDistance);
        	else
        		entry.setDistance(entry.getDistance()*0.6 + mDistance* 0.4);
        	entry.setLastUpdate(SystemClock.elapsedRealtime());
        	dataSource.updateLocationEntry(entry);
        } else {
        	/** New Device */
        	LocationEntry entry = new LocationEntry();
        	entry.setDeviceName(mDeviceName);
        	entry.setBSSID(mWiFiDirectMAC);
        	entry.setDistance(mDistance);
        	entry.setLastUpdate(SystemClock.elapsedRealtime());
        	dataSource.registerLocationEntry(entry);
        }
        callback.notifyDataBaseChange();
	}
}
