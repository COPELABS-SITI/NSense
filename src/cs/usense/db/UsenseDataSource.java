/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. This class support for DataBase module and 
 * it provides various methods to access the DB from external modules.
 * @author Saeik Firdose (COPELABS/ULHT), 
 * @author Luis Lopes (COPELABS/ULHT), 
 * @author Waldir Moreira (COPELABS/ULHT), 
 * @author Reddy Pallavali (COPELABS/ULHT)
 */
package cs.usense.db;

import java.util.Map;
import java.util.TreeMap;

import cs.usense.accelerometer.ActionsEntry;
import cs.usense.bluetooth.BTUserDevAverageEncounterDuration;
import cs.usense.bluetooth.BTUserDevEncounterDuration;
import cs.usense.bluetooth.BTUserDevSocialWeight;
import cs.usense.bluetooth.BTUserDevice;
import cs.usense.db.UsenseSQLiteHelper;
import cs.usense.location.LocationEntry;
import cs.usense.microphone.SoundLevel;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class provides various methods and information of the USense Data base. 
 * Methods include, open database, close database, access to the tables
 */
public class UsenseDataSource {

	/**
	 * COMMON OPERATIONS
	 */
	private SQLiteDatabase db;
	private UsenseSQLiteHelper dbHelper;
	private boolean isDbOpen;

	/**
	 * This method Construct the context. which takes Android Context as input.
	 * @param context Interface to global information about an application environment.
	 */
	public UsenseDataSource (Context context) {
		dbHelper = new UsenseSQLiteHelper(context);
		isDbOpen = false;		
	}


	/**
	 * This method opens the predefined USense database.
	 * @param writable The first time this is called, the database will be opened.
	 * @throws SQLException Throws SQL Exception
	 */
	public void openDB(boolean writable) throws SQLException {
		if (!isDbOpen) {
			if (writable)
				db = dbHelper.getWritableDatabase();
			else
				db = dbHelper.getReadableDatabase();
		}
	}

	/**
	 * This method close the predefined USense database.
	 */
	public void closeDB() {
		dbHelper.close();
		isDbOpen = false;
	}


	/**
	 * This method gets the number of records in the ACCESS_POINTS table. This is, the number of AP registered on the application.
	 * @return number of AP registered by the application.
	 */
	public long getNumAP(){
		return DatabaseUtils.queryNumEntries(db, UsenseSQLiteHelper.TABLE_ACCESSPOINTS);
	}


	/**
	 * ACTIONS TABLE: This table stores information
	 * regarding ACTIONS.
	 * The information stored is:
	 * 		- ACTIONTYPE 
	 * 		- TIMESTAMP 
	 * 		- DURATION
	 * 		- LASTACTION
	 * 		- ACTIONTIMEGAP
	 * 		- ACTIONCOUNTER
	 */

	/**
	 * List of all columns on the ACCESS_POINTS table.
	 */
	private String[] allColumnsActionsEntry = { 
			UsenseSQLiteHelper.COLUMN_ACTIONTYPE,
			UsenseSQLiteHelper.COLUMN_TIMESTAMP,
			UsenseSQLiteHelper.COLUMN_ENDTIMESTAMP,
			UsenseSQLiteHelper.COLUMN_AVGDURATION,
			UsenseSQLiteHelper.COLUMN_HOUR,
			UsenseSQLiteHelper.COLUMN_ACTIONCOUNTER,
			UsenseSQLiteHelper.COLUMN_DAY,
			UsenseSQLiteHelper.COLUMN_TIMEFRAME
	};

	/**
	 * This method converts a cursor pointing to a record in the ACTIONS table to a ActionsEntry object.
	 * @param cursor Cursor pointing to a record of the ACTIONS table.
	 * @return ae the ActionsEntry object
	 */
	private ActionsEntry cursorToActions(Cursor cursor) {
		ActionsEntry ae = new ActionsEntry();
		ae.setActionType(cursor.getString(0));
		ae.setActionStartTime(cursor.getLong(1));
		ae.setActionEndTime(cursor.getLong(2));
		ae.setAverageDuration(cursor.getDouble(3));
		ae.setHour(cursor.getInt(4));
		ae.setActionCounter(cursor.getInt(5));
		ae.setDay(cursor.getLong(6));
		ae.setTimeFrame(cursor.getString(7));
		return ae;
	}

	/**
	 * This method gets the number of records in the ACTION_ENTRY table. This is, the number of Actions registered on the application.
	 * @return the number of Actions registered by the application.
	 */
	public long getNumActionEntry(){
		return DatabaseUtils.queryNumEntries(db, UsenseSQLiteHelper.TABLE_ACTIONS);
	}

	/**
	 * This method register a new ActionEntry in the application. 
	 * It creates a new record on the Action Entry table, with the information passed as ActionEntry.
	 * @param ae Action enty information.
	 * @return the action type of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewActionEntry (ActionsEntry ae) {
		ContentValues values = new ContentValues();
		values.put(UsenseSQLiteHelper.COLUMN_ACTIONTYPE, ae.getActionType());
		values.put(UsenseSQLiteHelper.COLUMN_TIMESTAMP, ae.getActionStartTime());
		values.put(UsenseSQLiteHelper.COLUMN_ENDTIMESTAMP, ae.getActionEndTime());
		values.put(UsenseSQLiteHelper.COLUMN_AVGDURATION, ae.getAverageDuration());
		values.put(UsenseSQLiteHelper.COLUMN_HOUR, ae.getHour());
		values.put(UsenseSQLiteHelper.COLUMN_ACTIONCOUNTER, ae.getActionCounter());
		values.put(UsenseSQLiteHelper.COLUMN_DAY, ae.getDay());
		values.put(UsenseSQLiteHelper.COLUMN_TIMEFRAME, ae.getTimeFrame());

		return db.insert(UsenseSQLiteHelper.TABLE_ACTIONS, null, values);
	}

	/**
	 * This method update an ActionEntry already registered by the application. 
	 * This modifies the corresponding record to the Entry in the ACTIONENTRY table.
	 * @param ap action entry information.
	 * @return true if successful.
	 */
	public boolean updateActionEntry(ActionsEntry ae) {
		String identifier = UsenseSQLiteHelper.COLUMN_TIMESTAMP + "='" + ae.getActionStartTime() + "'";
		ContentValues values = new ContentValues();
		values.put(UsenseSQLiteHelper.COLUMN_ACTIONTYPE, ae.getActionType());
		values.put(UsenseSQLiteHelper.COLUMN_TIMESTAMP, ae.getActionStartTime());
		values.put(UsenseSQLiteHelper.COLUMN_ENDTIMESTAMP, ae.getActionEndTime());
		values.put(UsenseSQLiteHelper.COLUMN_AVGDURATION, ae.getAverageDuration());
		values.put(UsenseSQLiteHelper.COLUMN_HOUR, ae.getHour());
		values.put(UsenseSQLiteHelper.COLUMN_ACTIONCOUNTER, ae.getActionCounter());
		values.put(UsenseSQLiteHelper.COLUMN_DAY, ae.getDay());
		values.put(UsenseSQLiteHelper.COLUMN_TIMEFRAME, ae.getTimeFrame());

		int rows = db.update(UsenseSQLiteHelper.TABLE_ACTIONS, values, identifier, null);

		return ((rows != 0)? true : false);
	}

	/**
	 * This method gets an Actions already registered by the application.
	 * @param actionentry Actions which information should be returned
	 * @return ae the ActionsEntry object, null if not found.
	 */
	public ActionsEntry getActionEntry( String actionentry) {
		ActionsEntry ae;
		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_ACTIONS, allColumnsActionsEntry, UsenseSQLiteHelper.COLUMN_ACTIONTYPE + "='" + actionentry + "'", null, null, null, null);
		if (cursor.moveToFirst())
			ae = cursorToActions(cursor);
		else
			ae = null;	

		cursor.close();
		return ae;
	}


	/**
	 * This method checks if a given Actions has already been registered by the application.
	 * @param actionType Actions of the Actions
	 * @return true if Actions has already been registered by the application, false otherwise.
	 */
	public boolean hasActionEntry (String actionType) {
		return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + UsenseSQLiteHelper.TABLE_ACTIONS + " WHERE " + UsenseSQLiteHelper.COLUMN_ACTIONTYPE + "='" + actionType + "'", null) == 0)? false : true;
	}

	/**
	 * This method gets the all the Actions recorded by the application on the ACCESS_POINTS table.
	 * @return aeMap A map with the Actions objects, and the actiontype as key.
	 */
	public Map<String, ActionsEntry> getAllActionEntry() {
		Map<String, ActionsEntry> aeMap = new TreeMap<String, ActionsEntry>();

		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_ACTIONS, allColumnsActionsEntry, null, null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			ActionsEntry ae = cursorToActions(cursor);
			aeMap.put(ae.getActionType(), ae);
			cursor.moveToNext();
		}

		cursor.close();
		return aeMap;
	}

	/** Bluetooth pipeline */
	/**
	 * BTDEVICE TABLE: This table stores information
	 * regarding BTDEVICES.
	 * The information stored is:
	 * 		- BTDEV_MAC_ADDRESS 
	 * 		- BTDEV_NAME 
	 * 		- BTDEV_ENCOUNTERTIME
	 * 
	 * BTDEVICEENCOUNTERDURATION TABLE: This table stores information
	 * regarding the duration that found BTDEVICES are within communication range.
	 * The information stored is:
	 * 		- BTDEV_MAC_ADDRESS 
	 * 		- BTDEV_ENCOUNTERDURATION
	 * 
	 * BTDEVICEAVERAGEENCOUNTERDURATION TABLE: This table stores information
	 * regarding the average duration of encounters towards found BTDEVICES.
	 * The information stored is:
	 * 		- BTDEV_MAC_ADDRESS 
	 * 		- BTDEV_AVGENCOUNTERDURATION		 
	 * 
	 * BTDEVICESOCIALWEIGHT TABLE: This table stores information
	 * regarding the social weight towards found BTDEVICES.
	 * The information stored is:
	 * 		- BTDEV_MAC_ADDRESS 
	 * 		- BTDEV_SOCIALWEIGHT		 
	 * 
	 */

	/**
	 * List of all columns on the BTDEVICE table.
	 */
	private String[] allColumnsBTDevices = { 
			UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
			UsenseSQLiteHelper.COLUMN_BTDEV_NAME,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERSTART,
	};

	/**
	 * List of all columns on the BTDEVICEENCOUNTERDURATION table.
	 */
	private String[] allColumnsBTDeviceEncounterDuration = { 
			UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23,
			UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24,
	};

	/**
	 * List of all columns on the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 */
	private String[] allColumnsBTDeviceAverageEncounterDuration = { 
			UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23,
			UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24,
	};

	/**
	 * List of all columns on the BTDEVICESOCIALWEIGHT table.
	 */
	private String[] allColumnsBTDeviceSocialWeight = { 
			UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT1,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT2,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT3,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT4,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT5,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT6,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT7,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT8,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT9,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT10,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT11,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT12,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT13,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT14,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT15,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT16,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT17,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT18,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT19,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT20,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT21,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT22,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT23,
			UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT24,
	};

	/**
	 * This method converts a cursor pointing to a record in the 
	 * BTDEVICE table to a BTDevice object.
	 * @param cursor The cursor pointing to a record of the BTDEVICE table.
	 * @return btDev The BTDevice object
	 */
	private BTUserDevice cursorToBTDevice(Cursor cursor) {
		BTUserDevice btDev = new BTUserDevice();

		btDev.setDevAdd(cursor.getString(0));
		btDev.setDevName(cursor.getString(1));
		btDev.setEncounterTime(cursor.getLong(2));

		return btDev;
	}

	/**
	 * This method converts a cursor pointing to a record in the 
	 * BTDEVICEENCOUNTERDURATION table to a BTUserDevEncounterDuration object.
	 * @param cursor The cursor pointing to a record of the BTDEVICEENCOUNTERDURATION table.
	 * @return duration The BTUserDevEncounterDuration object.
	 */
	private BTUserDevEncounterDuration cursorToBTDevEncounterDuration(Cursor cursor) {
		BTUserDevEncounterDuration duration = new BTUserDevEncounterDuration();

		duration.setDevAdd(cursor.getString(0));

		for(int timeSlot = 0; timeSlot < 24; timeSlot++){
			duration.setEncounterDuration(timeSlot, cursor.getDouble(timeSlot+1)); 
		}
		return duration;
	}

	/**
	 * This method converts a cursor pointing to a record in the 
	 * BTDEVICEAVERAGEENCOUNTERDURATION table to a BTUserDevAverageEncounterDuration object.
	 * @param cursor The cursor pointing to a record of the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @return averageDuration The BTUserDevAverageEncounterDuration object.
	 */
	private BTUserDevAverageEncounterDuration cursorToBTDevAverageEncounterDuration(Cursor cursor) {
		BTUserDevAverageEncounterDuration averageDuration = new BTUserDevAverageEncounterDuration();

		averageDuration.setDevAdd(cursor.getString(0));

		for(int timeSlot = 0; timeSlot < 24; timeSlot++){
			averageDuration.setAverageEncounterDuration(timeSlot, cursor.getDouble(timeSlot+1)); 
		}
		return averageDuration;
	}

	/**
	 * This method converts a cursor pointing to a record in the 
	 * BTDEVICESOCIALWEIGHT table to a BTUserDevSocialWeight object.
	 * @param cursor The cursor pointing to a record of the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @return socialWeight The BTUserDevSocialWeight object.
	 */
	private BTUserDevSocialWeight cursorToBTDevSocialWeight(Cursor cursor) {
		BTUserDevSocialWeight socialWeight = new BTUserDevSocialWeight();

		socialWeight.setDevAdd(cursor.getString(0));

		for(int timeSlot = 0; timeSlot < 24; timeSlot++){
			socialWeight.setSocialWeight(timeSlot, cursor.getDouble(timeSlot+1)); 
		}
		return socialWeight;
	}


	/**
	 * This method gets the number of records in the BTDEVICE table. 
	 * This is, the number of BTDevice registered on the application.
	 * @return The number of BTDevice registered by the application.
	 */
	public long getNumBTDevice(){
		return DatabaseUtils.queryNumEntries(db, UsenseSQLiteHelper.TABLE_BTDEVICE);
	}

	/**
	 * This method registers a new BTDevice in the application. 
	 * It creates a new record on the BTDEVICE, BTDEVICEENCOUNTERDURATION, 
	 * BTDEVICEAVERAGEENCOUNTERDURATION, and BTDEVICESOCIALWEIGHT tables,
	 * with the information passed as BTDevice.
	 * @param btDev The Bluetooth device information.
	 * @param duration The Bluetooth device information regarding the duration that the BT device is within communication range of others.
	 * @param averageDuration The Bluetooth device information regarding the average duration of encounter between the BT device and other devices.
	 * @param socialWeight The Bluetooth device information regarding the social weight of the BT device towards others.
	 */
	public void registerNewBTDevice (BTUserDevice btDev, BTUserDevEncounterDuration duration, 
			BTUserDevAverageEncounterDuration averageDuration, BTUserDevSocialWeight socialWeight) {

		ContentValues values = new ContentValues();
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, btDev.getDevAdd());
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_NAME, btDev.getDevName());
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERSTART, btDev.getEncounterStart());
		db.insert(UsenseSQLiteHelper.TABLE_BTDEVICE, null, values);

		values = new ContentValues();
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, duration.getDevAdd());
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1, duration.getEncounterDuration(0));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2, duration.getEncounterDuration(1));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3, duration.getEncounterDuration(2));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4, duration.getEncounterDuration(3));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5, duration.getEncounterDuration(4));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6, duration.getEncounterDuration(5));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7, duration.getEncounterDuration(6));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8, duration.getEncounterDuration(7));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9, duration.getEncounterDuration(8));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10, duration.getEncounterDuration(9));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11, duration.getEncounterDuration(10));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12, duration.getEncounterDuration(11));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13, duration.getEncounterDuration(12));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14, duration.getEncounterDuration(13));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15, duration.getEncounterDuration(14));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16, duration.getEncounterDuration(15));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17, duration.getEncounterDuration(16));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18, duration.getEncounterDuration(17));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19, duration.getEncounterDuration(18));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20, duration.getEncounterDuration(19));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21, duration.getEncounterDuration(20));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22, duration.getEncounterDuration(21));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23, duration.getEncounterDuration(22));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24, duration.getEncounterDuration(23));
		db.insert(UsenseSQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, null, values);

		values = new ContentValues();
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, averageDuration.getDevAdd());
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1, averageDuration.getAverageEncounterDuration(0));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2, averageDuration.getAverageEncounterDuration(1));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3, averageDuration.getAverageEncounterDuration(2));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4, averageDuration.getAverageEncounterDuration(3));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5, averageDuration.getAverageEncounterDuration(4));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6, averageDuration.getAverageEncounterDuration(5));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7, averageDuration.getAverageEncounterDuration(6));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8, averageDuration.getAverageEncounterDuration(7));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9, averageDuration.getAverageEncounterDuration(8));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10, averageDuration.getAverageEncounterDuration(9));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11, averageDuration.getAverageEncounterDuration(10));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12, averageDuration.getAverageEncounterDuration(11));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13, averageDuration.getAverageEncounterDuration(12));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14, averageDuration.getAverageEncounterDuration(13));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15, averageDuration.getAverageEncounterDuration(14));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16, averageDuration.getAverageEncounterDuration(15));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17, averageDuration.getAverageEncounterDuration(16));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18, averageDuration.getAverageEncounterDuration(17));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19, averageDuration.getAverageEncounterDuration(18));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20, averageDuration.getAverageEncounterDuration(19));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21, averageDuration.getAverageEncounterDuration(20));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22, averageDuration.getAverageEncounterDuration(21));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23, averageDuration.getAverageEncounterDuration(22));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24, averageDuration.getAverageEncounterDuration(23));
		db.insert(UsenseSQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, null, values);

		values = new ContentValues();
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, socialWeight.getDevAdd());
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT1, socialWeight.getSocialWeight(0));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT2, socialWeight.getSocialWeight(1));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT3, socialWeight.getSocialWeight(2));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT4, socialWeight.getSocialWeight(3));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT5, socialWeight.getSocialWeight(4));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT6, socialWeight.getSocialWeight(5));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT7, socialWeight.getSocialWeight(6));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT8, socialWeight.getSocialWeight(7));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT9, socialWeight.getSocialWeight(8));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT10, socialWeight.getSocialWeight(9));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT11, socialWeight.getSocialWeight(10));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT12, socialWeight.getSocialWeight(11));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT13, socialWeight.getSocialWeight(12));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT14, socialWeight.getSocialWeight(13));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT15, socialWeight.getSocialWeight(14));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT16, socialWeight.getSocialWeight(15));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT17, socialWeight.getSocialWeight(16));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT18, socialWeight.getSocialWeight(17));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT19, socialWeight.getSocialWeight(18));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT20, socialWeight.getSocialWeight(19));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT21, socialWeight.getSocialWeight(20));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT22, socialWeight.getSocialWeight(21));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT23, socialWeight.getSocialWeight(22));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT24, socialWeight.getSocialWeight(23));
		db.insert(UsenseSQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT, null, values);
		//		    return db.insert(UsenseSQLiteHelper.TABLE_BTDEVICE, null, values);
	}

	/**
	 * This method updates a BTDevice already registered by the application. 
	 * This modifies the corresponding record to the BTDevice in the 
	 * BTDEVICE, BTDEVICEENCOUNTERDURATION, BTDEVICEAVERAGEENCOUNTERDURATION, 
	 * and BTDEVICESOCIALWEIGHT tables.
	 * @param btDev The Bluetooth device information.
	 * @param duration The Bluetooth device information regarding the duration that the BT device is within communication range of others.
	 */
	public void updateBTDeviceAndDuration(BTUserDevice btDev, BTUserDevEncounterDuration duration){
		String identifier = UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDev.getDevAdd() + "'";
		ContentValues values = new ContentValues();
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_NAME, btDev.getDevName());
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERSTART, btDev.getEncounterStart());
		db.update(UsenseSQLiteHelper.TABLE_BTDEVICE, values, identifier, null);

		values = new ContentValues();
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, duration.getDevAdd());
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1, duration.getEncounterDuration(0)); 
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2, duration.getEncounterDuration(1));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3, duration.getEncounterDuration(2));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4, duration.getEncounterDuration(3));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5, duration.getEncounterDuration(4));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6, duration.getEncounterDuration(5));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7, duration.getEncounterDuration(6));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8, duration.getEncounterDuration(7));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9, duration.getEncounterDuration(8));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10, duration.getEncounterDuration(9));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11, duration.getEncounterDuration(10));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12, duration.getEncounterDuration(11));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13, duration.getEncounterDuration(12));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14, duration.getEncounterDuration(13));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15, duration.getEncounterDuration(14));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16, duration.getEncounterDuration(15));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17, duration.getEncounterDuration(16));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18, duration.getEncounterDuration(17));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19, duration.getEncounterDuration(18));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20, duration.getEncounterDuration(19));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21, duration.getEncounterDuration(20));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22, duration.getEncounterDuration(21));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23, duration.getEncounterDuration(22));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24, duration.getEncounterDuration(23));
		db.update(UsenseSQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, values, identifier, null);
	}

	/**
	 * This method updates a BTDevice already registered by the application. 
	 * This modifies the corresponding record to the BTDevice in the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @param averageDuration The Bluetooth device information regarding its average encounter duration.
	 */
	public void updateBTDevAvgEncounterDuration(BTUserDevAverageEncounterDuration averageDuration){
		String identifier = UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + averageDuration.getDevAdd() + "'";
		ContentValues values = new ContentValues();

		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, averageDuration.getDevAdd());
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1, averageDuration.getAverageEncounterDuration(0));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2, averageDuration.getAverageEncounterDuration(1));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3, averageDuration.getAverageEncounterDuration(2));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4, averageDuration.getAverageEncounterDuration(3));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5, averageDuration.getAverageEncounterDuration(4));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6, averageDuration.getAverageEncounterDuration(5));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7, averageDuration.getAverageEncounterDuration(6));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8, averageDuration.getAverageEncounterDuration(7));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9, averageDuration.getAverageEncounterDuration(8));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10, averageDuration.getAverageEncounterDuration(9));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11, averageDuration.getAverageEncounterDuration(10));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12, averageDuration.getAverageEncounterDuration(11));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13, averageDuration.getAverageEncounterDuration(12));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14, averageDuration.getAverageEncounterDuration(13));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15, averageDuration.getAverageEncounterDuration(14));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16, averageDuration.getAverageEncounterDuration(15));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17, averageDuration.getAverageEncounterDuration(16));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18, averageDuration.getAverageEncounterDuration(17));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19, averageDuration.getAverageEncounterDuration(18));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20, averageDuration.getAverageEncounterDuration(19));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21, averageDuration.getAverageEncounterDuration(20));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22, averageDuration.getAverageEncounterDuration(21));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23, averageDuration.getAverageEncounterDuration(22));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24, averageDuration.getAverageEncounterDuration(23));
		db.update(UsenseSQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, values, identifier, null);
	}

	/**
	 * This method updates a BTDevice already registered by the application. 
	 * This modifies the corresponding record to the BTDevice in the BTDEVICESOCIALWEIGHT table.
	 * @param socialWeight The Bluetooth device information regarding its social weight.
	 */
	public void updateBTDevSocialWeight(BTUserDevSocialWeight socialWeight){
		String identifier = UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + socialWeight.getDevAdd() + "'";
		ContentValues values = new ContentValues();

		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, socialWeight.getDevAdd());
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT1, socialWeight.getSocialWeight(0));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT2, socialWeight.getSocialWeight(1));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT3, socialWeight.getSocialWeight(2));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT4, socialWeight.getSocialWeight(3));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT5, socialWeight.getSocialWeight(4));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT6, socialWeight.getSocialWeight(5));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT7, socialWeight.getSocialWeight(6));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT8, socialWeight.getSocialWeight(7));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT9, socialWeight.getSocialWeight(8));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT10, socialWeight.getSocialWeight(9));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT11, socialWeight.getSocialWeight(10));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT12, socialWeight.getSocialWeight(11));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT13, socialWeight.getSocialWeight(12));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT14, socialWeight.getSocialWeight(13));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT15, socialWeight.getSocialWeight(14));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT16, socialWeight.getSocialWeight(15));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT17, socialWeight.getSocialWeight(16));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT18, socialWeight.getSocialWeight(17));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT19, socialWeight.getSocialWeight(18));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT20, socialWeight.getSocialWeight(19));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT21, socialWeight.getSocialWeight(20));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT22, socialWeight.getSocialWeight(21));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT23, socialWeight.getSocialWeight(22));
		values.put(UsenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT24, socialWeight.getSocialWeight(23));
		db.update(UsenseSQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT, values, identifier, null);
	}

	/**
	 * This method gets information about a BTDevice already registered by the application. 
	 * @param mac The MAC address of the BTDevice which information should be returned.
	 * @return btDev The btDev object, null if not found.
	 */
	public BTUserDevice getBTDevice(String btDevice) {
		BTUserDevice btDev;
		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_BTDEVICE, allColumnsBTDevices, UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevice + "'", null, null, null, null);
		if (cursor.moveToFirst())
			btDev = cursorToBTDevice(cursor);
		else
			btDev = null;	

		cursor.close();
		return btDev;
	}

	/**
	 * This method gets encounter duration information of a BTDevice already registered by the application. 
	 * @param mac The MAC address of the BTDevice which information should be returned.
	 * @return btDevEncDur The btDevEncDur object, null if not found.
	 */
	public BTUserDevEncounterDuration getBTDeviceEncounterDuration(String btDevEncounterDuration){
		BTUserDevEncounterDuration btDevEncDur;
		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, 
				allColumnsBTDeviceEncounterDuration, UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevEncounterDuration + "'", null, null, null, null);
		if (cursor.moveToFirst())
			btDevEncDur = cursorToBTDevEncounterDuration(cursor);
		else
			btDevEncDur = null;	

		cursor.close();
		return btDevEncDur;
	}

	/**
	 * This method gets average encounter duration information of a BTDevice already registered by the application. 
	 * @param mac The MAC address of the BTDevice which information should be returned.
	 * @return btDevAvgEncDur The btDevAvgEncDur object, null if not found.
	 */
	public BTUserDevAverageEncounterDuration getBTDeviceAverageEncounterDuration(String btDevAverageEncounterDuration){
		BTUserDevAverageEncounterDuration btDevAvgEncDur;
		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, 
				allColumnsBTDeviceAverageEncounterDuration, UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevAverageEncounterDuration + "'", null, null, null, null);
		if (cursor.moveToFirst())
			btDevAvgEncDur = cursorToBTDevAverageEncounterDuration(cursor);
		else
			btDevAvgEncDur = null;	

		cursor.close();
		return btDevAvgEncDur;
	}

	/**
	 * This method gets social weight information of a BTDevice already registered by the application. 
	 * @param mac The MAC address of the BTDevice which information should be returned.
	 * @return btDevSocWeight The btDevSocWeight object, null if not found.
	 */
	public BTUserDevSocialWeight getBTDeviceSocialWeight(String btDevSocialWeight){
		BTUserDevSocialWeight btDevSocWeight;
		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT, 
				allColumnsBTDeviceSocialWeight, UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevSocialWeight + "'", null, null, null, null);
		if (cursor.moveToFirst())
			btDevSocWeight = cursorToBTDevSocialWeight(cursor);
		else
			btDevSocWeight = null;	

		cursor.close();
		return btDevSocWeight;
	}

	/**
	 * This method checks if a given BTDevice has already been registered by the application.
	 * @param mac The MAC address of the BTDevice.
	 * @return true, if BTDevice has already been registered by the application, false otherwise.
	 */
	public boolean hasBTDevice (String btDev) {
		return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + UsenseSQLiteHelper.TABLE_BTDEVICE + " WHERE " + UsenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDev + "'", null) == 0)? false : true;
	}

	/**
	 * This method gets the all the BTDevice recorded by the application on the BTDEVICE table.
	 * @return btDevMap The map with the BTUserDevice objects, and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, BTUserDevice> getAllBTDevice() {
		Map<String, BTUserDevice> btDevMap = new TreeMap<String, BTUserDevice>();

		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_BTDEVICE, allColumnsBTDevices, null, null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			BTUserDevice btDev = cursorToBTDevice(cursor);
			btDevMap.put(btDev.getDevAdd(), btDev);
			cursor.moveToNext();
		}

		cursor.close();
		return btDevMap;
	}

	/**
	 * This method gets the all the BTUserDevEncounterDuration recorded by the application 
	 * on the BTDEVICEENCOUNTERDURATION table.
	 * @return btDevEncounterDurationMap The map with the BTUserDevEncounterDuration objects, 
	 * and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, BTUserDevEncounterDuration> getAllBTDevEncounterDuration() {
		Map<String, BTUserDevEncounterDuration> btDevEncounterDurationMap = new TreeMap<String, BTUserDevEncounterDuration>();

		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, allColumnsBTDeviceEncounterDuration, null, null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			BTUserDevEncounterDuration btDevEncDur = cursorToBTDevEncounterDuration(cursor);
			btDevEncounterDurationMap.put(btDevEncDur.getDevAdd(), btDevEncDur);
			cursor.moveToNext();
		}

		cursor.close();
		return btDevEncounterDurationMap;
	}

	/**
	 * This method gets the all the BTUserDevAverageEncounterDuration recorded by the application 
	 * on the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @return btDevAverageEncounterDurationMap The map with the BTUserDevAverageEncounterDuration objects, 
	 * and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, BTUserDevAverageEncounterDuration> getAllBTDevAverageEncounterDuration() {
		Map<String, BTUserDevAverageEncounterDuration> btDevAverageEncounterDurationMap = new TreeMap<String, BTUserDevAverageEncounterDuration>();

		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, 
				allColumnsBTDeviceAverageEncounterDuration, null, null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			BTUserDevAverageEncounterDuration btDevAvgEncDur = cursorToBTDevAverageEncounterDuration(cursor);
			btDevAverageEncounterDurationMap.put(btDevAvgEncDur.getDevAdd(), btDevAvgEncDur);
			cursor.moveToNext();
		}

		cursor.close();
		return btDevAverageEncounterDurationMap;
	}

	/**
	 * This method gets the all the BTUserDevSocialWeight recorded by the application 
	 * on the BTDEVICESOCIALWEIGHT table.
	 * @return btDevSocialWeightMap The map with the BTUserDevSocialWeight objects, 
	 * and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, BTUserDevSocialWeight> getAllBTDevSocialWeight() {
		Map<String, BTUserDevSocialWeight> btDevSocialWeightMap = new TreeMap<String, BTUserDevSocialWeight>();

		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT, 
				allColumnsBTDeviceSocialWeight, null, null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			BTUserDevSocialWeight btDevSocWeight = cursorToBTDevSocialWeight(cursor);
			btDevSocialWeightMap.put(btDevSocWeight.getDevAdd(), btDevSocWeight);
			cursor.moveToNext();
		}

		cursor.close();
		return btDevSocialWeightMap;
	}


	/**
	 * LOCATION TABLE: This table stores information
	 * regarding each access point visited in the past.
	 * The information stored is:
	 * 		- BSSID of the device
	 * 		- Device Name
	 *      - Distance
	 *      - Last update
	 */

	/**
	 * List of all columns on the ACCESS_POINTS table.
	 */
	private String[] allColumnsLocation = { 
			UsenseSQLiteHelper.COLUMN_MAC_ADDRESS,
			UsenseSQLiteHelper.COLUMN_DEVICE_NAME,
			UsenseSQLiteHelper.COLUMN_DISTANCE,
			UsenseSQLiteHelper.COLUMN_LAST_UPDATE
	};

	/**
	 * This method converts a cursor pointing to a record in the TABLE_LOCATION table to a LocationEntry object.
	 * @param cursor Cursor pointing to a record of the TABLE_LOCATION table.
	 * @return entry the LocationEntry object
	 */
	private LocationEntry cursorToLocation(Cursor cursor) {
		LocationEntry entry = new LocationEntry();
		entry.setBSSID(cursor.getString(0));
		entry.setDeviceName(cursor.getString(1));
		entry.setDistance(cursor.getDouble(2));
		entry.setLastUpdate(cursor.getLong(3));
		return entry;
	}

	/**
	 * This method gets the number of records in the TABLE_LOCATION table. 
	 * This is, the number of locations registered on the application.
	 * @return the number of locations registered by the application.
	 */
	public long getNumEntries(){
		return DatabaseUtils.queryNumEntries(db, UsenseSQLiteHelper.TABLE_LOCATION);
	}

	/**
	 * This method register a new LocationEntry in the application. 
	 * It creates a new record on the TABLE_LOCATION table, with the information passed as MTrackerAP.
	 * @param entry Location Entry information.
	 * @return rowID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerLocationEntry (LocationEntry entry) {
		ContentValues values = new ContentValues();
		values.put(UsenseSQLiteHelper.COLUMN_MAC_ADDRESS, entry.getBSSID());
		values.put(UsenseSQLiteHelper.COLUMN_DEVICE_NAME, entry.getDeviceName());
		values.put(UsenseSQLiteHelper.COLUMN_DISTANCE, entry.getDistance());
		values.put(UsenseSQLiteHelper.COLUMN_LAST_UPDATE, entry.getLastUpdate());

		return db.insert(UsenseSQLiteHelper.TABLE_LOCATION, null, values);
	}

	/**
	 * This method update an Location Entry already registered by the application. 
	 * This modifies the corresponding record to the Location Entry in the TABLE_LOCATION table.
	 * @param location Location Entry information.
	 * @return true if successful.
	 */
	public boolean updateLocationEntry(LocationEntry entry) {
		String identifier = UsenseSQLiteHelper.COLUMN_MAC_ADDRESS + "='" + entry.getBSSID() + "'";
		ContentValues values = new ContentValues();
		values.put(UsenseSQLiteHelper.COLUMN_MAC_ADDRESS, entry.getBSSID());
		values.put(UsenseSQLiteHelper.COLUMN_DEVICE_NAME, entry.getDeviceName());
		values.put(UsenseSQLiteHelper.COLUMN_DISTANCE, entry.getDistance());
		values.put(UsenseSQLiteHelper.COLUMN_LAST_UPDATE, entry.getLastUpdate());

		int rows = db.update(UsenseSQLiteHelper.TABLE_LOCATION, values, identifier, null);

		return ((rows != 0)? true : false);
	}

	/**
	 * This method checks whether entry is removed from the location entry
	 * @param entry Location Entry
	 * @return true if successful
	 */
	public int removeLocationEntry(LocationEntry entry) {
		String identifier = UsenseSQLiteHelper.COLUMN_MAC_ADDRESS + "='" + entry.getBSSID() + "'";
		return db.delete(UsenseSQLiteHelper.TABLE_LOCATION, identifier, null);
	}

	/**
	 * This method get the location entries from the TABLE_LOCATION
	 * @param bssid location id
	 * @return ap location entry object
	 */
	public LocationEntry getLocationEntry(String bssid) {
		LocationEntry ap;
		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_LOCATION, allColumnsLocation, UsenseSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null, null, null, null);
		if (cursor.moveToFirst())
			ap = cursorToLocation(cursor);
		else
			ap = null;	

		cursor.close();
		return ap;
	}


	/**
	 * This method checks whether the bssid is available in TABLE_LOCATION table
	 * @param bssid location id
	 * @return true if location id is in location ebtry object
	 */
	public boolean hasLocationEntry (String bssid) {
		return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + UsenseSQLiteHelper.TABLE_LOCATION + " WHERE " + UsenseSQLiteHelper.COLUMN_MAC_ADDRESS + "='" + bssid + "'", null) == 0)? false : true;
	}

	/**
	 * This method gets the all the location entries recorded by the application on the TABLE_LOCATION table.
	 * @return mLocationMap A map with the LocationEntry objects.
	 */
	public Map<String, LocationEntry> getAllLocationEntries() {
		Map<String, LocationEntry> mLocationMap = new TreeMap<String, LocationEntry>();

		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_LOCATION, allColumnsLocation, null, null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			LocationEntry entry = cursorToLocation(cursor);
			mLocationMap.put(entry.getBSSID(), entry);
			cursor.moveToNext();
		}

		cursor.close();
		return mLocationMap;
	}

	/**
	 * This method clean the TABLE_LOCATION table
	 */
	public void cleanLocationTable() {
		db.execSQL("delete from "+ UsenseSQLiteHelper.TABLE_LOCATION);
	}

	/** Sound */
	
	/**
	 * SOUND TABLE: This table stores information
	 * regarding user's pattern spent in each stste (quiet, normal/loud, noisy).
	 * The information stored is:
	 * 		- Date 
	 * 		- Time spent in Queit 
	 * 		- Time spent in Normal
	 * 		- Time spent in Alert 
	 * 		- Time spent in Noisy
	 */

	/**
	 * List of all columns on the Sound table.
	 */
	public String[] allColumnsSound = { 
			UsenseSQLiteHelper.KEY_ROWID,
			UsenseSQLiteHelper.SOUND_DATE ,
			UsenseSQLiteHelper.QUIET_MILLIS,
			UsenseSQLiteHelper.NORMAL_MILLIS,
			UsenseSQLiteHelper.ALERT_MILLIS,
			UsenseSQLiteHelper.NOISE_MILLIS
	};

	/**
	 * This method provides cursor to the TABLE_SOUND table
	 * @param cursor Cursor pointing to a record of the TABLE_SOUND table.
	 * @return sl SoundLevel
	 */
	private SoundLevel cursorToSound(Cursor cursor) {
		SoundLevel sl = new SoundLevel();
		sl.setRowID(cursor.getInt(0));
		sl.setSoundDate(cursor.getInt(1));
		sl.setQuietTime(cursor.getLong(2));
		sl.setNormalTime(cursor.getLong(3));
		sl.setAlertTime(cursor.getLong(4));
		sl.setNoiseTime(cursor.getLong(5));
		return sl;
	}

	/**
	 * This method get the entries in TABLE_SOUND table
	 * @return  SoundLevel The number of rows in the table 
	 */
	public long getNumSoundLevel(){
		return DatabaseUtils.queryNumEntries(db, UsenseSQLiteHelper.TABLE_SOUND);
	}

	/**
	 * This method register the new entry into TABLE_SOUND table 
	 * @param sl Sound level object
	 * @return numberOfRows number of rows are inserted in the table
	 */
	public long registerNewSoundLevel (SoundLevel sl) {
		ContentValues cv = new ContentValues();
		cv.put(UsenseSQLiteHelper.SOUND_DATE, sl.getSoundDate());
		cv.put(UsenseSQLiteHelper.QUIET_MILLIS, sl.getQuietTime());
		cv.put(UsenseSQLiteHelper.NORMAL_MILLIS, sl.getNormalTime());
		cv.put(UsenseSQLiteHelper.ALERT_MILLIS, sl.getAlertTime());
		cv.put(UsenseSQLiteHelper.NOISE_MILLIS, sl.getNoiseTime());


		return db.insert(UsenseSQLiteHelper.TABLE_SOUND, null, cv);
	}

	/**
	 * This method update the sound level into TABLE_SOUND table
	 * @param sl Sound level object
	 * @return true The number of rows are updated in the table
	 */
	public boolean updateSoundLevel(SoundLevel sl) {
		String identifier = UsenseSQLiteHelper.SOUND_DATE + "='" + sl.getSoundDate() + "'";
		ContentValues values = new ContentValues();
		values.put(UsenseSQLiteHelper.KEY_ROWID, sl.getRowID());
		values.put(UsenseSQLiteHelper.SOUND_DATE, sl.getSoundDate());
		values.put(UsenseSQLiteHelper.QUIET_MILLIS, sl.getQuietTime());
		values.put(UsenseSQLiteHelper.NORMAL_MILLIS, sl.getNormalTime());
		values.put(UsenseSQLiteHelper.ALERT_MILLIS, sl.getAlertTime());
		values.put(UsenseSQLiteHelper.NOISE_MILLIS, sl.getNoiseTime());

		int rows = db.update(UsenseSQLiteHelper.TABLE_SOUND, values,identifier, null);

		return ((rows != 0)? true : false);
	}


	/**
	 * This method provides the sound level
	 * @param todayMillis Time in milliseconds
	 * @return sl SoundLevel with time in milliseconds
	 */
	public SoundLevel getSoundLevel(long todayMillis) {
		SoundLevel sl;
		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_SOUND, allColumnsSound, UsenseSQLiteHelper.SOUND_DATE + "='" + todayMillis + "'", null, null, null, null, null);
		if (cursor.moveToFirst())
			sl = cursorToSound(cursor);
		else
			sl = null;	

		cursor.close();
		return sl;
	}

	/**
	 * This method check whether the entry is available or not from TABLE_SOUND table
	 * @param soundDate Date of the sound level
	 * @return true If the entry is available
	 */
	public boolean hasSoundLevel (long soundDate) {
		return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + UsenseSQLiteHelper.TABLE_SOUND + " WHERE " + UsenseSQLiteHelper.SOUND_DATE + "='" + soundDate + "'", null) == 0)? false : true;
	}

	/**
	 * This method gets the all the sound level entries recorded by the application on the TABLE_SOUND table.
	 * @return slMap A map with the SoundLevel objects.
	 */
	public Map<String, SoundLevel> getAllSoundLevel() {
		Map<String, SoundLevel> slMap = new TreeMap<String, SoundLevel>();

		Cursor cursor = db.query(UsenseSQLiteHelper.TABLE_SOUND, allColumnsSound, null, null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			SoundLevel sl = cursorToSound(cursor);
			Integer s2=sl.getRowID();
			slMap.put(s2.toString(), sl);
			cursor.moveToNext();
		}

		cursor.close();
		return slMap;
	}
}

