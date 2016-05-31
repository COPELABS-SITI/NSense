/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the USense application.
 * This class represents a entry from the location table.
 * @author Luis Amaral Lopes (COPELABS/ULHT)
 */

package cs.usense.location;

public class LocationEntry {

	/** Device MAC address */
	private String BSSID;
	/** Device Name */
	private String mDeviceName;
	/** Distance to this device */
	private double mDistance;
	/** Last time this entry was updated */
	private long mLastUpdate;
	
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
     * Location Entry Constructor
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