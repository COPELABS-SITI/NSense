/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the USense application.
 *  This class represents a user device found by means of Bluetooth.
 *  The information kept are device name, MAC address, and time of first encounter.
 * @author Waldir Moreira (COPELABS/ULHT)
 */

package cs.usense.bluetooth;

/**
 * This class Provide details of User device name, MAC address, and time of first encounter.
 * */
public class BTUserDevice {

	/** This variable is to get the address of device */
	private String deviceAdd;
	
	/** This variable is to get the name of device */
	private String deviceName;

	/** This variable is to get the encounter time of device */
	private long encounterTime;
	
	/**
     * This method gets the MAC address of this Bluetooth device.
	 * @return deviceAdd The device address.
	 */
	public String getDevAdd() {
		return deviceAdd;
	}
	
	/**
     * This method gets the name of this Bluetooth device.
	 * @return deviceName The device name.
	 */
	public String getDevName() {
		return deviceName;
	}

	/**
     * This method gets the time that the BT device is is first found.
	 * @param encounterTime The time.
	 */
	public long getEncounterStart() {
		return encounterTime;
	}
	
	/**
	 * This method sets the ID of this Bluetooth device.
	 * @param DevAdd The MAC address of device to set.
	 */
	public void setDevAdd(String DevAdd) {
		deviceAdd = DevAdd;
	}
	
	/**
	 * This method sets the name of this Bluetooth device.
	 * @param DevName The name of device to set.
	 */
	public void setDevName(String DevName) {
		deviceName = DevName;
	}
	
	/**
     * This method sets the time that the BT device is first found.
	 * @param time The time.
	 */
	public void setEncounterTime(long time) {
		this.encounterTime = time;
	}
	
    /**
     * Bluetooth User Device Constructor
     */
	public BTUserDevice() {
		super();
	}
}



