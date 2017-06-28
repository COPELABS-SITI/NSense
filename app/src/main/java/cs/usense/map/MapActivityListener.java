/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class provides an interface to update pipeline information and 
 * provides the information to NSenseService class, and MainActivity class.
 * @author Saeik Firdose (COPELABS/ULHT),
 * @author Luis Lopes (COPELABS/ULHT), 
 * @author Waldir Moreira (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.map;

import android.location.Location;

import java.util.ArrayList;

import cs.usense.inferenceModule.SocialDetail;

/**
 * This class provides an interface to update pipeline information 
 */
public interface MapActivityListener {

	/**
	 * This method is called when Location change
	 * @param location user's location
	 */
	void onLocationChange(Location location);

	/**
	 * This method called when SocialDetail information has been changed
	 * @param socialInformation List of SocialDetail objects
	 */
	void onSociabilityChange(ArrayList<SocialDetail> socialInformation);
}