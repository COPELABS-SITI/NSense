/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for DataBase module.
 * @author Saeik Firdose (COPELABS/ULHT)
 * @author Luis Lopes (COPELABS/ULHT), 
 * @author Waldir Moreira (COPELABS/ULHT), 
 * @author Reddy Pallavali (COPELABS/ULHT)
 */
package cs.usense.db;

import java.util.ArrayList;
import java.util.List;

import cs.usense.pipelines.motion.MotionEntry;
import cs.usense.pipelines.proximity.BTUserDevAverageEncounterDuration;
import cs.usense.pipelines.proximity.BTUserDevEncounterDuration;
import cs.usense.pipelines.proximity.BTUserDevSocialWeight;
import cs.usense.pipelines.proximity.BTUserDevice;

/**
 * This class provides list of interfaces to construct the database structure 
 */
public interface DataBaseChangeListener {

	/**
	 * This method called when MotionEntry table has been changed in the database
	 * @param actionsEntries List of MotionEntry objects
	 */
	void onDataBaseChangeAcc(List<MotionEntry> actionsEntries);

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

}