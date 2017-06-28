/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the NSense application.
 * This class provides some methods to provide extended
 * functionality to the android BluetoothAdapter.
 * @author Waldir Moreira (COPELABS/ULHT)
 */

package cs.usense.pipelines.proximity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BTManager {
	/** check how long BT scan is !!!!!! */
	//public static int DISCOVER_INTERVAL = 3 * 60 * 1000;
	public static int DISCOVER_INTERVAL = 1 * 60 * 1000;

	/** for debugging purposes */
	private boolean debug = true;
	private static final String TAG = "BTManager";
	
	/** Interface to global information about an application environment. */
	private Context mContext;

	/** This class is to access functionality of Bluetooth Adapter */
	private static BluetoothAdapter androidBTAdapter;

	/** Used to identify NSense! devices */
	private BluetoothServerSocket socket;
	private UUID uuid = UUID.fromString("11111111-1111-0101-0101-111111111111");
	
	/** Used to access the Bluetooth device list */
	private Map<BluetoothDevice, BluetoothClass> btDeviceList = new HashMap<BluetoothDevice, BluetoothClass>();

	/** Used to identify Bluetooth Device*/
	private FindBluetoothDevice BTDevFinder;
	
	/** This class is to access functionality of Bluetooth Adapter */
	private adapterBroadcastReceiver adapterReceiver;
	
	/** This class is to access the listener of Bluetooth device finder */
	private BTDeviceFinder listener;

	/** This variable is to check Scanning is Active */
	public boolean isScanningActive = false;
	
	/** This variable is to check Waiting for Scan Results */
	public boolean isWaitingScanResults = false;
	
	/** This variable is to check Bluetooth is turning on or off */
	private static boolean mBTisTurningOnOff = false;

	/** This variable for fetching UUID from Device */
	private String fetchingUUIDfromDevice;

	/** This variable to iterate bluetooth devices */
	Iterator<BluetoothDevice> BTDeviceIterator;

	/** This class is to use send or process message */
	private Handler mHandler = new Handler();
	
	/** NSENSE Devices found */
	private ArrayList<String> mNSENSEConfirmedDevices = new ArrayList<String>(); 
	
	/** Non NSENSE Devices found */
	private ArrayList<String> mNonNSENSEConfirmedDevices = new ArrayList<String>();
	private Map<String, Integer> mNonNSENSEStillToConfirmDevices = new HashMap<String, Integer>();
	private int threshold = 3;

	/** Devices found but without NSENSE confirmation (UUID) */
	private ArrayList<BluetoothDevice> mDeviceToFetchUUIDs = new ArrayList<BluetoothDevice>();

	/**
	 * This method allows for starting and stopping periodic Bluetooth scanning.
	 **/
	private Runnable runScan = new Runnable() {
		public void run() {
			if (isScanningActive) {
				if (!isBTEnabled()) {
					if(enableBT()) {
						stopPeriodicScanning();
					} else {
						Toast.makeText(mContext, "Error enabling BT. Closing Social Pipeline.", Toast.LENGTH_SHORT).show();
						close(mContext);
					}
					return;
				}
				if (!isWaitingScanResults && isBTEnabled()) {
					btDeviceList.clear();
					if (startDiscovery()) {
						if(debug){
							Log.i(TAG, "Starting BT scaning...");
						}
						isWaitingScanResults = true;
					}
					mHandler.postDelayed(runScan, DISCOVER_INTERVAL);
				}
				else if (isWaitingScanResults) {
					mHandler.postDelayed(runScan, DISCOVER_INTERVAL);
					isWaitingScanResults = false;
				}
			}
		}
	};

	/**
	 * This method is the constructor for BTManager.
	 * @param mContext Interface to global information about an application environment.
	 **/
	public BTManager(Context mContext) {
		this.mContext = mContext;
		messageToTurnOnBluetooth(mContext);
		androidBTAdapter = BluetoothAdapter.getDefaultAdapter();

		startAnnouncingUUID(uuid);

		BTDevFinder = new FindBluetoothDevice();
		adapterReceiver = new adapterBroadcastReceiver();
		mContext.registerReceiver(BTDevFinder, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		mContext.registerReceiver(BTDevFinder, new IntentFilter(BluetoothDevice.ACTION_UUID));
		mContext.registerReceiver(adapterReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		mContext.registerReceiver(adapterReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

	}

	/**
	 * This method turns on the bluetooth.
	 */
	private void messageToTurnOnBluetooth(Context mContext) {
		/**
		 * The following code forces visibility of BT to always. This
		 * code works for 4.3 and latest versions
		 * of android. For Android versions prior to 4.3 the maximum time visible is one hour.
		 * Even forcing the visibility, a cs message will appear to the user.
		 * The user must agree or not.
		 * After the NSense finish, the bluetooth must be disable and enable again to get the
		 * previous configurations (2 min of visibility). If this is not done, the BT visibility
		 * is always on, draining the device battery.
		 * To remove the cs (no user interaction) the device needs to be rooted.
		 * As indicated at: https://code.google.com/p/android/issues/detail?id=15486
		 */
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	/**
	 * This class allows for getting information
	 * from devices found during scan.
	 **/
	class FindBluetoothDevice extends BroadcastReceiver {
		/**
		 * This method receives different BluetoothDevice actions.
		 * @param context Interface to global information about an application environment. 
		 * @param intent The intent
		 **/
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			/** Get the BluetoothDevice object from the Intent */
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			/** Get the BluetoothClass object from the Intent */
			BluetoothClass btClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
			
			/** When discovery finds a device */
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				/** Add to btDeviceList for later filtering */
				if(debug){
					Log.i(TAG,"Adding: " + device.getName() + " to btDeviceList !!");
				}
				btDeviceList.put(device, btClass);
			}

			/** Get UUIDs from neighbor device */
			if(BluetoothDevice.ACTION_UUID.equals(action) ) {
				if (fetchingUUIDfromDevice == null)
					return;
				if (!fetchingUUIDfromDevice.equalsIgnoreCase(device.getAddress()))
					return;

				Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
				boolean UUIDfound = false;
				
				if(uuidExtra!=null){
					if(debug){
						Log.i(TAG,"looking for this uuid: " + uuid);
					}
					for (int i=0; i<uuidExtra.length; i++) {

						/** Filter devices and report only on device with Oi! UUID */
						if(debug){
							Log.i(TAG,"\n  Device: " + device.getName() + ", " + device + ", Service: " + uuidExtra[i].toString());
						}

						if(uuidExtra[i].toString().equals(uuid.toString())){
							if(debug){
								Log.i(TAG,"\n  Device: " + device.getName() + " sent !!");
							}
							mNSENSEConfirmedDevices.add(device.getAddress());
		        			mNonNSENSEConfirmedDevices.remove(device.getAddress());
		        			mNonNSENSEStillToConfirmDevices.remove(device.getAddress());
							/** Send this device to Bluetooth pipeline */
							listener.onDeviceFound(device, btDeviceList.get(device));
							UUIDfound = true;
						}
					}
				} else {
					Log.i(TAG,"UUID null for device " + device.getName());
				}
				if (!UUIDfound) {
        			if(mNonNSENSEStillToConfirmDevices.containsKey(device.getAddress())) {
        				mNonNSENSEStillToConfirmDevices.put(device.getAddress(), mNonNSENSEStillToConfirmDevices.get(device.getAddress()) + 1);
        			} else {
        				mNonNSENSEStillToConfirmDevices.put(device.getAddress(), 1);
        			}
        		}
	        	
	        	mDeviceToFetchUUIDs.remove(0);
	        	
	        	if (mDeviceToFetchUUIDs.isEmpty()) {
	        		fetchingUUIDfromDevice = null;

	        		for(Iterator<Map.Entry<String, Integer>> it = mNonNSENSEStillToConfirmDevices.entrySet().iterator(); it.hasNext(); ) {
	        		    Map.Entry<String, Integer> entry = it.next();
	        		    if (entry.getValue() > threshold) {
	        		    	mNonNSENSEConfirmedDevices.add(entry.getKey());
	        		    	it.remove();
	        		    }
	        		}
	        		
	        		return;
	        	}
	        	startFetchingUUIDFromDevice(mDeviceToFetchUUIDs.get(0));
	        } 
		}
	}

	/**
	 * This class allows for getting information from BluetoothAdapter.
	 **/
	class adapterBroadcastReceiver extends BroadcastReceiver {

		/**
		 * This method receives different actions.
		 * @param context Interface to global information about an application environment. 
		 * @param intent The intent
		 **/
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
				if(debug){
					Log.i(TAG,"Entered the Finished ");
				}
				isWaitingScanResults = false;
				
				if(BluetoothCore.appRestarted || BluetoothCore.SWupdated) {
					BluetoothCore.appRestartReset();
					BluetoothCore.SWupdatedReset();
                }

				/** When discovery process is done, acquire UUIDs of encountered peers */
				Iterator<BluetoothDevice> iterator = btDeviceList.keySet().iterator();
                while(iterator.hasNext()) {
                	// Get Services for paired devices
                	BluetoothDevice device = iterator.next();
                	
                	if(mNSENSEConfirmedDevices.contains(device.getAddress())) {
                		Log.i(TAG, "NSENSE Device found " + device.getName());
                		//Send this device to Social Proximity
	        			listener.onDeviceFound(device, btDeviceList.get(device));  
                	}
                	else if (!mNonNSENSEConfirmedDevices.contains(device.getAddress())) {
                		mDeviceToFetchUUIDs.add(device);  
                	} else 
                		Log.i(TAG, "Non NSENSE Device found, ignoring...");
                }
                if (mDeviceToFetchUUIDs.isEmpty())
                	return;
                startFetchingUUIDFromDevice(mDeviceToFetchUUIDs.get(0));
            }

			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
						BluetoothAdapter.ERROR);
				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					mBTisTurningOnOff = false;
					enableBT();
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					mBTisTurningOnOff = true;
					break;
				case BluetoothAdapter.STATE_ON:
					mBTisTurningOnOff = false;
					startPeriodicScanning();
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					Toast.makeText(mContext, "Enabling Bluetooth for Social Pipeline.", Toast.LENGTH_SHORT).show();
					mBTisTurningOnOff = true;
					break;
				}
			}
		}
	}

	/**
	 * This method stops scanning and unregister BroadcastReceivers.
	 * @param c Interface to global information about an application environment.
	 **/
	public void close (Context c) {
		this.stopPeriodicScanning();
		c.unregisterReceiver(BTDevFinder);
		c.unregisterReceiver(adapterReceiver);
		if (socket != null) {
			try {
				socket.close();
				socket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startFetchingUUIDFromDevice(BluetoothDevice mDevice) {
		if(mDevice == null)
			return;
    	Log.i(TAG, "Fetching UUIDs for  " + mDevice.getName());
    	if(!mDevice.fetchUuidsWithSdp()) {
    		Log.i(TAG, "\nSDP Failed for " + mDevice.getName());
    		return;
    	}
    	fetchingUUIDfromDevice = mDevice.getAddress();
	}

	/**
	 * This method starts scanning.
	 **/
	public void startPeriodicScanning() {
		//startAnnouncingUUID(uuid);
		isScanningActive = true;
		mHandler.removeCallbacks(runScan);
		mHandler.post(runScan);
	}

	/**
	 * This method stops scanning.
	 **/
	private void stopPeriodicScanning() {
		isScanningActive = false;
		mHandler.removeCallbacks(runScan);          
	}

	/**
	 * This method checks whether Bluetooth is enabled.
	 * @return true/false If Bluetooth Adapter is enabled/not
	 **/
	public boolean isBTEnabled () {
		return androidBTAdapter.isEnabled();
	}  

	/**
	 * This method enables Bluetooth.
	 * @return true/false If Bluetooth Adapter is Turning on or off
	 **/
	public boolean enableBT() {
		if (!mBTisTurningOnOff)
			return androidBTAdapter.enable();
		else
			return true;
	}
	
	/**
	 * This method announce the UUIDs.
	 * @return true/false If Bluetooth Adapter is Turning on or off
	 **/
	private boolean startAnnouncingUUID(UUID mUuid) {
		//Advertised UUID to let peer node knows that current node runs Oi!  
		try {
			socket = androidBTAdapter.listenUsingInsecureRfcommWithServiceRecord("nsense", mUuid);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method starts Bluetooth discovery process.
	 * @return true/false True on success, false on failure
	 **/
	public boolean startDiscovery() {
		return androidBTAdapter.startDiscovery();
	}

	/**
	 * This method sets a change listener.
	 * @param listener The BTDeviceFinder listener
	 **/
	public void setOnBTChangeListener (BTDeviceFinder listener) {
		this.listener = listener;
	}

	/**
	 * This method clears the change listener.
	 **/
	public void clearOnBTChangeListener () {
		this.listener = null;
	}

	/**
	 * This method provides the MAC Address and name of local Bluetooth adapter.
	 * @return localinfo The local MAC Address
	 */
	public List<String> getLocalInfo(){
		List<String> localinfo = new ArrayList<String>();
		localinfo.add(0, androidBTAdapter.getName());
		localinfo.add(1, androidBTAdapter.getAddress());
		return localinfo;
	}

	public static String getBTMACAddress(Context context) {
		String btMacAddress;
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			btMacAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
		} else {
			btMacAddress = androidBTAdapter.getAddress();
		}
		return btMacAddress;
	}


}