/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/11.
 * Class is part of the NSense application.
 */

package cs.usense.pipelines.location;


import android.net.wifi.ScanResult;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cs.usense.db.NSenseDataSource;
import cs.usense.wifi.legacy.WifiLegacyListener;
import cs.usense.wifi.legacy.WifiLegacyListenerManager;
import cs.usense.wifi.p2p.WifiP2pListener;
import cs.usense.wifi.p2p.WifiP2pListenerManager;

import static cs.usense.wifi.p2p.TextRecordKeys.BT_MAC_KEY;
import static cs.usense.wifi.p2p.TextRecordKeys.INTERESTS_KEY;

/**
 * This class receive user's interests and also computes the relative distance
 * toward the other users
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
class RelativePositionWifi implements WifiLegacyListener.ScanResultsAvailable,
        WifiP2pListener.TxtRecordAvailable, WifiP2pListener.ServiceAvailable {

    /** This variable is used to debug WifiAccessPoint class */
    private static final String TAG = "RelativePositionWifi";

    /** This variable represents the WI-FI flag on DB */
    private static final int WIFI_UPDATE_FLAG = 1;

    /** List with all nsense devices found by the WiFi P2P */
    private ArrayList<NSenseDevice> devices = new ArrayList<>();

    /** NSense Data base */
    private NSenseDataSource mDataSource;

    /** Object that contains info from the TXT of the device found. */
    private TxTInfoReceived mTxTInfoReceived;

    RelativePositionWifi(NSenseDataSource dataSource) {
        mDataSource = dataSource;
        WifiP2pListenerManager.registerListener(this);
        WifiLegacyListenerManager.registerListener(this);
    }

    @Override
    public void onServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
        Log.i(TAG, "Received service");
        String deviceNameFixed = deviceNameFix(srcDevice.deviceName);
        int listPosition = searchDeviceOnList(deviceNameFixed, srcDevice.deviceAddress);
        if (listPosition == -1) {
            NSenseDevice nSenseDevice = new NSenseDevice(deviceNameFixed, instanceName, srcDevice.deviceAddress);
            addTxtInfo(nSenseDevice, srcDevice.deviceName);
            Log.i(TAG, "Fill new record with " + nSenseDevice.toString());
            devices.add(nSenseDevice);
            listPosition = devices.size() - 1;
        } else {
            addTxtInfo(devices.get(listPosition), srcDevice.deviceName);
        }
        mDataSource.updateDevice(devices.get(listPosition));
    }

    @Override
    public void onTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        Log.i(TAG, "Received text record");
        if (txtRecordMap != null) {
            mTxTInfoReceived = new TxTInfoReceived();
            mTxTInfoReceived.mDevice = srcDevice;
            String btMac = txtRecordMap.get(BT_MAC_KEY);
            String interests = txtRecordMap.get(INTERESTS_KEY);

            if (btMac != null && !btMac.equals("")) {
                Log.i(TAG, "TXT BTMAC Received: " + btMac);
                mTxTInfoReceived.TXTBTMACAddress = btMac;
            } else {
                Log.e(TAG, "No TXT BTMAC Received.");
            }

            if (interests != null && !interests.equals("")) {
                Log.i(TAG, "TXT Interests Received: " + interests);
                mTxTInfoReceived.interests = interests;
            } else {
                Log.e(TAG, "No TXT Interests Received.");
            }
        }
    }

    /**
     * This method fix the device names. Some devices contains the
     * string "Phone" on it's name's.
     * @param deviceName device name to be fixed
     * @return device name fixed
     */
    private String deviceNameFix(String deviceName) {
        if(deviceName.contains("[Phone]")) {
            deviceName = deviceName.split("\\[.*\\]")[1].trim();
            Log.i(TAG, "Name fixed to " + deviceName);
        }
        return deviceName;
    }

    /**
     * This method search for a device in the list and returns it's index
     * @param deviceName device name to search
     * @param deviceAddress device mac to search
     * @return it's index if exists, if not returns -1
     */
    private int searchDeviceOnList(String deviceName, String deviceAddress) {
        int position = -1;
        for(int i = 0; i < devices.size(); i++) {
            if(deviceName.equalsIgnoreCase(devices.get(i).getDeviceName())) {
                position = i;
                break;
            } else if(deviceAddress.equalsIgnoreCase(devices.get(i).getWifiDirectMac())) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * This method stores txtRecord data received through WI-FI P2P
     * @param nSenseDevice device where the received data will be stored
     * @param deviceName device name to check if the data belongs to this device
     */
    private void addTxtInfo(NSenseDevice nSenseDevice, String deviceName) {
        if (mTxTInfoReceived != null) {
            Log.i(TAG, "Compare: " + mTxTInfoReceived.mDevice.deviceName + " " + deviceName);
            if (mTxTInfoReceived.mDevice.deviceName.equalsIgnoreCase(deviceName)) {
                nSenseDevice.setWifiApMac(mTxTInfoReceived.mDevice.deviceAddress);
                nSenseDevice.setBtMac(mTxTInfoReceived.TXTBTMACAddress);
                nSenseDevice.setInterests(mTxTInfoReceived.interests);
            }
            mTxTInfoReceived = null;
        }
    }

    @Override
    public void onScanResultsAvailable(List<ScanResult> scanResults) {
        Log.i(TAG, "Received scan results");
        for (NSenseDevice nSenseDevice : devices) {
            Log.i(TAG, nSenseDevice.toString());
        }
        Log.i(TAG, "SCAN RESULTS");
        if (!devices.isEmpty()) {
            for (NSenseDevice device : devices) {
                for (ScanResult scanResult : scanResults) {
                    if (checkIfDeviceMatches(device, scanResult)) {
                        Log.i(TAG, "SSID: " + scanResult.SSID + " MAC SCAN: " + scanResult.BSSID + " MAC: " + device.getWifiApMac() + " RSSI: " + scanResult.level);
                        storeDeviceInfo(device, scanResult);
                        break;
                    }
                }
            }
        }
    }

    private void storeDeviceInfo(NSenseDevice device, ScanResult scanResult) {
        device.setWifiApMac(scanResult.BSSID);
        double distance = DistanceModels.logDistancePathLossModel(scanResult.level, -36, -10);
        Log.i(TAG, "Device: " + scanResult.SSID + " RSSI: " + scanResult.level + "dBm - Distance: " + distance);
        saveDistance(device.getDeviceName(), device.getWifiDirectMac(), device.getBtMac(), distance);
    }

    private boolean checkIfDeviceMatches(NSenseDevice device, ScanResult scanResult) {
        boolean found = false;
        Log.i(TAG, "SCAN RESULT: " + scanResult.SSID + " " + scanResult.BSSID + " " + scanResult.level);
        if (scanResult.SSID.equalsIgnoreCase(device.getSsid())) {
            Log.i(TAG, "FOUND AP with the same SSID");
            found = true;
        } else if (scanResult.BSSID.equalsIgnoreCase(device.getWifiApMac())) {
            Log.i(TAG, "FOUND AP with the same MAC ADDRESS");
            found = true;
            if (device.getDeviceName().isEmpty()) {
                device.setDeviceName(device.getSsid().split("-")[2]);
            }
        } else if (device.getDeviceName() != null) {
            if (scanResult.SSID.contains(device.getDeviceName())) {
                Log.i(TAG, "FOUND AP containing the DEVICE NAME in SSID");
                found = true;
            } else if (checkWiFiP2PMacs(scanResult.BSSID, device.getWifiApMac())) {
                Log.i(TAG, "FOUND AP with partial MAC");
                found = true;
            }
        }
        return found;
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
            String[] splitScanMac = scanMac.split(":");
            String[] splitMac = mac.split(":");
            for (int i = 0; i < splitScanMac.length; i++) {
                if (splitScanMac[i].equalsIgnoreCase(splitMac[i])) {
                    checkedParts++;
                }
            }
        }
        return checkedParts > 3;
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

    public void close() {
        WifiP2pListenerManager.unregisterListener(this);
        WifiLegacyListenerManager.unregisterListener(this);
    }

    private class TxTInfoReceived {
        String TXTBTMACAddress = "";
        WifiP2pDevice mDevice = null;
        String interests = "";
    }

}
