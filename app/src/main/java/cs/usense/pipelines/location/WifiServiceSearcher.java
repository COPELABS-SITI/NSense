/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class manage all the devices found at the WiFi mWifiP2pManager discover mechanism.
 * @author Luis Amaral Lopes (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.pipelines.location;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cs.usense.db.NSenseDataSource;

class WifiServiceSearcher {

    /** This variable is used to debug WifiServiceSearcher class */
    private static final String TAG = "WifiServiceSearcher";

    /** This variable is used to set a time window to reboot Wi-Fi */
    private static int WIFI_RESTART_INITIAL_FIX_TIME = 2 * 60 * 1000;

    /** Interface to global
     *    about an application environment. */
    private Context mContext;

    /** NSense Data base. */
    private NSenseDataSource mDataSource;

    /** This class provides the API for managing Wi-Fi peer-to-peer connectivity. */
    private WifiP2pManager mWifiP2pManager;

    /** A mChannel that connects the application to the Wifi mWifiP2pManager framework. */
    private WifiP2pManager.Channel mChannel;

    /** A class for creating a Bonjour service discovery request. */
    private WifiP2pDnsSdServiceRequest mServiceRequest;

    /** RelativePositionWiFiNoConnection module. */
    private RelativePositionWiFiNoConnection mCallBack;

    /** Object that contains info from the TXT of the device found. */
    private TxTInfoReceived mTxTInfoReceived = null;

    private Timer mWifiRestartFixTimer = null;

    private class TxTInfoReceived {
        String TXTBTMACAddress = "";
    	WifiP2pDevice mDevice = null;
        String interests = "";
    }

    /**
     * WifiServiceSearcher constructor
     * @param context - Interface to global information about an application environment.
     * @param callback - RelativePositionWiFiNoConnection module.
     * @param dataSource - NSense data base.
     */
    WifiServiceSearcher(Context context, RelativePositionWiFiNoConnection callback, NSenseDataSource dataSource) {
        mContext = context;
        mCallBack = callback;
        mDataSource = dataSource;
    }

    /**
     * It initializes the WiFi mWifiP2pManager and gets a mChannel.
     */
    public void start() {
        mWifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        if (mWifiP2pManager == null) {
            Log.i(TAG,"This device does not support Wi-Fi Direct");
        } else {
            mChannel = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), null);
        }
    }

    /**
     * Stops any discovery request running.
     */
    void stop() {
        stopDiscovery();
    }

    /**
     * Requests a service discovery.
     */
    void startServiceDiscovery() {


        if(mWifiRestartFixTimer == null) {
            startWiFiRestartFixTimer();
        }


        /**
         * Register mListeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */
        mWifiP2pManager.setDnsSdResponseListeners(mChannel, new DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice device) {
                        stopWiFiRestartFixTimer();
                        /** A service has been discovered. Is this our app? */
                        if (registrationType.contains(RelativePositionWiFiNoConnection.SERVICE_TYPE)) {
                            Log.i(TAG, "Found Something...");
                            String deviceNameFixed = deviceNameFix(device.deviceName);
                            int listPosition = searchDeviceOnList(deviceNameFixed, device.deviceAddress);
                            if (listPosition == -1) {
                                /** New Device */
                                NSenseDevice nSenseDevice = new NSenseDevice(deviceNameFixed, instanceName, device.deviceAddress);
                                addTxtInfo(nSenseDevice, device.deviceName);
                                Log.i(TAG, "Fill new record with " + nSenseDevice.toString());
                                mCallBack.listNSenseDevices.add(nSenseDevice);
                                if (!mDataSource.hasLocationEntry(device.deviceAddress, deviceNameFixed)) {
                                    mDataSource.registerLocationEntry(new LocationEntry(nSenseDevice.getDeviceName(), nSenseDevice.getWifiDirectMACAddress()));
                                }
                                listPosition = mCallBack.listNSenseDevices.size() - 1;
                            } else {
                                /** Not New Device */
                                Log.i(TAG, "position " + listPosition + "\n" + mCallBack.listNSenseDevices.get(listPosition).toString());
                                addTxtInfo(mCallBack.listNSenseDevices.get(listPosition), device.deviceName);
                            }
                            if(mDataSource.hasBTDevice(mCallBack.listNSenseDevices.get(listPosition))) {
                                Log.i(TAG, "I will update the interests on database");
                                mDataSource.updateInterests(mCallBack.listNSenseDevices.get(listPosition));
                            } else {
                                mDataSource.insertDevice(mCallBack.listNSenseDevices.get(listPosition));
                            }
                        } else {
                            Log.i(TAG, "Other device Type found " + instanceName + " " + registrationType);
                        }
                    }

            }, new DnsSdTxtRecordListener() {

                /**
                 * A new TXT record is available. Pick up the advertised buddy name.
                 */
                @Override
                public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record, WifiP2pDevice device) {
                	if (record != null) {
                        mTxTInfoReceived = new TxTInfoReceived();
                        mTxTInfoReceived.mDevice = device;
                        String mBTAddress = record.get(LocationPipeline.BT_MAC_INFO);
                        String mInterests = record.get(LocationPipeline.INTERESTS_INFO);

                        if (mBTAddress != null && !mBTAddress.equals("")){
                            Log.i(TAG, "TXT BTMAC Received: " + mBTAddress);
                            mTxTInfoReceived.TXTBTMACAddress = mBTAddress;
                        } else {
                            Log.w(TAG, "No TXT BTMAC Received.");
                        }

                        if (mInterests != null && !mInterests.equals("")){
                            Log.i(TAG, "TXT Interests Received: " + mInterests);
                            mTxTInfoReceived.interests = mInterests;
                        } else {
                            Log.w(TAG, "No TXT Interests Received.");
                        }

                    }
                }
            });


        /** After attaching mListeners, create a service request and initiate discovery. */
        mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mWifiP2pManager.addServiceRequest(mChannel, mServiceRequest,
                new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.i(TAG,"Added service discovery request");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        Log.i(TAG,"Failed adding service discovery request");
                    }
                });

        mWifiP2pManager.discoverServices(mChannel, new ActionListener() {

            @Override
            public void onSuccess() {
                Log.i(TAG,"Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                Log.i(TAG,"Service discovery failed");

            }
        });
    }

    private void addTxtInfo(NSenseDevice nSenseDevice, String deviceAddress) {
        if (mTxTInfoReceived != null) {
            Log.i(TAG, "Compare: " + mTxTInfoReceived.mDevice.deviceName + " " + deviceAddress);
            if (mTxTInfoReceived.mDevice.deviceName.equalsIgnoreCase(deviceAddress)) {
                nSenseDevice.setWifiAPMACAddress(mTxTInfoReceived.mDevice.deviceAddress);
                nSenseDevice.setBtMACAddress(mTxTInfoReceived.TXTBTMACAddress);
                nSenseDevice.setInterests(mTxTInfoReceived.interests);
            }
            mTxTInfoReceived = null;
        }
    }

    private int searchDeviceOnList(String deviceName, String deviceAddress) {
        int position = -1;
        for(int i = 0; i < mCallBack.listNSenseDevices.size(); i++) {
            if(deviceName.equalsIgnoreCase(mCallBack.listNSenseDevices.get(i).getDeviceName())) {
                position = i;
                break;
            } else if(deviceAddress.equalsIgnoreCase(mCallBack.listNSenseDevices.get(i).getWifiDirectMACAddress())) {
                position = i;
                break;
            }
        }
        return position;
    }

    private String deviceNameFix(String deviceName) {
        if(deviceName.contains("[Phone]")) {
            deviceName = deviceName.split("\\[.*\\]")[1].trim();
            Log.i(TAG, "Name fixed to " + deviceName);
        }
        return deviceName;
    }

    /**
     * Stops any discovery request running.
     */
    void stopDiscovery() {
    	if (mServiceRequest != null) {
    		mWifiP2pManager.removeServiceRequest(mChannel, mServiceRequest, null);
    		mServiceRequest = null;
    	}
    }

    private void restartWiFi() {
        Log.e(TAG, "APPLYING HOTFIX: Restarting WiFi... ");
        WifiManager wifimanager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifimanager.setWifiEnabled(false);
    }

    private void startWiFiRestartFixTimer() {
        Log.w(TAG, "Starting Timer WiFi P2P Discovery issue hotfix.");
        mWifiRestartFixTimer = new Timer();
        mWifiRestartFixTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                restartWiFi();
            }

        }, WIFI_RESTART_INITIAL_FIX_TIME, WIFI_RESTART_INITIAL_FIX_TIME);
    }

    private void stopWiFiRestartFixTimer() {
        if (mWifiRestartFixTimer != null) {
            Log.w(TAG, "Stopped Timer WiFi P2P Discovery issue hotfix.");
            mWifiRestartFixTimer.cancel();
            mWifiRestartFixTimer = null;
        }
    }
}