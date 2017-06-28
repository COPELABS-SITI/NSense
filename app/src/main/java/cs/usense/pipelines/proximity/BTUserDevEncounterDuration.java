/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the NSense application.
 *  This class holds the encounter duration between a peer and the user device in specific time slots.
 *  The information kept are device MAC address and time spent in the vicinity (i.e., total time within communication range).
 * @author Waldir Moreira (COPELABS/ULHT)
 */

package cs.usense.pipelines.proximity;

/**
 * This class provide details of Bluetooth User Device and  Encounter Duration
 * */
public class BTUserDevEncounterDuration {
	
	/** This variable is to get the address of device */
	private String deviceAdd;
	
	/** This variable is to get the device encounter duration in specific time slots */
	private double[] encounterDuration = new double [24];
	
	
	/**
     * This method gets the MAC address of this Bluetooth device.
	 * @return deviceAdd The device address to be used as key.
	 */
	public String getDevAdd() {
		return deviceAdd;
	}
	
	/**
     * This method gets the duration that the BT device is within communication range.
	 * @param timeSlot The specific time slot.
	 * @return The encounter duration in the given slot.
	 */
	public double getEncounterDuration(int timeSlot) {
		return encounterDuration[timeSlot];
	}
	
	/**
	 * This method sets the ID of this Bluetooth device.
	 * @param DevAdd The MAC address of device to set.
	 */
	public void setDevAdd(String DevAdd) {
		deviceAdd = DevAdd;
	}
	
	/**
     * This method sets the duration that the BT device is within communication range.
	 * @param timeSlot The specific time slot.
	 * @param encounterDuration The duration of encounter.
	 */
	public void setEncounterDuration(int timeSlot, double encounterDuration) {
		this.encounterDuration[timeSlot] = encounterDuration;
	}

	/**
	 * This method returns the avg of encounter duration
	 * @return avg of encounter duration
     */
	public double getAvgEncounterDuration(int hoursRunning) {
		double result = 0.0;
		for(double value : encounterDuration) {
				result += value;
		}
		return result == 0.0 ? 0.0 : result / hoursRunning + 1;
	}

}