/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the NSense application.
 *  This class holds the average duration of encounter between a peer and the user device in specific time slots.
 *  The information kept are device MAC address and average encounter duration.
 * @author Waldir Moreira (COPELABS/ULHT)
 */
package cs.usense.bluetooth;

/**
 * This class provides the average duration of encounter between a peer and the user device in specific time slots
 * @author Waldir Moreira (COPELABS/ULHT)
 *
 */
public class BTUserDevAverageEncounterDuration {
	
	/** This variable is to get the address of device */
	private String deviceAdd;
	
	/** This variable is to get the device average duration in specific time slots */
	private double[] averageEncounterDuration = new double [24];
	
	
	/**
     * This method gets the MAC address of this Bluetooth device.
	 * @return deviceAdd The device address to be used as key.
	 */
	public String getDevAdd() {
		return deviceAdd;
	}
	
	/**
     * This method gets the average encounter duration that the BT device is within communication range.
	 * @param timeSlot The specific time slot.
	 * @return The average encounter duration in the given slot.
	 */
	public double getAverageEncounterDuration(int timeSlot) {
		return averageEncounterDuration[timeSlot];
	}
	
	/**
	 * This method sets the ID of this Bluetooth device.
	 * @param DevAdd The MAC address of device to set.
	 */
	public void setDevAdd(String DevAdd) {
		deviceAdd = DevAdd;
	}
	
	/**
     * This method sets the average duration that the BT device is within communication range.
	 * @param timeSlot The specific time slot.
	 * @param avgEncounterDuration The average duration of encounter.
	 */
	public void setAverageEncounterDuration(int timeSlot, double avgEncounterDuration) {
		this.averageEncounterDuration[timeSlot] = avgEncounterDuration;
	}

    /**
     * BT User Device Average Encounter Duration Constructor
     */
	public BTUserDevAverageEncounterDuration() {
		super();
	}

}
