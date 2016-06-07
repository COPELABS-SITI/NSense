/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for DataBase module.
 * This class provides helper methods to the NSenseDataSource.
 * @author Saeik Firdose (COPELABS/ULHT), 
 * @author Luis Lopes (COPELABS/ULHT), 
 * @author Waldir Moreira (COPELABS/ULHT), 
 * @author Reddy Pallavali (COPELABS/ULHT)
 */
package cs.usense.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class provides various methods and information of the NSense Data base. 
 * Methods include, defining the tables, listing the columns, creating the tables, upgrading the tables 
 *
 */
public class NSenseSQLiteHelper extends SQLiteOpenHelper{

	public static final String DATABASE_NAME = "nsense.db";
	public static final int DATABASE_VERSION = 1;

	/** MTracker */
	public static final String TABLE_ACCESSPOINTS = "accesspoints";
	/** Firdose */
	public static final String TABLE_ACTIONS = "actions";
	/** Bluetooth pipeline */
	public static final String TABLE_BTDEVICE = "btdevices";
	public static final String TABLE_BTDEVICEENCOUNTERDURATION = "btdevice_encounterduration";
	public static final String TABLE_BTDEVICEAVERAGEENCOUNTERDURATION = "btdevice_averageencounterduration";
	public static final String TABLE_BTDEVICESOCIALWEIGHT = "btdevice_socialweight";
	/** Location Table */
	public static final String TABLE_LOCATION = "location";

	/** Microphone */
	public static final String TABLE_SOUND = "environment_sound";

	/** Location */
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_BSSID = "bssid";

	/** Accelerometer */
	public static final String COLUMN_ACTIONTYPE = "actiontype";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_ENDTIMESTAMP = "endtimestamp";
	public static final String COLUMN_AVGDURATION = "avgduration";
	public static final String COLUMN_ACTIONCOUNTER = "actioncounter";
	public static final String COLUMN_TIMEFRAME = "timeframe";

	/** Bluetooth pipeline */
	public static final String COLUMN_BTDEV_MAC_ADDRESS = "devBtMacAdd";
	public static final String COLUMN_BTDEV_NAME = "devName";
	public static final String COLUMN_BTDEV_ENCOUNTERSTART = "devEncounterStart";
	/**	Encounter Duration */
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1= "devEncounterDuration_slot1";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2= "devEncounterDuration_slot2";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3= "devEncounterDuration_slot3";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4= "devEncounterDuration_slot4";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5= "devEncounterDuration_slot5";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6= "devEncounterDuration_slot6";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7= "devEncounterDuration_slot7";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8= "devEncounterDuration_slot8";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9= "devEncounterDuration_slot9";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10= "devEncounterDuration_slot10";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11= "devEncounterDuration_slot11";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12= "devEncounterDuration_slot12";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13= "devEncounterDuration_slot13";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14= "devEncounterDuration_slot14";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15= "devEncounterDuration_slot15";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16= "devEncounterDuration_slot16";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17= "devEncounterDuration_slot17";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18= "devEncounterDuration_slot18";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19= "devEncounterDuration_slot19";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20= "devEncounterDuration_slot20";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21= "devEncounterDuration_slot21";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22= "devEncounterDuration_slot22";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23= "devEncounterDuration_slot23";
	public static final String COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24= "devEncounterDuration_slot24";
	
	/** Average Encounter Duration */
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1= "devAvgEncounterDuration_slot1";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2= "devAvgEncounterDuration_slot2";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3= "devAvgEncounterDuration_slot3";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4= "devAvgEncounterDuration_slot4";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5= "devAvgEncounterDuration_slot5";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6= "devAvgEncounterDuration_slot6";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7= "devAvgEncounterDuration_slot7";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8= "devAvgEncounterDuration_slot8";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9= "devAvgEncounterDuration_slot9";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10= "devAvgEncounterDuration_slot10";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11= "devAvgEncounterDuration_slot11";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12= "devAvgEncounterDuration_slot12";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13= "devAvgEncounterDuration_slot13";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14= "devAvgEncounterDuration_slot14";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15= "devAvgEncounterDuration_slot15";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16= "devAvgEncounterDuration_slot16";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17= "devAvgEncounterDuration_slot17";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18= "devAvgEncounterDuration_slot18";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19= "devAvgEncounterDuration_slot19";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20= "devAvgEncounterDuration_slot20";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21= "devAvgEncounterDuration_slot21";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22= "devAvgEncounterDuration_slot22";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23= "devAvgEncounterDuration_slot23";
	public static final String COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24= "devAvgEncounterDuration_slot24";
	
	/** Social Weight */
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT1= "devSocialWeight_slot1";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT2= "devSocialWeight_slot2";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT3= "devSocialWeight_slot3";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT4= "devSocialWeight_slot4";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT5= "devSocialWeight_slot5";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT6= "devSocialWeight_slot6";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT7= "devSocialWeight_slot7";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT8= "devSocialWeight_slot8";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT9= "devSocialWeight_slot9";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT10= "devSocialWeight_slot10";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT11= "devSocialWeight_slot11";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT12= "devSocialWeight_slot12";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT13= "devSocialWeight_slot13";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT14= "devSocialWeight_slot14";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT15= "devSocialWeight_slot15";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT16= "devSocialWeight_slot16";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT17= "devSocialWeight_slot17";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT18= "devSocialWeight_slot18";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT19= "devSocialWeight_slot19";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT20= "devSocialWeight_slot20";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT21= "devSocialWeight_slot21";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT22= "devSocialWeight_slot22";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT23= "devSocialWeight_slot23";
	public static final String COLUMN_BTDEV_SOCIALWEIGHT_SLOT24= "devSocialWeight_slot24";

	/** Location Table */
	public static final String COLUMN_MAC_ADDRESS = "bssid";
	public static final String COLUMN_DEVICE_NAME = "name";
	public static final String COLUMN_DISTANCE = "distance";
	public static final String COLUMN_LAST_UPDATE = "lastupdate";

	/** Outdoor Location Table */
	public static final String COLUMN_LATITUDE = "lat";
	public static final String COLUMN_LONGITUDE = "lng";
	public static final String COLUMN_DURATION = "duration";
	public static final String COLUMN_DAY = "day";
	public static final String COLUMN_HOUR = "hour";

	/** Microphone */
	public static final String KEY_ROWID = "key_row_id";
	public static final String SOUND_DATE = "date_millis";
	public static final String QUIET_MILLIS = "quiet_millis";
	public static final String NORMAL_MILLIS = "normal_millis";
	public static final String ALERT_MILLIS = "alert_millis";
	public static final String NOISE_MILLIS = "noise_millis";

	/** Firdose */
	private static final String CREATE_ACTIONS_TABLE = "create table "
			+ TABLE_ACTIONS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_ACTIONTYPE + " text not null, "
			+ COLUMN_TIMESTAMP + " text not null, "
			+ COLUMN_ENDTIMESTAMP + " text not null, "
			+ COLUMN_AVGDURATION + " double not null, "
			+ COLUMN_HOUR + " integer not null, "
			+ COLUMN_ACTIONCOUNTER + " integer not null, "
			+ COLUMN_DAY + " integer not null, "
			+ COLUMN_TIMEFRAME + " text not null"
			+ ");";

	/** Bluetooth pipeline */
	private static final String CREATE_BTDEVICE_TABLE = "create table "
			+ TABLE_BTDEVICE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BTDEV_MAC_ADDRESS + " text not null unique, "
			+ COLUMN_BTDEV_NAME + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERSTART + " text not null"
			+ ");";

	private static final String CREATE_BTDEVICEENCOUNTERDURATION_TABLE = "create table "
			+ TABLE_BTDEVICEENCOUNTERDURATION + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BTDEV_MAC_ADDRESS + " text not null unique, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT1 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT2 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT3 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT4 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT5 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT6 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT7 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT8 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT9 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT10 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT11 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT12 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT13 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT14 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT15 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT16 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT17 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT18 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT19 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT20 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT21 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT22 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT23 + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERDURATION_SLOT24 + " text not null"
			+ ");";

	private static final String CREATE_BTDEVICEAVERAGEENCOUNTERDURATION_TABLE = "create table "
			+ TABLE_BTDEVICEAVERAGEENCOUNTERDURATION + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BTDEV_MAC_ADDRESS + " text not null unique, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT1 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT2 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT3 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT4 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT5 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT6 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT7 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT8 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT9 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT10 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT11 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT12 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT13 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT14 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT15 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT16 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT17 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT18 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT19 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT20 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT21 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT22 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT23 + " text not null, "
			+ COLUMN_BTDEV_AVGENCOUNTERDURATION_SLOT24 + " text not null"
			+ ");";

	private static final String CREATE_BTDEVICESOCIALWEIGHT_TABLE = "create table "
			+ TABLE_BTDEVICESOCIALWEIGHT + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BTDEV_MAC_ADDRESS + " text not null unique, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT1 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT2 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT3 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT4 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT5 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT6 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT7 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT8 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT9 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT10 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT11 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT12 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT13 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT14 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT15 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT16 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT17 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT18 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT19 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT20 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT21 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT22 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT23 + " text not null, "
			+ COLUMN_BTDEV_SOCIALWEIGHT_SLOT24 + " text not null"
			+ ");";

	/** Location Table */
	private static final String CREATE_LOCATION_TABLE = "create table "
			+ TABLE_LOCATION + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_MAC_ADDRESS + " text not null unique, "
			+ COLUMN_DEVICE_NAME + " text not null, "
			+ COLUMN_DISTANCE + " double not null, "
			+ COLUMN_LAST_UPDATE + " text not null"
			+ ");";

	/** Microphone */
	private static final String CREATE_SOUND_TABLE = "create table "
			+ TABLE_SOUND + "("
			+ KEY_ROWID + " integer primary key autoincrement, "
			+ SOUND_DATE + " long not null,"
			+ QUIET_MILLIS + " long, " 
			+ NORMAL_MILLIS + " long, "
			+ ALERT_MILLIS + " long, "
			+ NOISE_MILLIS + " long "
			+ ");";

	/**
	 * This method construct the NSenseSQLiteHelper with the help of Android Context
	 * @param context Interface to global information about an application environment.
	 */
	public NSenseSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	/**
	 * This method create the SQLiteDatabase with the predefined tables
	 * @param dataBase SQLiteDatabase
	 */
	public void onCreate(SQLiteDatabase dataBase)
	{
		/** Firdose */
		dataBase.execSQL(CREATE_ACTIONS_TABLE);
		/** Bluetooth pipeline */
		dataBase.execSQL(CREATE_BTDEVICE_TABLE);
		dataBase.execSQL(CREATE_BTDEVICEENCOUNTERDURATION_TABLE);
		dataBase.execSQL(CREATE_BTDEVICEAVERAGEENCOUNTERDURATION_TABLE);
		dataBase.execSQL(CREATE_BTDEVICESOCIALWEIGHT_TABLE);

		/** Location */
		dataBase.execSQL(CREATE_LOCATION_TABLE);

		/** Microphone */
		dataBase.execSQL(CREATE_SOUND_TABLE);

	}

	/**
	 * This method update the SQLiteDatabase with the predefined tables
	 * @param database SQLiteDatabase
	 * @param oldVersion Old version of SQLiteDatabase
	 * @param newVersion New version of SQLiteDatabase
	 */
	public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion)
	{
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCESSPOINTS);
		/** Firdose */
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIONS);
		/** Bluetooth pipeline */
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICE);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICEENCOUNTERDURATION);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICEAVERAGEENCOUNTERDURATION);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICESOCIALWEIGHT);

		/** Location */
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);

		/** Microphone */
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_SOUND);

		onCreate(dataBase);
	}


}
