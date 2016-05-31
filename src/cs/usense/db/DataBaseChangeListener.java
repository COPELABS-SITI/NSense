/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. It provides support for DataBase module.
 * @author Saeik Firdose (COPELABS/ULHT)
 * @author Luis Lopes (COPELABS/ULHT), 
 * @author Waldir Moreira (COPELABS/ULHT), 
 * @author Reddy Pallavali (COPELABS/ULHT)
 */
package cs.usense.db;

import java.util.ArrayList;
import java.util.List;

import cs.usense.accelerometer.ActionsEntry;
import cs.usense.bluetooth.BTUserDevAverageEncounterDuration;
import cs.usense.bluetooth.BTUserDevEncounterDuration;
import cs.usense.bluetooth.BTUserDevSocialWeight;
import cs.usense.bluetooth.BTUserDevice;
import cs.usense.location.LocationEntry;
import cs.usense.microphone.SoundLevel;

/**
 * This class provides list of interfaces to construct the database structure 
 */
public interface DataBaseChangeListener {

	/**
	 * This method called when ActionEntry table has been changed in the database 
	 * @param actionsEntries List of ActionsEntry objects
	 */
	void onDataBaseChangeAcc(List<ActionsEntry> actionsEntries);

	/**
	 * This method called when BTUserDevice table has been changed in the database
	 * @param arrayList List of BTUserDevice objects
	 */
	void onDataBaseChangeBT(ArrayList<BTUserDevice> arrayList);

	/**
	 * This method called when BTUserDevEncounterDuration table has been changed in the database
	 * @param arrayList List of BTUserDevEncounterDuration objects
	 */
	void onDataBaseChangeBTEncDur(ArrayList<BTUserDevEncounterDuration> arrayList);

	/**
	 * This method called when BTUserDevAverageEncounterDuration table has been changed in the database
	 * @param arrayList List of BTUserDevAverageEncounterDuration objects
	 */
	void onDataBaseChangeBTAvgEncDur(ArrayList<BTUserDevAverageEncounterDuration> arrayList);

	/**
	 * This method called when BTUserDevSocialWeight table has been changed in the database
	 * @param arrayList List of BTUserDevSocialWeight objects
	 */
	void onDataBaseChangeBTSocialWeight(ArrayList<BTUserDevSocialWeight> arrayList);	

	/**
	 * This method called when LocationEntry table has been changed in the database
	 * @param mLocationEntries List of LocationEntry objects
	 */
	void onDataBaseChangeLocation(List<LocationEntry> mLocationEntries);

	/**
	 * This method called when SoundLevel table has been changed in the database
	 * @param mLocationEntries List of SoundLevel objects
	 */
	void onSoundLevelChange(ArrayList<SoundLevel> arrayList);
}