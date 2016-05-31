/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. This class provides an interface to update pipeline information and 
 * provides the information to USenseService class, and USenseActivity class.
 * @author Saeik Firdose (COPELABS/ULHT),
 * @author Luis Lopes (COPELABS/ULHT), 
 * @author Waldir Moreira (COPELABS/ULHT)
 */
package cs.usense;

import java.util.ArrayList;
import java.util.List;

import cs.usense.bluetooth.BluetoothCore.socialWeight;
import cs.usense.inferenceModule.SocialityDetails;
import cs.usense.location.LocationEntry;

/**
 * This class provides an interface to update pipeline information 
 */
public interface ChangeListener {
	/**
	 * This method called when Location Outdoor table has been changed in the database 
	 * @param newMessage Out door location information
	 */
	void onLocationOutdoorChange(String newMessage);
	
	/**
	 * This method called when Location Indoor table has been changed in the database 
	 * @param apEntries List of LocationEntry objects
	 */
	void onLocationIndoorChange(List<LocationEntry> apEntries);
	
	/**
	 * This method called when Action information has been changed 
	 * @param newMessage Action Type information
	 */
	void onActionChange(String newMessage);
	
	/**
	 * This method called when social Weight table has been changed
	 * @param arrayList List of socialWeight objects
	 */
	void onSocialWeightChange(ArrayList<socialWeight> arrayList);
	
	/**
	 * This method called when Sound Level has been changed  
	 * @param newSound Sound level information
	 */
	void onSoundLevelChange(String newSound);
	
	/**
	 * This method called when Social Interaction information has been changed
	 * @param socialInt Social Interaction information
	 */
	void onSocialInteractionChange(double socialInt);
	
	/**
	 * This method called when Propinquity information has been changed 
	 * @param propIn Propinquity information
	 */
	void onPropinquityChange(double propIn);

	/**
	 * This method called when SocialityDetails information has been changed
	 * @param newSocial List of SocialityDetails objects
	 */
	void onGetSociabilityChange(ArrayList<SocialityDetails> newSocial);
}