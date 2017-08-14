/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.map;

import android.location.Location;

import java.util.ArrayList;

import cs.usense.inferenceModule.SocialDetail;


/**
 * This interface provides some methods that updates the MainActivity
 * @author Saeik Firdose (COPELABS/ULHT),
 * @author Luis Lopes (COPELABS/ULHT),
 * @author Waldir Moreira (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
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