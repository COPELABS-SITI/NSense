/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class represents a entry from the location table.
 * @author Luis Amaral Lopes (COPELABS/ULHT)
 */

package cs.usense.pipelines.location;

import android.os.SystemClock;

import cs.usense.utilities.Utils;

public class LocationEntry {

	/** This value is assigned when we can't compute the distance */
	public static final int NA_DISTANCE_VALUE = -1;

	/** Device MAC address */
	private String BSSID;

	private String btMACAddress;

	/** Device Name */
	private String mDeviceName;

	/** Distance to this device */
	private double mDistance = NA_DISTANCE_VALUE;

	/** Last time this entry was updated */
	private long mLastUpdate;

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
	public LocationEntry(String deviceName, String mBSSID, long mLastUpdate, double distance, String btMACAddress) {
		this.mDeviceName = deviceName;
		this.BSSID = mBSSID;
		this.mLastUpdate = mLastUpdate;
		this.mDistance = distance;
		this.btMACAddress = btMACAddress;
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
	public String getBSSID() {
		return BSSID;
	}

	public String getBTMACAddress() {
		return btMACAddress;
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
     * Set the device name 
	 * @param device name
	 */
	public void setDeviceName(String mDeviceName) {
		this.mDeviceName = mDeviceName;
	}
	/**
     * Set the BSSID of this AP
	 * @param bSSID the MAC address to set
	 */
	public void setBSSID(String bSSID) {
		this.BSSID = bSSID;
	}

	public void setBTMACAddress(String btMACAddress) {
		this.btMACAddress = btMACAddress;
	}
	
	/**
     * Set the distance to this device
	 * @param mDistance distance to this device
	 */
	public void setDistance(double mDistance) {
		this.mDistance = mDistance;
	}
	
	/**
     * Set the last update time
	 * @param mLastUpdate last update time
	 */
	public void setLastUpdate(long mLastUpdate) {
		this.mLastUpdate = mLastUpdate;
	}
	
    /**
     * LocationPipeline Entry Constructor
     */
	public LocationEntry() {
		super();
	}

    /**
     * Return a string containing the SSID, BSSID, Distance and the last update.
     */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Device Name: " + this.mDeviceName + "\n");
		sb.append("BSSID: " + this.BSSID + "\n");
		sb.append("Distance: " + this.mDistance + "\n");
		sb.append("Last Update: " + this.mLastUpdate + "\n");
		return sb.toString();
	}
}