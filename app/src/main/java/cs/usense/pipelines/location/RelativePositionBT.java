package cs.usense.pipelines.location;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import cs.usense.activities.MainActivity;
import cs.usense.db.NSenseDataSource;
import cs.usense.utilities.DateUtils;
import cs.usense.utilities.Utils;

class RelativePositionBT {

    private final String TAG = "RelativePositionBT";

    // Free Space Path Loss (FSPL) Constants (see above)
    private static final int FSPL_FREQ = 189;
    private static final int FSPL_LIGHT = -148;

    private static final int DEFAULT_TX_POWER_LEVEL = -36;

    private NSenseDataSource dataSource;

    private LocationPipeline callback;

    /* (dBm) PATH_LOSS for isotropic antenna transmitting BLE (2.45 GHz) */
    private static final int FREE_SPACE_PATH_LOSS_CONSTANT_FOR_BLE = FSPL_FREQ + FSPL_LIGHT; // const = 41

    RelativePositionBT(Context context, NSenseDataSource dataSource, LocationPipeline callback) {
        this.dataSource = dataSource;
        this.callback = callback;
        context.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    BluetoothDevice mBTdevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    BluetoothClass btClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);


                    if (!filterDevice(btClass)) {
                        Log.i(TAG, "Device of no interest");
                        return;
                    }
                    double mDistance = distanceFromRssi(rssi, DEFAULT_TX_POWER_LEVEL);

                    Utils.appendLogs("BTDistance",
                            new String[]{
                                    DateUtils.getTimeNowAsStringSecond(),
                                    String.valueOf(rssi),
                                    String.valueOf(2450),
                                    String.valueOf(mDistance),
                                    //MainActivity.expectedDistance,
                                    mBTdevice.getName()
                            });

                    if (dataSource.hasLocationEntry(mBTdevice.getAddress(), mBTdevice.getName())) {
                        //Device exists
                        LocationEntry entry = dataSource.getLocationEntry(mBTdevice.getAddress(), mBTdevice.getName());
                        entry.setDistance(mDistance);
                        entry.setLastUpdate(SystemClock.elapsedRealtime());
                        dataSource.updateLocationEntry(entry);
                    } else {
                        //New Device
                        LocationEntry entry = new LocationEntry();
                        entry.setDeviceName(mBTdevice.getName());
                        entry.setBSSID(mBTdevice.getAddress());
                        entry.setDistance(mDistance);
                        entry.setLastUpdate(SystemClock.elapsedRealtime());
                        dataSource.registerLocationEntry(entry);
                    }
                    Log.i(TAG, " Device: " + mBTdevice.getName() + " RSSI: " + rssi + "dBm - Distance: " + mDistance);
                }
            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.e(TAG, e.getMessage());
                Utils.appendLogs("Error" + DateUtils.getTimeNowFileNameFormatted(), errors.toString());
            }
        }
    };

    public void close(Context context) {
        context.unregisterReceiver(receiver);
    }

    /**
     * Convert RSSI to distance using the free space path loss equation. See <a
     * href="http://en.wikipedia.org/wiki/Free-space_path_loss">Free-space_path_loss</a>
     *
     * @param rssi Received Signal Strength Indication (RSSI) in dBm
     * @param txPowerAtSource the calibrated power of the transmitter (dBm) at 0 meter
     * @return the distance at which that rssi value would occur in meters
     */
    private double distanceFromRssi(int rssi, int txPowerAtSource) {
        int pathLoss = txPowerAtSource - rssi;
        return Math.pow(10, (pathLoss - FREE_SPACE_PATH_LOSS_CONSTANT_FOR_BLE) / 20.0) * 2;
    }

    private boolean filterDevice(BluetoothClass btClass) {
        return btClass.getDeviceClass() == 524 || btClass.getDeviceClass() == 268;
    }
}