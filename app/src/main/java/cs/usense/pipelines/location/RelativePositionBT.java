/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/16.
 * Class is part of the NSense application. It provides support for location pipeline.
 */

package cs.usense.pipelines.location;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;

import cs.usense.db.NSenseDataSource;

/**
 * This class computes the distance between the user and the peers around.
 * The distance is computed through BT and this class computes short distances.
 * Longer distances are computed through WI-FI.
 * @author Luis Amaral Lopes (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
class RelativePositionBT {

    /** This variable is used to debug RelativePositionBT class */
    private static final String TAG = "RelativePositionBT";

    /** Calibrated power of the transmitter (dBm) at 0 meter */
    private static final int DEFAULT_TX_POWER_LEVEL = -26;

    /** This variable represents the RSSI at 1 meter using BT */
    private static final int RSSI_AT_1_METER = -62;

    /** This variable represents the BT flag on DB */
    private static final int BT_UPDATE_FLAG = 0;

    /** This object is used to manage the database*/
    private NSenseDataSource mDataSource;

    /** This object contains the application context */
    private Context mContext;

    /**
     * Constructor of RelativePositionBT class
     * @param context application context
     * @param dataSource database reference
     */
    RelativePositionBT(Context context, NSenseDataSource dataSource) {
        mContext = context;
        mDataSource = dataSource;
        context.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                if (rssi > -70) {
                    if (filterDevice((BluetoothClass) intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS))) {
                        BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        double distance = DistanceModels.logDistancePathLossModel(rssi, RSSI_AT_1_METER, DEFAULT_TX_POWER_LEVEL);
                        Log.i(TAG, "LOG " + btDevice.getName() + " " + rssi + " " + distance);
                        if (mDataSource.hasLocationEntry(btDevice.getAddress(), btDevice.getName())) {
                            // Device exists
                            mDataSource.updateLocationEntry(btDevice.getName(), btDevice.getAddress(), distance, BT_UPDATE_FLAG);
                        } else {
                            // New device
                            mDataSource.registerLocationEntry(new LocationEntry(btDevice.getName(), null, distance, btDevice.getAddress()));
                        }
                    }
                }
            }
        }
    };

    /**
     * This method filter the devices detected by BT. 524 represents smart phones
     * https://developer.android.com/reference/android/bluetooth/BluetoothClass.Device.html#PHONE_SMART
     * @param btClass btClass object to check the device nature
     * @return true if device is a smart phone, false if not
     */
    private boolean filterDevice(BluetoothClass btClass) {
        return btClass.getDeviceClass() == 524;
    }

    /**
     * This method stops the bluetooth distance computing feature
     */
    public void close() {
        mContext.unregisterReceiver(receiver);
    }
}