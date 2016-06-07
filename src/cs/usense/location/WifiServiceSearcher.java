/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class manage all the devices found at the WiFi p2p discover mechanism.
 * @author Luis Amaral Lopes (COPELABS/ULHT)
 */

package cs.usense.location;

import java.util.Map;

import cs.usense.db.NSenseDataSource;
import cs.usense.location.RelativePositionWiFiNoConnection.NSenseDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class WifiServiceSearcher {

    private String TAG = "WifiServiceSearcher";
    /** Interface to global information about an application environment. */
    private Context mContext;
    /** NSense Data base. */
    private NSenseDataSource dataSource;
    /** This class provides the API for managing Wi-Fi peer-to-peer connectivity. */
    private WifiP2pManager p2p;
    /** A channel that connects the application to the Wifi p2p framework. */
    private WifiP2pManager.Channel channel;
    /** A class for creating a Bonjour service discovery request. */
    private WifiP2pDnsSdServiceRequest serviceRequest;
    /** RelativePositionWiFiNoConnection module. */
    private RelativePositionWiFiNoConnection callback;
    /** Object that contains info from the TXT of the device found. */
    private TxTInfoReceived mTxTInfoReceived = null;
    class TxTInfoReceived {
    	public String TXTMACAddress = "";
    	public WifiP2pDevice mDevice = null;
    }
    
    /**
     * WifiServiceSearcher constructor
     * @param Context - Interface to global information about an application environment.
     * @param callback - RelativePositionWiFiNoConnection module.
     * @param dataSource - NSense data base.
     */
    public WifiServiceSearcher(Context Context, RelativePositionWiFiNoConnection callback, NSenseDataSource dataSource) {
        this.mContext = Context;
        this.callback = callback;
        this.dataSource = dataSource;
    }

    /**
     * It initializes the WiFi p2p and gets a channel.
     */
    public void Start() {
        p2p = (WifiP2pManager) this.mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        if (p2p == null) {
            Log.i(TAG,"This device does not support Wi-Fi Direct");
        }else {
            channel = p2p.initialize(this.mContext, this.mContext.getMainLooper(), null);
        }
    }

    /**
     * Stops any discovery request running.
     */
    public void Stop() {
        stopDiscovery();
    }

    /**
     * Requests a service discovery.
     */
    public void startServiceDiscovery() {

        /**
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */
        p2p.setDnsSdResponseListeners(channel,
                new DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                            String registrationType, WifiP2pDevice srcDevice) {

                        /** A service has been discovered. Is this our app? */
                    if (registrationType.contains(RelativePositionWiFiNoConnection.SERVICE_TYPE)) {
                    	Log.i(TAG, "Found Something...");
                    	/** update the UI and add the item the discovered */
                    	/** device. */ 
                		boolean mDeviceFound = false;
                		if (!callback.listNSenseDevices.isEmpty()) {
                    		for (int i = 0; i < callback.listNSenseDevices.size(); i++) {
                    			NSenseDevice mDevice = callback.listNSenseDevices.get(i);
                            	if (mDevice.mWiFiDirectMACAddress.equals(srcDevice.deviceAddress)) {
                            		mDeviceFound = true;
                            		if (mDevice.mSSID.equals(instanceName)) {
                            			Log.i(TAG,"Same SSID. no updating - " + srcDevice.deviceName);
                            			break;
                            		}
                            		/** Known Device - Just update the SSID */                          
                            		Log.i(TAG,"Known Device Found added: " + srcDevice.deviceName);
                                	Log.i(TAG, " Updating his SSID to " + instanceName);
                            		mDevice.mSSID = instanceName;
                            		callback.listNSenseDevices.set(i, mDevice);
                            		break;
                            	}
                            }
                		}
                		if (!mDeviceFound) {
                			Log.i(TAG,"New Device Found added: " + srcDevice.deviceName);
                        	Log.i(TAG, " His SSID: " + instanceName);
                        	NSenseDevice mNewDevice = new RelativePositionWiFiNoConnection().new NSenseDevice();
                        	mNewDevice.mSSID = instanceName;
                        	mNewDevice.mWiFiDirectMACAddress = srcDevice.deviceAddress;
                        	mNewDevice.mDeviceName = srcDevice.deviceName;
                        	if (mTxTInfoReceived != null) {
                        		if (mTxTInfoReceived.mDevice.deviceAddress.equalsIgnoreCase(srcDevice.deviceAddress)) {
                        			mNewDevice.mWiFiAPMACAddress = mTxTInfoReceived.TXTMACAddress;
                        		}
                        		mTxTInfoReceived = null;
                        	}
                        	callback.listNSenseDevices.add(mNewDevice);
                		}
                        Log.d(TAG, srcDevice.deviceName + " is " + srcDevice.deviceAddress);
                    	/** Save Distance into DB */
                        if (!dataSource.hasLocationEntry(srcDevice.deviceAddress)){
                        	/** New Device */
                        	LocationEntry entry = new LocationEntry();
                        	entry.setDeviceName(srcDevice.deviceName);
                        	entry.setBSSID(srcDevice.deviceAddress);
                        	entry.setDistance(-1);
                        	entry.setLastUpdate(SystemClock.elapsedRealtime());
                        	dataSource.registerLocationEntry(entry);
                        	callback.notifyDataBaseChange();
                        }
                    } else {
                    	Log.i(TAG, "Other device Type found " + instanceName + " " + registrationType);
                    }
                }
            }, new DnsSdTxtRecordListener() {

                /**
                 * A new TXT record is available. Pick up the advertised
                 * buddy name.
                 */
                @Override
                public void onDnsSdTxtRecordAvailable(
                        String fullDomainName, Map<String, String> record,
                        WifiP2pDevice device) {
                	if (record == null)
                		return;
                	String mMACAddress = record.get("mMACAddress");
                	if (mMACAddress == null)
                		return;
                	if (!mMACAddress.equals("")){
                    	Log.i(TAG, "TXT MAC Received: " + record.get("mMACAddress"));
                    	mTxTInfoReceived = new TxTInfoReceived();
                    	mTxTInfoReceived.TXTMACAddress = record.get("mMACAddress");
                    	mTxTInfoReceived.mDevice = device;
                	} else {
                		Log.w(TAG, "No TXT MAC Received.");
                	}
                }
            });

        /** After attaching listeners, create a service request and initiate */
        /** discovery. */
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        p2p.addServiceRequest(channel, serviceRequest,
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
        p2p.discoverServices(channel, new ActionListener() {

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
    
    /**
     * Removes all services request and start a new service request.
     */
    public void restartWifiP2P() {
		Log.i(TAG, "Restarting Discover Service");
		p2p.clearServiceRequests(channel, null);
		startServiceDiscovery();
	}

    /**
     * Stops any discovery request running.
     */
    public void stopDiscovery() {
    	if (serviceRequest != null) {
    		p2p.removeServiceRequest(channel, serviceRequest, null);
    		serviceRequest = null;
    	}
    }
}
