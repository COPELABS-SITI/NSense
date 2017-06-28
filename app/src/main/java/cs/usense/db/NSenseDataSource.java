/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class support for DataBase module and 
 * it provides various methods to access the DB from external modules.
 * @author Saeik Firdose (COPELABS/ULHT), 
 * @author Luis Lopes (COPELABS/ULHT), 
 * @author Waldir Moreira (COPELABS/ULHT), 
 * @author Reddy Pallavali (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 */
package cs.usense.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import cs.usense.models.SociabilityDetailItem;
import cs.usense.models.SociabilityGraphItem;
import cs.usense.pipelines.location.LocationEntry;
import cs.usense.pipelines.location.NSenseDevice;
import cs.usense.pipelines.motion.MotionEntry;
import cs.usense.pipelines.proximity.BTUserDevAverageEncounterDuration;
import cs.usense.pipelines.proximity.BTUserDevEncounterDuration;
import cs.usense.pipelines.proximity.BTUserDevSocialWeight;
import cs.usense.pipelines.proximity.BTUserDevice;
import cs.usense.pipelines.sound.SoundLevel;
import cs.usense.utilities.DateUtils;
import cs.usense.utilities.InterestsUtils;

/**
 * This class provides various methods and information of the NSense Data base. 
 * Methods include, open database, close database, access to the tables
 */
public class NSenseDataSource {

	/**
	 * COMMON OPERATIONS
	 */
	private static NSenseDataSource mInstance = null;
	private SQLiteDatabase db;
	private NSenseSQLiteHelper dbHelper;
	private boolean isDbOpen;

	public static NSenseDataSource getInstance(Context context) {
		if(mInstance == null) {
			mInstance = new NSenseDataSource(context);
		}
		return mInstance;
	}

	/**
	 * This method Construct the context. which takes Android Context as input.
	 * @param context Interface to global information about an application environment.
	 */
	private NSenseDataSource (Context context) {
		dbHelper = new NSenseSQLiteHelper(context);
		isDbOpen = false;
	}


	/**
	 * This method opens the predefined NSense database.
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
	 * This method close the predefined NSense database.
	 */
	public void closeDB() {
		dbHelper.close();
		isDbOpen = false;
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
			NSenseSQLiteHelper.ACTIONS_COLUMN_ACTION_TYPE,
			NSenseSQLiteHelper.ACTIONS_COLUMN_DURATION,
			NSenseSQLiteHelper.ACTIONS_COLUMN_HOUR,
			NSenseSQLiteHelper.ACTIONS_COLUMN_ACTIONCOUNTER,
			NSenseSQLiteHelper.ACTIONS_COLUMN_DATE,
			NSenseSQLiteHelper.ACTIONS_COLUMN_TIMEFRAME
	};

	/**
	 * This method register a new MotionEntry in the application.
	 * It creates a new record on the Action Entry table, with the information passed as MotionEntry.
	 * @param ae Action enty information.
	 * @return the action type of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewActionEntry (MotionEntry ae) {
		ContentValues values = new ContentValues();
		values.put(NSenseSQLiteHelper.ACTIONS_COLUMN_ACTION_TYPE, ae.getActionType());
		values.put(NSenseSQLiteHelper.ACTIONS_COLUMN_DURATION, ae.getActionDuration());
		values.put(NSenseSQLiteHelper.COLUMN_HOUR, ae.getHour());
		values.put(NSenseSQLiteHelper.ACTIONS_COLUMN_ACTIONCOUNTER, ae.getActionCounter());
		values.put(NSenseSQLiteHelper.ACTIONS_COLUMN_DATE, ae.getDate());
		values.put(NSenseSQLiteHelper.ACTIONS_COLUMN_TIMEFRAME, ae.getTimeFrame());
		return db.insert(NSenseSQLiteHelper.TABLE_ACTIONS, null, values);
	}

	/**
	 * This method update an MotionEntry already registered by the application.
	 * This modifies the corresponding record to the Entry in the ACTIONENTRY table.
	 * @param ae action entry information.
	 * @return true if successful.
	 */
	public boolean updateActionEntry(MotionEntry ae) {
		String identifier = NSenseSQLiteHelper.ACTIONS_COLUMN_ACTION_TYPE + "='" + ae.getActionType() + "'"
				+ "and " + NSenseSQLiteHelper.ACTIONS_COLUMN_HOUR + "='" + ae.getTimeFrame() + "'";
		ContentValues values = new ContentValues();
		values.put(NSenseSQLiteHelper.ACTIONS_COLUMN_ACTION_TYPE, ae.getActionType());
		values.put(NSenseSQLiteHelper.ACTIONS_COLUMN_DURATION, getDuration(ae.getActionType(), ae.getHour()) + ae.getActionDuration());
		values.put(NSenseSQLiteHelper.COLUMN_HOUR, ae.getHour());
		values.put(NSenseSQLiteHelper.ACTIONS_COLUMN_ACTIONCOUNTER, ae.getActionCounter());
		values.put(NSenseSQLiteHelper.ACTIONS_COLUMN_DATE, ae.getDate());
		values.put(NSenseSQLiteHelper.ACTIONS_COLUMN_TIMEFRAME, ae.getTimeFrame());
		int rows = db.update(NSenseSQLiteHelper.TABLE_ACTIONS, values, identifier, null);
		return ((rows != 0));
	}

	public long getDuration(String activityType, int hour) {
		long duration = 0;
		Cursor cursor = db.query(
				NSenseSQLiteHelper.TABLE_ACTIONS,
				allColumnsActionsEntry,
				NSenseSQLiteHelper.ACTIONS_COLUMN_ACTION_TYPE + "='" + activityType + "'"
						+ " and " +
						NSenseSQLiteHelper.ACTIONS_COLUMN_HOUR + "='" + hour + "'" ,
				null,
				null,
				null,
				null);

		if (cursor.moveToFirst()) {
			duration = cursor.getLong(1);
		}
		return duration;
	}

	/**
	 * This method checks if a given Actions has already been registered by the application.
	 * @param actionType Actions of the Actions
	 * @return true if Actions has already been registered by the application, false otherwise.
	 */
	public boolean hasActionEntry (String actionType, int hour) {
		return DatabaseUtils.longForQuery(
				db,
				"SELECT COUNT(*) FROM "
						+ NSenseSQLiteHelper.TABLE_ACTIONS
						+ " WHERE " + NSenseSQLiteHelper.ACTIONS_COLUMN_ACTION_TYPE
						+ "='" + actionType + "'"
						+ " and "
						+ NSenseSQLiteHelper.ACTIONS_COLUMN_HOUR
						+ "='" + hour + "'"
				, null) != 0;
	}

	/**
	 * This method inserts on motion table the last motion type
	 * @param date timestamp
	 * @param action action type
	 */
	public void insertMotionRegistry(String date, String action) {
		ContentValues cv = new ContentValues();
		cv.put(NSenseSQLiteHelper.TABLE_ACCELEROMETER_DATE, date);
		cv.put(NSenseSQLiteHelper.TABLE_ACCELEROMETER_ACTION, action);
		db.insert(NSenseSQLiteHelper.TABLE_ACCELEROMETER, null, cv);
	}

	/**
	 * This method returns the last activity registered
	 * @return last activity registered
	 */
	public String fetchLastMotionRegistry() {
		String result = "null";
		try {
			Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_ACCELEROMETER, null, null, null, null, null, null);
			cursor.moveToLast();
			result = cursor.getString(1);
		} catch (CursorIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return result;
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
			NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
			NSenseSQLiteHelper.COLUMN_BTDEV_NAME,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERSTART,
			NSenseSQLiteHelper.COLUMN_INTERESTS
	};

	/**
	 * List of all columns on the BTDEVICEENCOUNTERDURATION table.
	 */
	private String[] allColumnsBTDeviceEncounterDuration = {
			NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23,
			NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24,
	};

	/**
	 * List of all columns on the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 */
	private String[] allColumnsBTDeviceAverageEncounterDuration = {
			NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23,
			NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24,
	};

	/**
	 * List of all columns on the BTDEVICESOCIALWEIGHT table.
	 */
	private String[] allColumnsBTDeviceSocialWeight = {
			NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT1,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT2,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT3,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT4,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT5,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT6,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT7,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT8,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT9,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT10,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT11,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT12,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT13,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT14,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT15,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT16,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT17,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT18,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT19,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT20,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT21,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT22,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT23,
			NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT24,
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
		btDev.setInterests(cursor.getString(3));
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

	public void insertDevice(NSenseDevice nSenseDevice) {
		if(nSenseDevice.getBtMACAddress() != null && !(nSenseDevice.getBtMACAddress().isEmpty())) {

			BTUserDevice btDev = new BTUserDevice();
			btDev.setDevAdd(nSenseDevice.getBtMACAddress());
			btDev.setDevName(nSenseDevice.getDeviceName());
			btDev.setInterests(nSenseDevice.getInterests());
			btDev.setEncounterTime(System.nanoTime());

			BTUserDevEncounterDuration btUserDevEncounterDuration = new BTUserDevEncounterDuration();
			btUserDevEncounterDuration.setDevAdd(nSenseDevice.getBtMACAddress());

			BTUserDevAverageEncounterDuration btUserDevAverageEncounterDuration = new BTUserDevAverageEncounterDuration();
			btUserDevAverageEncounterDuration.setDevAdd(nSenseDevice.getBtMACAddress());

			BTUserDevSocialWeight btUserDevSocialWeight = new BTUserDevSocialWeight();
			btUserDevSocialWeight.setDevAdd(nSenseDevice.getBtMACAddress());

			registerNewBTDevice(
					btDev,
					btUserDevEncounterDuration,
					btUserDevAverageEncounterDuration,
					btUserDevSocialWeight
			);

			/*
			values.put(NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, nSenseDevice.getBtMACAddress());
			values.put(NSenseSQLiteHelper.COLUMN_BTDEV_NAME, nSenseDevice.getDeviceName());
			values.put(NSenseSQLiteHelper.COLUMN_INTERESTS, nSenseDevice.getInterests());
            values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERSTART, System.nanoTime());
			db.insert(NSenseSQLiteHelper.TABLE_BTDEVICE, null, values);
			*/
		}
	}

	public void updateInterests(NSenseDevice nSenseDevice) {
		if(nSenseDevice.getBtMACAddress() == null || nSenseDevice.getBtMACAddress().isEmpty()) {
			updateInterestsByDeviceName(nSenseDevice.getDeviceName(), nSenseDevice.getInterests());
		} else {
			updateInterestsByBtMacAddress(nSenseDevice.getBtMACAddress(), nSenseDevice.getInterests());
		}
	}

	private void updateInterestsByBtMacAddress(String btMacAddress, String interests) {
		String identifier = NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btMacAddress + "'";
		storeInterestsOnDB(identifier, interests);
	}

	private void updateInterestsByDeviceName(String deviceName, String interests) {
		String identifier = NSenseSQLiteHelper.COLUMN_BTDEV_NAME + "='" + deviceName + "'";
		storeInterestsOnDB(identifier, interests);
	}

	private void storeInterestsOnDB(String identifier, String interests) {
		ContentValues values = new ContentValues();
		values.put(NSenseSQLiteHelper.COLUMN_INTERESTS, interests);
		db.update(NSenseSQLiteHelper.TABLE_BTDEVICE, values, identifier, null);
	}

	public void updateSW(String btMacAddress, double sw) {
		if(sw > 0) {
			String identifier = NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btMacAddress + "'";
			ContentValues values = new ContentValues();
			values.put(NSenseSQLiteHelper.COLUMN_SW_NOW, sw);
			db.update(NSenseSQLiteHelper.TABLE_BTDEVICE, values, identifier, null);
		}
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
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, btDev.getDevAdd());
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_NAME, btDev.getDevName());
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERSTART, btDev.getEncounterStart());
		db.insert(NSenseSQLiteHelper.TABLE_BTDEVICE, null, values);

		values = new ContentValues();
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, duration.getDevAdd());
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1, duration.getEncounterDuration(0));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2, duration.getEncounterDuration(1));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3, duration.getEncounterDuration(2));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4, duration.getEncounterDuration(3));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5, duration.getEncounterDuration(4));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6, duration.getEncounterDuration(5));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7, duration.getEncounterDuration(6));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8, duration.getEncounterDuration(7));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9, duration.getEncounterDuration(8));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10, duration.getEncounterDuration(9));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11, duration.getEncounterDuration(10));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12, duration.getEncounterDuration(11));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13, duration.getEncounterDuration(12));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14, duration.getEncounterDuration(13));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15, duration.getEncounterDuration(14));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16, duration.getEncounterDuration(15));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17, duration.getEncounterDuration(16));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18, duration.getEncounterDuration(17));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19, duration.getEncounterDuration(18));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20, duration.getEncounterDuration(19));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21, duration.getEncounterDuration(20));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22, duration.getEncounterDuration(21));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23, duration.getEncounterDuration(22));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24, duration.getEncounterDuration(23));
		db.insert(NSenseSQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, null, values);

		values = new ContentValues();
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, averageDuration.getDevAdd());
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1, averageDuration.getAverageEncounterDuration(0));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2, averageDuration.getAverageEncounterDuration(1));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3, averageDuration.getAverageEncounterDuration(2));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4, averageDuration.getAverageEncounterDuration(3));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5, averageDuration.getAverageEncounterDuration(4));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6, averageDuration.getAverageEncounterDuration(5));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7, averageDuration.getAverageEncounterDuration(6));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8, averageDuration.getAverageEncounterDuration(7));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9, averageDuration.getAverageEncounterDuration(8));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10, averageDuration.getAverageEncounterDuration(9));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11, averageDuration.getAverageEncounterDuration(10));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12, averageDuration.getAverageEncounterDuration(11));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13, averageDuration.getAverageEncounterDuration(12));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14, averageDuration.getAverageEncounterDuration(13));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15, averageDuration.getAverageEncounterDuration(14));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16, averageDuration.getAverageEncounterDuration(15));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17, averageDuration.getAverageEncounterDuration(16));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18, averageDuration.getAverageEncounterDuration(17));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19, averageDuration.getAverageEncounterDuration(18));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20, averageDuration.getAverageEncounterDuration(19));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21, averageDuration.getAverageEncounterDuration(20));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22, averageDuration.getAverageEncounterDuration(21));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23, averageDuration.getAverageEncounterDuration(22));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24, averageDuration.getAverageEncounterDuration(23));
		db.insert(NSenseSQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, null, values);

		values = new ContentValues();
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, socialWeight.getDevAdd());
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT1, socialWeight.getSocialWeight(0));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT2, socialWeight.getSocialWeight(1));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT3, socialWeight.getSocialWeight(2));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT4, socialWeight.getSocialWeight(3));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT5, socialWeight.getSocialWeight(4));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT6, socialWeight.getSocialWeight(5));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT7, socialWeight.getSocialWeight(6));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT8, socialWeight.getSocialWeight(7));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT9, socialWeight.getSocialWeight(8));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT10, socialWeight.getSocialWeight(9));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT11, socialWeight.getSocialWeight(10));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT12, socialWeight.getSocialWeight(11));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT13, socialWeight.getSocialWeight(12));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT14, socialWeight.getSocialWeight(13));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT15, socialWeight.getSocialWeight(14));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT16, socialWeight.getSocialWeight(15));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT17, socialWeight.getSocialWeight(16));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT18, socialWeight.getSocialWeight(17));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT19, socialWeight.getSocialWeight(18));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT20, socialWeight.getSocialWeight(19));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT21, socialWeight.getSocialWeight(20));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT22, socialWeight.getSocialWeight(21));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT23, socialWeight.getSocialWeight(22));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT24, socialWeight.getSocialWeight(23));
		db.insert(NSenseSQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT, null, values);
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
		String identifier = NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDev.getDevAdd() + "'";
		ContentValues values = new ContentValues();
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_NAME, btDev.getDevName());
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERSTART, btDev.getEncounterStart());
		db.update(NSenseSQLiteHelper.TABLE_BTDEVICE, values, identifier, null);

		values = new ContentValues();
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, duration.getDevAdd());
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1, duration.getEncounterDuration(0));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2, duration.getEncounterDuration(1));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3, duration.getEncounterDuration(2));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4, duration.getEncounterDuration(3));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5, duration.getEncounterDuration(4));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6, duration.getEncounterDuration(5));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7, duration.getEncounterDuration(6));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8, duration.getEncounterDuration(7));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9, duration.getEncounterDuration(8));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10, duration.getEncounterDuration(9));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11, duration.getEncounterDuration(10));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12, duration.getEncounterDuration(11));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13, duration.getEncounterDuration(12));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14, duration.getEncounterDuration(13));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15, duration.getEncounterDuration(14));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16, duration.getEncounterDuration(15));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17, duration.getEncounterDuration(16));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18, duration.getEncounterDuration(17));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19, duration.getEncounterDuration(18));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20, duration.getEncounterDuration(19));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21, duration.getEncounterDuration(20));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22, duration.getEncounterDuration(21));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23, duration.getEncounterDuration(22));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24, duration.getEncounterDuration(23));
		db.update(NSenseSQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, values, identifier, null);
	}

	/**
	 * This method updates a BTDevice already registered by the application.
	 * This modifies the corresponding record to the BTDevice in the BTDEVICEAVERAGEENCOUNTERDURATION table.
	 * @param averageDuration The Bluetooth device information regarding its average encounter duration.
	 */
	public void updateBTDevAvgEncounterDuration(BTUserDevAverageEncounterDuration averageDuration){
		String identifier = NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + averageDuration.getDevAdd() + "'";
		ContentValues values = new ContentValues();

		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, averageDuration.getDevAdd());
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1, averageDuration.getAverageEncounterDuration(0));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2, averageDuration.getAverageEncounterDuration(1));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3, averageDuration.getAverageEncounterDuration(2));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4, averageDuration.getAverageEncounterDuration(3));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5, averageDuration.getAverageEncounterDuration(4));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6, averageDuration.getAverageEncounterDuration(5));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7, averageDuration.getAverageEncounterDuration(6));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8, averageDuration.getAverageEncounterDuration(7));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9, averageDuration.getAverageEncounterDuration(8));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10, averageDuration.getAverageEncounterDuration(9));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11, averageDuration.getAverageEncounterDuration(10));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12, averageDuration.getAverageEncounterDuration(11));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13, averageDuration.getAverageEncounterDuration(12));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14, averageDuration.getAverageEncounterDuration(13));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15, averageDuration.getAverageEncounterDuration(14));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16, averageDuration.getAverageEncounterDuration(15));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17, averageDuration.getAverageEncounterDuration(16));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18, averageDuration.getAverageEncounterDuration(17));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19, averageDuration.getAverageEncounterDuration(18));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20, averageDuration.getAverageEncounterDuration(19));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21, averageDuration.getAverageEncounterDuration(20));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22, averageDuration.getAverageEncounterDuration(21));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23, averageDuration.getAverageEncounterDuration(22));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24, averageDuration.getAverageEncounterDuration(23));
		db.update(NSenseSQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION, values, identifier, null);
	}

	/**
	 * This method updates a BTDevice already registered by the application.
	 * This modifies the corresponding record to the BTDevice in the BTDEVICESOCIALWEIGHT table.
	 * @param socialWeight The Bluetooth device information regarding its social weight.
	 */
	public void updateBTDevSocialWeight(BTUserDevSocialWeight socialWeight){
		String identifier = NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + socialWeight.getDevAdd() + "'";
		ContentValues values = new ContentValues();

		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS, socialWeight.getDevAdd());
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT1, socialWeight.getSocialWeight(0));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT2, socialWeight.getSocialWeight(1));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT3, socialWeight.getSocialWeight(2));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT4, socialWeight.getSocialWeight(3));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT5, socialWeight.getSocialWeight(4));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT6, socialWeight.getSocialWeight(5));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT7, socialWeight.getSocialWeight(6));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT8, socialWeight.getSocialWeight(7));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT9, socialWeight.getSocialWeight(8));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT10, socialWeight.getSocialWeight(9));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT11, socialWeight.getSocialWeight(10));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT12, socialWeight.getSocialWeight(11));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT13, socialWeight.getSocialWeight(12));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT14, socialWeight.getSocialWeight(13));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT15, socialWeight.getSocialWeight(14));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT16, socialWeight.getSocialWeight(15));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT17, socialWeight.getSocialWeight(16));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT18, socialWeight.getSocialWeight(17));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT19, socialWeight.getSocialWeight(18));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT20, socialWeight.getSocialWeight(19));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT21, socialWeight.getSocialWeight(20));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT22, socialWeight.getSocialWeight(21));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT23, socialWeight.getSocialWeight(22));
		values.put(NSenseSQLiteHelper.COLUMN_BTDEV_SOCIALWEIGHT_SLOT24, socialWeight.getSocialWeight(23));
		db.update(NSenseSQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT, values, identifier, null);
	}

	/**
	 * This method gets information about a BTDevice already registered by the application.
	 * @param mac The MAC address of the BTDevice which information should be returned.
	 * @return btDev The btDev object, null if not found.
	 */
	public BTUserDevice getBTDevice(String btDevice) {
		BTUserDevice btDev;
		Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_BTDEVICE, allColumnsBTDevices, NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevice + "'", null, null, null, null);
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
		Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION,
				allColumnsBTDeviceEncounterDuration, NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevEncounterDuration + "'", null, null, null, null);
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
		Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION,
				allColumnsBTDeviceAverageEncounterDuration, NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevAverageEncounterDuration + "'", null, null, null, null);
		if (cursor.moveToFirst())
			btDevAvgEncDur = cursorToBTDevAverageEncounterDuration(cursor);
		else
			btDevAvgEncDur = null;
		cursor.close();
		return btDevAvgEncDur;
	}

	public double getEncounterDurationNow(String btMacAddress) {
		BTUserDevEncounterDuration duration = getBTDeviceEncounterDuration(btMacAddress);
		return duration.getEncounterDuration(DateUtils.getTimeSlot());
	}

	/**
	 * This method gets social weight information of a BTDevice already registered by the application.
	 * @param mac The MAC address of the BTDevice which information should be returned.
	 * @return btDevSocWeight The btDevSocWeight object, null if not found.
	 */
	public BTUserDevSocialWeight getBTDeviceSocialWeight(String btDevSocialWeight){
		BTUserDevSocialWeight btDevSocWeight;
		Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT,
				allColumnsBTDeviceSocialWeight, NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btDevSocialWeight + "'", null, null, null, null);
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
	public boolean hasBTDeviceByMac(String mac) {
		return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + NSenseSQLiteHelper.TABLE_BTDEVICE + " WHERE " + NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + mac + "'", null) != 0;
	}

	public boolean hasBTDeviceByName(String deviceName) {
		return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + NSenseSQLiteHelper.TABLE_BTDEVICE + " WHERE " + NSenseSQLiteHelper.COLUMN_BTDEV_NAME + "='" + deviceName + "'", null) != 0;
	}

	public boolean hasBTDevice(NSenseDevice nSenseDevice) {
		boolean result;
		if(nSenseDevice.getBtMACAddress() == null || nSenseDevice.getBtMACAddress().isEmpty()) {
			result = hasBTDeviceByName(nSenseDevice.getDeviceName());
		} else {
			result = (hasBTDeviceByName(nSenseDevice.getDeviceName()) || hasBTDeviceByMac(nSenseDevice.getBtMACAddress()));
		}
		return result;
	}


	/**
	 * This method gets the all the BTDevice recorded by the application on the BTDEVICE table.
	 * @return btDevMap The map with the BTUserDevice objects, and the BTDEV_MAC_ADDRESS as key.
	 */
	public Map<String, BTUserDevice> getAllBTDevice() {
		Map<String, BTUserDevice> btDevMap = new TreeMap<>();

		Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_BTDEVICE, allColumnsBTDevices, null, null, null, null, null);
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

		Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_BTDEVICEENCOUNTERDURATION, allColumnsBTDeviceEncounterDuration, null, null, null, null, null);
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

		Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_BTDEVICEAVERAGEENCOUNTERDURATION,
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

		Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_BTDEVICESOCIALWEIGHT,
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
			NSenseSQLiteHelper.COLUMN_MAC_ADDRESS,
			NSenseSQLiteHelper.COLUMN_BT_MAC_ADDRESS,
			NSenseSQLiteHelper.COLUMN_DEVICE_NAME,
			NSenseSQLiteHelper.COLUMN_DISTANCE,
			NSenseSQLiteHelper.COLUMN_LAST_UPDATE
	};

	/**
	 * This method converts a cursor pointing to a record in the TABLE_LOCATION table to a LocationEntry object.
	 * @param cursor Cursor pointing to a record of the TABLE_LOCATION table.
	 * @return entry the LocationEntry object
	 */
	private LocationEntry cursorToLocation(Cursor cursor) {
		return new LocationEntry(cursor.getString(2), cursor.getString(0), cursor.getLong(4), cursor.getDouble(3), cursor.getString(1));
	}

	/**
	 * This method register a new LocationEntry in the application.
	 * It creates a new record on the TABLE_LOCATION table, with the information passed as MTrackerAP.
	 * @param entry LocationPipeline Entry information.
	 * @return rowID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerLocationEntry (LocationEntry entry) {
		ContentValues values = new ContentValues();
		values.put(NSenseSQLiteHelper.COLUMN_MAC_ADDRESS, entry.getBSSID());
		values.put(NSenseSQLiteHelper.COLUMN_BT_MAC_ADDRESS, entry.getBTMACAddress());
		values.put(NSenseSQLiteHelper.COLUMN_DEVICE_NAME, entry.getDeviceName());
		values.put(NSenseSQLiteHelper.COLUMN_DISTANCE, entry.getDistance());
		values.put(NSenseSQLiteHelper.COLUMN_LAST_UPDATE, entry.getLastUpdate());
		return db.insert(NSenseSQLiteHelper.TABLE_LOCATION, null, values);
	}

	/**
	 * This method update an LocationPipeline Entry already registered by the application.
	 * This modifies the corresponding record to the LocationPipeline Entry in the TABLE_LOCATION table.
	 * @param location LocationPipeline Entry information.
	 * @return true if successful.
	 */
	public boolean updateLocationEntry(LocationEntry entry) {
		String identifier = NSenseSQLiteHelper.COLUMN_MAC_ADDRESS + "='" + entry.getBSSID() + "'";
		ContentValues values = new ContentValues();
		values.put(NSenseSQLiteHelper.COLUMN_MAC_ADDRESS, entry.getBSSID());
		values.put(NSenseSQLiteHelper.COLUMN_BT_MAC_ADDRESS, entry.getBTMACAddress());
		values.put(NSenseSQLiteHelper.COLUMN_DEVICE_NAME, entry.getDeviceName());
		values.put(NSenseSQLiteHelper.COLUMN_DISTANCE, entry.getDistance());
		values.put(NSenseSQLiteHelper.COLUMN_LAST_UPDATE, entry.getLastUpdate());
		int rows = db.update(NSenseSQLiteHelper.TABLE_LOCATION, values, identifier, null);
		return ((rows != 0));
	}

	/**
	 * This method checks whether entry is removed from the location entry
	 * @param entry LocationPipeline Entry
	 * @return true if successful
	 */
	public int removeLocationEntry(LocationEntry entry) {
		String identifier = NSenseSQLiteHelper.COLUMN_MAC_ADDRESS + "='" + entry.getBSSID() + "'";
		return db.delete(NSenseSQLiteHelper.TABLE_LOCATION, identifier, null);
	}

	/**
	 * This method get the location entries from the TABLE_LOCATION
	 * @param mac location id
	 * @return ap location entry object
	 */
	public LocationEntry getLocationEntry(String mac, String deviceName) {
		LocationEntry ap = null;
		Cursor cursorWifi = db.query(NSenseSQLiteHelper.TABLE_LOCATION, allColumnsLocation, NSenseSQLiteHelper.COLUMN_MAC_ADDRESS + "='" + mac + "'", null, null, null, null);
		Cursor cursorBt = db.query(NSenseSQLiteHelper.TABLE_LOCATION, allColumnsLocation, NSenseSQLiteHelper.COLUMN_BT_MAC_ADDRESS + "='" + mac + "'", null, null, null, null);
		Cursor cursorName = db.query(NSenseSQLiteHelper.TABLE_LOCATION, allColumnsLocation, NSenseSQLiteHelper.COLUMN_DEVICE_NAME + "='" + deviceName + "'", null, null, null, null);

		if (cursorWifi.moveToFirst()) {
			ap = cursorToLocation(cursorWifi);
		} else {
			if (cursorBt.moveToFirst()) {
				ap = cursorToLocation(cursorBt);
			} else {
				if (cursorName.moveToFirst()) {
					ap = cursorToLocation(cursorName);
				}
			}
		}
		cursorWifi.close();
		cursorBt.close();
		return ap;
	}


	/**
	 * This method checks whether the bssid is available in TABLE_LOCATION table
	 * @param bssid location id
	 * @return true if location id is in location ebtry object
	 */
	public boolean hasLocationEntry (String mac, String deviceName) {
		boolean b1 = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + NSenseSQLiteHelper.TABLE_LOCATION + " WHERE " + NSenseSQLiteHelper.COLUMN_MAC_ADDRESS + "='" + mac + "'", null) != 0;
		boolean b2 = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + NSenseSQLiteHelper.TABLE_LOCATION + " WHERE " + NSenseSQLiteHelper.COLUMN_BT_MAC_ADDRESS + "='" + mac + "'", null) != 0;
		boolean b3 = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + NSenseSQLiteHelper.TABLE_LOCATION + " WHERE " + NSenseSQLiteHelper.COLUMN_DEVICE_NAME + "='" + deviceName + "'", null) != 0;
		return b1 || b2 || b3;
	}

	public boolean updateDistanceExpired(String mac, String deviceName) {
		LocationEntry entry = getLocationEntry(mac, deviceName);
		return ((SystemClock.elapsedRealtime() - entry.getLastUpdate()) > 140000);
	}

	/**
	 * This method gets the all the location entries recorded by the application on the TABLE_LOCATION table.
	 * @return mLocationMap A map with the LocationEntry objects.
	 */
	public Map<String, LocationEntry> getAllLocationEntries() {
		Map<String, LocationEntry> mLocationMap = new TreeMap<String, LocationEntry>();

		Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_LOCATION, allColumnsLocation, null, null, null, null, null);
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
	 * List of all columns on the Route table.
	 */
	public String[] allColumnsRoute = {
			NSenseSQLiteHelper.TABLE_ROUTE_COLUMN_DATE,
			NSenseSQLiteHelper.TABLE_ROUTE_COLUMN_LATITUDE ,
			NSenseSQLiteHelper.TABLE_ROUTE_COLUMN_LONGITUDE
	};

	/**
	 * This method clean the TABLE_LOCATION table
	 */
	public void cleanLocationTable() {
		db.execSQL("delete from "+ NSenseSQLiteHelper.TABLE_LOCATION);
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
			NSenseSQLiteHelper.KEY_ROWID,
			NSenseSQLiteHelper.SOUND_DATE ,
			NSenseSQLiteHelper.QUIET_MILLIS,
			NSenseSQLiteHelper.NORMAL_MILLIS,
			NSenseSQLiteHelper.ALERT_MILLIS,
			NSenseSQLiteHelper.NOISE_MILLIS
	};

	/**
	 * This method provides cursor to the TABLE_SOUND table
	 * @param cursor Cursor pointing to a record of the TABLE_SOUND table.
	 * @return sl SoundLevel
	 */
	private SoundLevel cursorToSound(Cursor cursor) {
		SoundLevel sl = new SoundLevel();
		sl.setSoundDate(cursor.getString(0));
		sl.setQuietTime(cursor.getLong(1));
		sl.setNormalTime(cursor.getLong(2));
		sl.setAlertTime(cursor.getLong(3));
		sl.setNoisyTime(cursor.getLong(4));
		return sl;
	}

	/**
	 * This method register the new entry into TABLE_SOUND table
	 * @param sl Sound level object
	 * @return numberOfRows number of rows are inserted in the table
	 */
	public long registerNewSoundLevel (SoundLevel sl) {
		ContentValues cv = new ContentValues();
		cv.put(NSenseSQLiteHelper.SOUND_DATE, sl.getSoundDate());
		cv.put(NSenseSQLiteHelper.QUIET_MILLIS, sl.getQuietTime());
		cv.put(NSenseSQLiteHelper.NORMAL_MILLIS, sl.getNormalTime());
		cv.put(NSenseSQLiteHelper.ALERT_MILLIS, sl.getAlertTime());
		cv.put(NSenseSQLiteHelper.NOISE_MILLIS, sl.getNoisyTime());
		return db.insert(NSenseSQLiteHelper.TABLE_SOUND, null, cv);
	}

	/**
	 * This method inserts a new sound registry on database
	 * @param date timestamp
	 * @param sound sound type
	 */
	public void insertSoundRegistry(String date, String sound) {
		ContentValues cv = new ContentValues();
		cv.put(NSenseSQLiteHelper.TABLE_MICROPHONE_DATE, date);
		cv.put(NSenseSQLiteHelper.TABLE_MICROPHONE_SOUND, sound);
		db.insert(NSenseSQLiteHelper.TABLE_MICROPHONE, null, cv);
	}

	/**
	 * This method returns the last sound type registry on database
	 * @return last sound type
	 */
	public String fetchLastSoundRegistry() {
		Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_MICROPHONE, null, null, null, null, null, null);
		cursor.moveToLast();
		return cursor.getString(1);
	}

	/**
	 * This method update the sound level into TABLE_SOUND table
	 * @param sl Sound level object
	 * @return true The number of rows are updated in the table
	 */
	public boolean updateSoundLevel(SoundLevel sl) {
		String identifier = NSenseSQLiteHelper.SOUND_DATE + "='" + sl.getSoundDate() + "'";
		ContentValues values = new ContentValues();
		values.put(NSenseSQLiteHelper.SOUND_DATE, sl.getSoundDate());
		values.put(NSenseSQLiteHelper.QUIET_MILLIS, sl.getQuietTime());
		values.put(NSenseSQLiteHelper.NORMAL_MILLIS, sl.getNormalTime());
		values.put(NSenseSQLiteHelper.ALERT_MILLIS, sl.getAlertTime());
		values.put(NSenseSQLiteHelper.NOISE_MILLIS, sl.getNoisyTime());

		int rows = db.update(NSenseSQLiteHelper.TABLE_SOUND, values,identifier, null);

		return ((rows != 0));
	}


	/**
	 * This method provides the sound level
	 * @param todayDate Time in milliseconds
	 * @return sl SoundLevel with time in milliseconds
	 */
	public SoundLevel getSoundLevel(String todayDate) {
		SoundLevel sl;
		Cursor cursor = db.query(NSenseSQLiteHelper.TABLE_SOUND, allColumnsSound, NSenseSQLiteHelper.SOUND_DATE + "='" + todayDate + "'", null, null, null, null, null);
		if (cursor.moveToFirst())
			sl = cursorToSound(cursor);
		else
			sl = null;
		cursor.close();
		return sl;
	}

	public double[] getLastLocation() {
		double[] lastLocation = new double[2];
		Cursor routeCursor = db.query(NSenseSQLiteHelper.TABLE_ROUTE, allColumnsRoute, null, null, null, null, null, null);
		if(routeCursor.moveToLast()) {
			lastLocation[0] = routeCursor.getDouble(1);
			lastLocation[1] = routeCursor.getDouble(2);
		}
		return lastLocation;
	}

	/**
	 * This method check whether the entry is available or not from TABLE_SOUND table
	 * @param soundDate Date of the sound level
	 * @return true If the entry is available
	 */
	public boolean hasSoundLevel (String soundDate) {
		return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + NSenseSQLiteHelper.TABLE_SOUND + " WHERE " + NSenseSQLiteHelper.SOUND_DATE + "='" + soundDate + "'", null) != 0;
	}

	private String mergedReportQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_DATE).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_OWN_DEVICE_NAME).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_CONNECTED_DEVICE_NAME).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_LATITUDE).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_LONGITUDE).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_DISTANCE).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_SOUND).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_PHYSICAL_ACTIVITY).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_TCT).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_SOCIAL_STRENGTH_MINUTE).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_SOCIAL_INTERACTION).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_PROPINQUITY).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_EMA_CD).append(", ");
		sb.append(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_INTERESTS).append(" ");
		sb.append("FROM ").append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(" ");
		sb.append("INNER JOIN ").append(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT).append(" ");
		sb.append("ON ").append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_DATE).append(" = ");
		sb.append(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_DATE).append(" AND ");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_CONNECTED_DEVICE_NAME).append(" = ");
		sb.append(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT).append(".");
		sb.append(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_DEVICE_NAME).append(";");
		return sb.toString();
	}

	/**
	 * This method inserts device info in database
	 * @param btMac bluetooth mac address
	 * @param deviceName bluetooth device name
	 */
	public void insertDeviceInfo(String deviceName, String btMac) {
		String identifier = NSenseSQLiteHelper.TABLE_OWN_DEVICE_INFO_BT_MAC + "='" + btMac + "'";
		ContentValues values = new ContentValues();
		values.put(NSenseSQLiteHelper.TABLE_OWN_DEVICE_INFO_BT_MAC, btMac);
		values.put(NSenseSQLiteHelper.TABLE_OWN_DEVICE_INFO_DEVICE_NAME, deviceName);
		if(db.update(NSenseSQLiteHelper.TABLE_OWN_DEVICE_INFO, values, identifier, null) == 0) {
			db.insert(NSenseSQLiteHelper.TABLE_OWN_DEVICE_INFO, null, values);
		}
	}

	/**
	 * This method inserts a new place and timestamp
	 * @param location place to be insert
	 */
	public void insertPlace(Location location) {
		ContentValues cv = new ContentValues();
		cv.put(NSenseSQLiteHelper.TABLE_ROUTE_COLUMN_DATE, location.getTime());
		cv.put(NSenseSQLiteHelper.TABLE_ROUTE_COLUMN_LATITUDE, location.getLatitude());
		cv.put(NSenseSQLiteHelper.TABLE_ROUTE_COLUMN_LONGITUDE, location.getLongitude());
		db.insert(NSenseSQLiteHelper.TABLE_ROUTE, null, cv);
	}

	/**
	 * This method returns the columns needed on the join between table devices and location
	 * @return columns needed on join
     */
	private String columnsOfJoinDevicesAndLocationTables() {
		StringBuilder sb = new StringBuilder();
		sb.append(NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS).append(",");
		sb.append(NSenseSQLiteHelper.COLUMN_BTDEV_NAME).append(",");
		sb.append(NSenseSQLiteHelper.COLUMN_DISTANCE).append(",");
		sb.append(NSenseSQLiteHelper.COLUMN_SW_NOW).append(",");
		sb.append(NSenseSQLiteHelper.COLUMN_SI).append(",");
		sb.append(NSenseSQLiteHelper.COLUMN_PROP).append(",");
		sb.append(NSenseSQLiteHelper.COLUMN_EMA_CD);
		return sb.toString();
	}

	private String queryJoinDevicesLocation() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT " + columnsOfJoinDevicesAndLocationTables());
		sb.append(" FROM " + NSenseSQLiteHelper.TABLE_BTDEVICE + " a INNER JOIN " + NSenseSQLiteHelper.TABLE_LOCATION + " b ");
		sb.append(" ON a." + NSenseSQLiteHelper.COLUMN_BTDEV_NAME + " = b." + NSenseSQLiteHelper.COLUMN_DEVICE_NAME);
		return sb.toString();
	}

	/**
	 * This method is used to fetch data to fill social report table
	 */
	public void insertDataOnSocialReportTable() {
		String deviceName = "Owner";
		Cursor devicesCursor = db.rawQuery(queryJoinDevicesLocation(), null);
		Cursor ownInfoCursor = db.query(NSenseSQLiteHelper.TABLE_OWN_DEVICE_INFO, null, null, null, null, null, null, null);

		if(ownInfoCursor.moveToFirst()) {
			deviceName = ownInfoCursor.getString(1);
		}

		for(devicesCursor.moveToFirst(), ownInfoCursor.moveToFirst(); !devicesCursor.isAfterLast(); devicesCursor.moveToNext()) {
			ContentValues values = new ContentValues();
			values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_DATE, DateUtils.getTimeNowAsString());
			values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_OWN_DEVICE_NAME, deviceName);
			values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_CONNECTED_DEVICE_NAME, devicesCursor.getString(1));
			if(devicesCursor.getString(2).equals(String.valueOf(LocationEntry.NA_DISTANCE_VALUE))) {
				values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_DISTANCE, "NA");
			} else {
				values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_DISTANCE, devicesCursor.getString(2));
			}
			values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_SOUND, fetchLastSoundRegistry());
			values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_PHYSICAL_ACTIVITY, fetchLastMotionRegistry());
			values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_TCT, getEncounterDurationNow(devicesCursor.getString(0)));
			values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_SOCIAL_STRENGTH_MINUTE, devicesCursor.getString(3));
			values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_SOCIAL_INTERACTION, devicesCursor.getString(4));
			values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_PROPINQUITY, devicesCursor.getString(5));
			values.put(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT_EMA_CD, devicesCursor.getString(6));
			db.insert(NSenseSQLiteHelper.TABLE_SOCIAL_REPORT, null, values);
		}
	}

	private String queryJoinDevicesBtSw() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(NSenseSQLiteHelper.COLUMN_BTDEV_NAME + ",");
		sb.append(NSenseSQLiteHelper.COLUMN_INTERESTS + ",");
		sb.append(NSenseSQLiteHelper.COLUMN_SW_NOW);
		sb.append(" FROM btdevices a INNER JOIN btdevice_socialweight b " + " ON a.devBtMacAdd = b.devBtMacAdd");
		return sb.toString();
	}

	/**
	 * This method is used to fetch data to fill interests report table
	 */
	public void insertDataOnInterestsReportTable() {
		String deviceName = "Owner";
		double[] lastLocation = getLastLocation();
		Cursor devicesCursor = db.rawQuery(queryJoinDevicesBtSw(), null);
		Cursor ownInfoCursor = db.query(NSenseSQLiteHelper.TABLE_OWN_DEVICE_INFO, null, null, null, null, null, null, null);
		Cursor routeCursor = db.query(NSenseSQLiteHelper.TABLE_ROUTE, allColumnsRoute, null, null, null, null, null, null);

		if(ownInfoCursor.moveToFirst()) {
			deviceName = ownInfoCursor.getString(1);
		}

		for (devicesCursor.moveToFirst(); !devicesCursor.isAfterLast(); devicesCursor.moveToNext()) {
			ContentValues values = new ContentValues();
			values.put(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_DATE, DateUtils.getTimeNowAsString());
			values.put(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_OWN_DEVICE_NAME, deviceName);
			values.put(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_LATITUDE, lastLocation[0]);
			values.put(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_LONGITUDE, lastLocation[1]);
			values.put(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_DEVICE_NAME, devicesCursor.getString(0));
			values.put(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_INTERESTS, devicesCursor.getString(1));
			values.put(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT_COLUMN_SW, devicesCursor.getString(2));
			db.insert(NSenseSQLiteHelper.TABLE_INTERESTS_REPORT, null, values);
		}

		ownInfoCursor.close();
		routeCursor.close();
		devicesCursor.close();

	}

	/**
	 * This method is used to fetch all data from interests report table
	 * @return interests report data
	 */
	public ArrayList<String[]> fetchInterestsReportTable() {
		ArrayList<String[]> reportData = new ArrayList<>();
		Cursor reportCursor = db.query(true, NSenseSQLiteHelper.TABLE_INTERESTS_REPORT, null, null, null, null, null, null, null);
		String[] columnNames = reportCursor.getColumnNames();
		columnNames[5] = InterestsUtils.getInterestsAsCsv();
		reportData.add(columnNames);
		for(reportCursor.moveToFirst(); !reportCursor.isAfterLast(); reportCursor.moveToNext()) {
			String[] data = new String[reportCursor.getColumnCount()];
			for(int i = 0; i < data.length; i++) {
				try {
					if (i == 5) {
						data[5] = InterestsUtils.getInterestsDataAsCsv(reportCursor.getString(i));
					} else {
						data[i] = reportCursor.getString(i);
					}
				} catch (NullPointerException e) {
					//data[5] = InterestsUtils.getInterestsDataAsCsv(",");
					data[i] = InterestsUtils.getInterestsDataAsCsv(",");
				}
			}
			reportData.add(data);
		}
		reportCursor.close();
		return reportData;
	}

	/**
	 * This method is used to fetch all data from social report table
	 * @return social report data
	 */
	public ArrayList<String[]> fetchSocialReportTable() {
		ArrayList<String[]> reportData = new ArrayList<>();
		Cursor reportCursor = db.query(true, NSenseSQLiteHelper.TABLE_SOCIAL_REPORT, null, null, null, null, null, null, null);
		reportData.add(reportCursor.getColumnNames());
		for(reportCursor.moveToFirst(); !reportCursor.isAfterLast(); reportCursor.moveToNext()) {
			String[] data = new String[reportCursor.getColumnCount()];
			for(int i = 0; i < data.length; i++)
				data[i] = reportCursor.getString(i);
			reportData.add(data);
		}
		reportCursor.close();
		return reportData;
	}

	/**
	 * This method returns the stars average
	 * @return stars average
	 */
	public ArrayList<SociabilityGraphItem> getStarsAvgValues() {
		ArrayList<SociabilityGraphItem> values = new ArrayList<>();
		Cursor cursor = db.rawQuery(
				"SELECT " + NSenseSQLiteHelper.TABLE_STARS_DATE +
				", AVG(" + NSenseSQLiteHelper.TABLE_STARS_SOCIAL_INTERACTION + ")" +
				", AVG(" + NSenseSQLiteHelper.TABLE_STARS_PROPINQUITY + ") " +
				"FROM " + NSenseSQLiteHelper.TABLE_STARS + " " +
				"GROUP BY " + NSenseSQLiteHelper.TABLE_STARS_DATE, null);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            values.add(new SociabilityGraphItem(cursor.getString(0), cursor.getFloat(1), cursor.getFloat(2)));
		}
		cursor.close();
		return values;
	}

	public void updateStars(String dayOfMonth, String deviceName, double siStars, double propStars) {
		if(siStars > 0 && propStars > 0) {
			ContentValues values = new ContentValues();
			String identifier = NSenseSQLiteHelper.TABLE_STARS_DATE + "='" + dayOfMonth + "' AND " +
					NSenseSQLiteHelper.TABLE_STARS_DEVICE_NAME + "='" + deviceName + "'";
			Cursor todaysData = db.query(NSenseSQLiteHelper.TABLE_STARS, null, identifier, null, null, null, null);
			if(todaysData.moveToFirst()) {
				values.put(NSenseSQLiteHelper.TABLE_STARS_SOCIAL_INTERACTION, siStars);
				values.put(NSenseSQLiteHelper.TABLE_STARS_PROPINQUITY, propStars);
				db.update(NSenseSQLiteHelper.TABLE_STARS, values, identifier, null);
			} else {
				values.put(NSenseSQLiteHelper.TABLE_STARS_DATE, dayOfMonth);
				values.put(NSenseSQLiteHelper.TABLE_STARS_DEVICE_NAME, deviceName);
				values.put(NSenseSQLiteHelper.TABLE_STARS_SOCIAL_INTERACTION, siStars);
				values.put(NSenseSQLiteHelper.TABLE_STARS_PROPINQUITY, propStars);
				db.insert(NSenseSQLiteHelper.TABLE_STARS, null, values);
			}
			todaysData.close();
		}
	}

	public ArrayList<SociabilityDetailItem> getStarsOfADay(String dayOfMonth, int type) {
		ArrayList<SociabilityDetailItem> items = new ArrayList<>();
		String identifier = NSenseSQLiteHelper.TABLE_STARS_DATE + "='" + dayOfMonth + "'";
		Cursor data = db.query(NSenseSQLiteHelper.TABLE_STARS, null, identifier, null, null, null, null);
		for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
			if(type == 0) {
				/** Social Interaction */
				items.add(new SociabilityDetailItem(data.getString(1), data.getDouble(2)));
			} else {
				/** Propinquity */
				items.add(new SociabilityDetailItem(data.getString(1), data.getDouble(3)));
			}
		}
		return items;
	}

	public void storeSocialInfoInDataBase(String deviceName, double si, double prop, double ema) {
		String identifier = NSenseSQLiteHelper.COLUMN_BTDEV_NAME + "='" + deviceName + "'";
		Cursor data = db.query(NSenseSQLiteHelper.TABLE_BTDEVICE, null, identifier, null, null, null, null);
		if(data.moveToFirst()) {
			ContentValues values = new ContentValues();

			if(si > 0.0) {
				values.put(NSenseSQLiteHelper.COLUMN_SI, si);
			} else {
				values.put(NSenseSQLiteHelper.COLUMN_SI, 0.0);
			}

			if(prop > 0.0){
				values.put(NSenseSQLiteHelper.COLUMN_PROP, prop);
			} else {
				values.put(NSenseSQLiteHelper.COLUMN_PROP, 0.0);
			}


			values.put(NSenseSQLiteHelper.COLUMN_EMA_CD, ema);
			db.update(NSenseSQLiteHelper.TABLE_BTDEVICE, values, identifier, null);
		}
		data.close();
	}

	public double getEmaCd(String btMacAddress) {
		String identifier = NSenseSQLiteHelper.COLUMN_BTDEV_MAC_ADDRESS + "='" + btMacAddress + "'";
		Cursor data = db.query(NSenseSQLiteHelper.TABLE_BTDEVICE, new String[] {NSenseSQLiteHelper.COLUMN_EMA_CD}, identifier, null, null, null, null);
		data.moveToFirst();
		return data.getDouble(0);
	}

	public ArrayList<String[]> fetchMergedReport() {
		ArrayList<String[]> reportData = new ArrayList<>();
		Cursor reportCursor = db.rawQuery(mergedReportQuery(), null);
		String[] columnNames = reportCursor.getColumnNames();
		columnNames[13] = InterestsUtils.getInterestsAsCsv();
		reportData.add(columnNames);
		for(reportCursor.moveToFirst(); !reportCursor.isAfterLast(); reportCursor.moveToNext()) {
			String[] data = new String[reportCursor.getColumnCount()];
			for(int i = 0; i < data.length; i++)
				try {
					if (i == 13) {
						data[13] = InterestsUtils.getInterestsDataAsCsv(reportCursor.getString(i));
					} else {
						data[i] = reportCursor.getString(i);
					}
				}  catch (NullPointerException e) {
					data[13] = InterestsUtils.getInterestsDataAsCsv(",");
				}
			reportData.add(data);
		}
		reportCursor.close();
		return reportData;
	}

}
