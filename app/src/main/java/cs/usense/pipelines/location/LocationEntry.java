/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/16.
 * Class is part of the NSense application. It provides support for location pipeline.
 */

package cs.usense.pipelines.location;

import android.os.SystemClock;

import cs.usense.utilities.Utils;


/**
 * This class represents an entry from the location table.
 * @author Luis Amaral Lopes (COPELABS/ULHT)
 * @version 1.0, 2015
 */
public class LocationEntry {

	/** This value is assigned when we can't compute the distance */
	public static final int NA_DISTANCE_VALUE = -1;

	/** Device Wifi MAC address */
	private String mBssid;

	/** Device BT MAC address */
	private String mBtMac;

	/** Device Name */
	private String mDeviceName = "NA";

	/** Distance to this device */
	private double mDistance = NA_DISTANCE_VALUE;

	/** Last time this entry was updated */
	private long mLastUpdate;

	/** This variable contains the flag which represents if the distance was updated by BT or WIFI */ 
	private int mUpdateFlag;

	/**
	 * LocationPipeline Entry Constructor
	 */
	public LocationEntry(String deviceName, String mBSSID) {
		this(deviceName, mBSSID, SystemClock.elapsedRealtime(), NA_DISTANCE_VALUE, Utils.EMPTY_STRING);
	}

	/**
	 * LocationPipeline Entry Constructor
	 */
	public LocationEntry(String deviceName, String mBSSID, double distance, String btMACAddress) {
		this(deviceName, mBSSID, SystemClock.elapsedRealtime(), distance, btMACAddress);
	}

	/**
	 * LocationPipeline Entry Constructor
	 */
	public LocationEntry(String deviceName, String bssid, long lastUpdate, double distance, String btMac) {
		mDeviceName = deviceName;
		mBssid = bssid;
		mLastUpdate = lastUpdate;
		mDistance = distance;
		mBtMac = btMac;
	}

	/**
	 * LocationPipeline Entry Constructor
	 */
	public LocationEntry(String deviceName, String bssid, long lastUpdate, double distance, String btMac, int flag) {
		mDeviceName = deviceName;
		mBssid = bssid;
		mLastUpdate = lastUpdate;
		mDistance = distance;
		mBtMac = btMac;
		mUpdateFlag = flag;
	}
	
	/**
     * Get the device name
	 * @return mDeviceName - The device name. 
	 */
	public String getDeviceName() {
		return mDeviceName;
	}
	/**
     * Get the BSSID
	 * @return the BSSID - The device's MAC address.
	 */
	public String getBssid() {
		return mBssid;
	}

	/**
	 * Get the bt mac
	 * @return mBtMac - bt mac
	 */
	public String getBtMac() {
		return mBtMac;
	}
	
	/**
     * Get the distance to this device
	 * @return mDistance - Distance to this device.
	 */
	public double getDistance() {
		return mDistance;
	}
	
	/**
     * Get the time of last update
	 * @return mLastUpdate - Last time this entry was updated
	 */
	public long getLastUpdate() {
		return mLastUpdate;
	}

	/**
	 * This method checks if the current state of mUpdateFlag is equals to updateFlag
	 * @param updateFlag update flag, can be BT (0) or WIFI (1)
	 * @return true if the current flag is the same of updateFlag, false if not
	 */
	public boolean checkFlagUpdate(int updateFlag) {
		return mUpdateFlag == updateFlag;
	}

	/**
     * Set the device name 
	 * @param deviceName name
	 */
	public void setDeviceName(String deviceName) {
		mDeviceName = deviceName;
	}

	/**
     * Set the BSSID of this AP
	 * @param bssid the MAC address to set
	 */
	public void setBssid(String bssid) {
		mBssid = bssid;
	}

	/**
	 * Set the BT MAC  of this AP
	 * @param btMac the BT MAC address to set
	 */
	public void setBTMACAddress(String btMac) {
		mBtMac = btMac;
	}
	
	/**
     * Set the distance to this device
	 * if mDistance is NA_DISTANCE_VALUE then is value will be distance
	 * if not will be 40% of mDistance (previous one) and 60% of distance (current one)
	 * @param distance distance to this device
	 */
	public void setDistance(double distance) {
		mDistance = mDistance == NA_DISTANCE_VALUE ? distance : mDistance * 0.4 + distance * 0.6;
	}
	
	/**
     * Set the last update time
	 * @param lastUpdate last update time
	 */
	public void setLastUpdate(long lastUpdate) {
		mLastUpdate = lastUpdate;
	}

    /**
     * Return a string containing the SSID, BSSID, Distance and the last update.
     */
    @Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Device Name: " + mDeviceName + "\n");
		sb.append("BSSID: " + mBssid + "\n");
		sb.append("Distance: " + mDistance + "\n");
		sb.append("Last Update: " + mLastUpdate + "\n");
		return sb.toString();
	}
}