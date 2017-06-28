/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the NSense application.
 *  This class represents a user device found by means of Bluetooth.
 *  The information kept are device name, MAC address, and time of first encounter.
 * @author Waldir Moreira (COPELABS/ULHT)
 */

package cs.usense.pipelines.proximity;

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

	private String interests;

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
	 * @return encounterTime The time.
	 */
	public long getEncounterStart() {
		return encounterTime;
	}

	/**
	 * This method gets the user's interests.
	 * @return interests.
	 */
	public String getInterests() {
		return interests;
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
	 * This method sets the user's interests.
	 * @param interests
	 */
	public void setInterests(String interests) {
		this.interests = interests;
	}
}


