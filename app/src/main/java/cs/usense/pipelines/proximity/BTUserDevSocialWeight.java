/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/04/06.
 * Class is part of the NSense application. It provides support for proximity pipeline.
 */


package cs.usense.pipelines.proximity;


/**
 * This class holds the social weight between a peer and the user device in specific time slots.
 * The information kept are device MAC address and social weight.
 * @author Waldir Moreira (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class BTUserDevSocialWeight {
	
	/** This variable is to get the address of device */
	private String deviceAdd;
	
	/** This variable is to get the social weight in specific time slots */
	private double[] socialWeight = new double [24];
	
	/**
     * This method gets the MAC address of this Bluetooth device.
	 * @return deviceAdd The device address to be used as key.
	 */
	public String getDevAdd() {
		return deviceAdd;
	}
	
	/**
     * This method gets the social weight towards a BT device.
	 * @param timeSlot The specific time slot.
	 * @return The social weight in the given slot. 
	 */
	public double getSocialWeight(int timeSlot) {
		return socialWeight[timeSlot];
	}
	
	/**
	 * This method sets the ID of this Bluetooth device.
	 * @param DevAdd The MAC address of device to set.
	 */
	public void setDevAdd(String DevAdd) {
		deviceAdd = DevAdd;
	}
	
	/**
     * This method sets the social weight towards a BT device.
	 * @param timeSlot The specific time slot.
	 * @param socialWeight The social weight.
	 */
	public void setSocialWeight(int timeSlot, double socialWeight) {
		this.socialWeight[timeSlot] = socialWeight;
	}

}