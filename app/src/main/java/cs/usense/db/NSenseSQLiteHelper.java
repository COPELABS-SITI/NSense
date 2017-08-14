/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for DataBase module.
 * This class provides helper methods to the NSenseDataSource.
 * @author Saeik Firdose (COPELABS/ULHT), 
 * @author Luis Lopes (COPELABS/ULHT), 
 * @author Waldir Moreira (COPELABS/ULHT), 
 * @author Reddy Pallavali (COPELABS/ULHT)
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cs.usense.pipelines.motion.MotionGlobals;
import cs.usense.pipelines.sound.SoundGlobals;

/**
 * This class provides various methods and information of the NSense Data base. 
 * Methods include, defining the tables, listing the columns, creating the tables, upgrading the tables 
 *
 */
public class NSenseSQLiteHelper extends SQLiteOpenHelper{

	public static final String DATABASE_NAME = "nsense.db";
	public static final int DATABASE_VERSION = 1;

	/** Interests Report */
	public static final String TABLE_INTERESTS_REPORT = "interests_report";
	public static final String TABLE_INTERESTS_REPORT_COLUMN_DATE = "date";
	public static final String TABLE_INTERESTS_REPORT_COLUMN_OWN_DEVICE_NAME = "own_device_name";
	public static final String TABLE_INTERESTS_REPORT_COLUMN_LATITUDE = "latitude";
	public static final String TABLE_INTERESTS_REPORT_COLUMN_LONGITUDE = "longitude";
	public static final String TABLE_INTERESTS_REPORT_COLUMN_DEVICE_NAME = "device_name";
	public static final String TABLE_INTERESTS_REPORT_COLUMN_INTERESTS = "interests";
	public static final String TABLE_INTERESTS_REPORT_COLUMN_SW = "sw";

	/** Social Report */
	public static final String TABLE_SOCIAL_REPORT = "social_report";
	public static final String TABLE_SOCIAL_REPORT_DATE = "date";
	public static final String TABLE_SOCIAL_REPORT_OWN_DEVICE_NAME = "own_device_name";
	public static final String TABLE_SOCIAL_REPORT_CONNECTED_DEVICE_NAME = "connected_device_name";
	public static final String TABLE_SOCIAL_REPORT_DISTANCE = "distance";
	public static final String TABLE_SOCIAL_REPORT_SOUND = "sound";
	public static final String TABLE_SOCIAL_REPORT_PHYSICAL_ACTIVITY = "physical_activity";
	public static final String TABLE_SOCIAL_REPORT_TCT = "tct";
	public static final String TABLE_SOCIAL_REPORT_SOCIAL_STRENGTH_MINUTE = "social_strength_minute";
	public static final String TABLE_SOCIAL_REPORT_SOCIAL_INTERACTION = "si";
	public static final String TABLE_SOCIAL_REPORT_PROPINQUITY = "p";
	public static final String TABLE_SOCIAL_REPORT_EMA_CD = "ema_cd";

	/** Stars per day */
	public static final String TABLE_STARS = "stars";
	public static final String TABLE_STARS_DATE = "date";
	public static final String TABLE_STARS_DEVICE_NAME = "device_name";
	public static final String TABLE_STARS_SOCIAL_INTERACTION = "social_interaction";
	public static final String TABLE_STARS_PROPINQUITY = "propinquity";

	/** Own Device Info */
	public static final String TABLE_OWN_DEVICE_INFO = "own_device_info";
	public static final String TABLE_OWN_DEVICE_INFO_BT_MAC = "own_bt_mac";
	public static final String TABLE_OWN_DEVICE_INFO_DEVICE_NAME = "bt_device_name";

	/** Route table */
	public static final String TABLE_ROUTE = "route";
	public static final String TABLE_ROUTE_COLUMN_DATE = "date";
	public static final String TABLE_ROUTE_COLUMN_LATITUDE = "latitude";
	public static final String TABLE_ROUTE_COLUMN_LONGITUDE = "longitude";

	/** MTracker */
	public static final String TABLE_ACCESS_POINTS = "accesspoints";

	/** Bluetooth pipeline */
	public static final String TABLE_BTDEVICE = "btdevices";
	public static final String TABLE_BTDEVICEENCOUNTERDURATION = "btdevice_encounterduration";
	public static final String TABLE_BTDEVICEAVERAGEENCOUNTERDURATION = "btdevice_averageencounterduration";
	public static final String TABLE_BTDEVICESOCIALWEIGHT = "btdevice_socialweight";

	/** LocationPipeline Table */
	public static final String TABLE_LOCATION = "location";

	/** Microphone */
	public static final String TABLE_SOUND = "environment_sound";

	/** Microphone Table */
	public static final String TABLE_MICROPHONE = "microphone";
	public static final String TABLE_MICROPHONE_DATE = "date";
	public static final String TABLE_MICROPHONE_SOUND = "sound";

	/** Microphone -> Table Sound Types */
	public static final String TABLE_SOUND_TYPE = "sound_type";
	public static final String TABLE_SOUND_TYPE_SOUND = "sound";

	/** LocationPipeline */
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_BSSID = "bssid";

	/** Accelerometer table */
	public static final String TABLE_ACCELEROMETER = "accelerometer";
	public static final String TABLE_ACCELEROMETER_DATE = "data";
	public static final String TABLE_ACCELEROMETER_ACTION = "action";

	/** Accelerometer -> Table Actions */
	public static final String TABLE_ACTIONS = "actions";
	public static final String ACTIONS_COLUMN_ACTION_TYPE = "action_type";
	public static final String ACTIONS_COLUMN_DURATION = "duration";
	public static final String ACTIONS_COLUMN_HOUR = "hour";
	public static final String ACTIONS_COLUMN_ACTIONCOUNTER = "action_counter";
	public static final String ACTIONS_COLUMN_DATE = "date";
	public static final String ACTIONS_COLUMN_TIMEFRAME = "time_frame";

	/** Accelerometer -> Table Action Types */
	public static final String TABLE_ACTION_TYPE = "action_type";
	public static final String ACTION_TYPE_COLUMN_ACTION_NAME = "action_name";

	/** Accelerometer -> Table Time Frame */
	public static final String TABLE_TIME_FRAME = "time_frame";
	public static final String TIME_FRAME_COLUMN_TIME_FRAME_NAME = "time_frame_name";

	/** Bluetooth pipeline */
	public static final String COLUMN_BTDEV_MAC_ADDRESS = "devBtMacAdd";
	public static final String COLUMN_BTDEV_NAME = "deviceName";
	public static final String COLUMN_BTDEV_ENCOUNTERSTART = "devEncounterStart";
	public static final String COLUMN_INTERESTS = "interests";
	public static final String COLUMN_SW_NOW = "sw";
	public static final String COLUMN_SI = "si";
	public static final String COLUMN_PROP = "prop";
	public static final String COLUMN_EMA_CD = "ema_cd";
	public static final String COLUMN_ACTIVE_DEVICE = "active_device";

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

	/** LocationPipeline Table */
	public static final String COLUMN_MAC_ADDRESS = "bssid";
	public static final String COLUMN_BT_MAC_ADDRESS = "btmac";
	public static final String COLUMN_DEVICE_NAME = "name";
	public static final String COLUMN_DISTANCE = "distance";
	public static final String COLUMN_LAST_UPDATE = "lastupdate";
	public static final String COLUMN_WIFI_UPDATE = "isWifiUpdate";

	/** Outdoor LocationPipeline Table */
	public static final String COLUMN_LATITUDE = "lat";
	public static final String COLUMN_LONGITUDE = "lng";
	public static final String COLUMN_DURATION = "duration";
	public static final String COLUMN_DAY = "day";
	public static final String COLUMN_HOUR = "hour";

	/** Microphone */
	public static final String KEY_ROWID = "key_row_id";
	public static final String SOUND_DATE = "date";
	public static final String QUIET_MILLIS = "quiet_millis";
	public static final String NORMAL_MILLIS = "normal_millis";
	public static final String ALERT_MILLIS = "alert_millis";
	public static final String NOISE_MILLIS = "noise_millis";

	/** Acceleromter table */
	private static final String CREATE_ACCELEROMETER_TABLE = "create table "
			+ TABLE_ACCELEROMETER + "("
			+ TABLE_ACCELEROMETER_DATE + " text primary key, "
			+ TABLE_ACCELEROMETER_ACTION + " text default NA, "
			+ "FOREIGN KEY(" + TABLE_ACCELEROMETER_ACTION + ") REFERENCES "
			+ TABLE_ACTION_TYPE + "("+ ACTION_TYPE_COLUMN_ACTION_NAME +") "
			+ ");";

	/** Actions */
	private static final String CREATE_ACTIONS_TABLE = "create table "
			+ TABLE_ACTIONS + "("
			+ ACTIONS_COLUMN_ACTION_TYPE + " text not null, "
			+ ACTIONS_COLUMN_DURATION + " integer not null, "
			+ ACTIONS_COLUMN_HOUR + " integer not null, "
			+ ACTIONS_COLUMN_ACTIONCOUNTER + " integer not null, "
			+ ACTIONS_COLUMN_DATE + " text not null, "
			+ ACTIONS_COLUMN_TIMEFRAME + " text not null, "
			+ "FOREIGN KEY(" + ACTIONS_COLUMN_ACTION_TYPE + ") REFERENCES "
			+ TABLE_ACTION_TYPE + "("+ ACTION_TYPE_COLUMN_ACTION_NAME +"), "
			+ "FOREIGN KEY(" + ACTIONS_COLUMN_TIMEFRAME + ") REFERENCES "
			+ TABLE_TIME_FRAME + "("+ TIME_FRAME_COLUMN_TIME_FRAME_NAME +"),"
			+ "PRIMARY KEY (" + ACTIONS_COLUMN_ACTION_TYPE +", " + ACTIONS_COLUMN_HOUR + ")"
			+ ");";

	/** Action_Types */
	private static final String CREATE_ACTIONS_TYPE_TABLE = "create table "
			+ TABLE_ACTION_TYPE + "("
			+ ACTION_TYPE_COLUMN_ACTION_NAME + " text primary key "
			+ ");";

	/** Stars */
	private static final String CREATE_STARS_TABLE = "create table "
			+ TABLE_STARS + "("
			+ TABLE_STARS_DATE + " text, "
			+ TABLE_STARS_DEVICE_NAME + " text, "
			+ TABLE_STARS_SOCIAL_INTERACTION + " real, "
			+ TABLE_STARS_PROPINQUITY + " real, "
			+ "PRIMARY KEY (" + TABLE_STARS_DATE + ", " + TABLE_STARS_DEVICE_NAME + ")"
			+ ");";

	/** Own device info */
	private static final String CREATE_OWN_DEVICE_INFO_TABLE = "create table "
			+ TABLE_OWN_DEVICE_INFO + "("
			+ TABLE_OWN_DEVICE_INFO_BT_MAC + " text primary key, "
			+ TABLE_OWN_DEVICE_INFO_DEVICE_NAME + " text "
			+ ");";

	/** Time_Frame */
	private static final String CREATE_TIME_FRAMES_TABLE = "create table "
			+ TABLE_TIME_FRAME + "("
			+ TIME_FRAME_COLUMN_TIME_FRAME_NAME + " text primary key "
			+ ");";

	/** Fill domain tables with data */
	private void fillDomainTables(SQLiteDatabase database, String table, String value) {
		String INSERT_INTO = "insert into " + table + " values ('" + value + "');";
		database.execSQL(INSERT_INTO);
	}

	/** Bluetooth pipeline */
	private static final String CREATE_BTDEVICE_TABLE = "create table "
			+ TABLE_BTDEVICE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BTDEV_MAC_ADDRESS + " text not null, "
			+ COLUMN_BTDEV_NAME + " text not null, "
			+ COLUMN_BTDEV_ENCOUNTERSTART + " text not null, "
			+ COLUMN_INTERESTS + " text,"
			+ COLUMN_SW_NOW + " text default 0.0, "
			+ COLUMN_SI + " text default 0.0, "
			+ COLUMN_PROP + " text default 0.0, "
			+ COLUMN_EMA_CD + " text default 0.0, "
			+ COLUMN_ACTIVE_DEVICE + " integer default 1 "
			+ ");";

	private static final String CREATE_BTDEVICEENCOUNTERDURATION_TABLE = "create table "
			+ TABLE_BTDEVICEENCOUNTERDURATION + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_BTDEV_MAC_ADDRESS + " text not null, "
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
			+ COLUMN_BTDEV_MAC_ADDRESS + " text not null, "
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
			+ COLUMN_BTDEV_MAC_ADDRESS + " text not null, "
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


	/** Interests Report */
	private static final String CREATE_INTERESTS_REPORT_TABLE = "create table "
			+ TABLE_INTERESTS_REPORT + "("
			+ TABLE_INTERESTS_REPORT_COLUMN_DATE + " text, "
			+ TABLE_INTERESTS_REPORT_COLUMN_OWN_DEVICE_NAME + " text default OWNER, "
			+ TABLE_INTERESTS_REPORT_COLUMN_LATITUDE + " text default 0.0, "
			+ TABLE_INTERESTS_REPORT_COLUMN_LONGITUDE + " text default 0.0, "
			+ TABLE_INTERESTS_REPORT_COLUMN_DEVICE_NAME + " text default NA, "
			+ TABLE_INTERESTS_REPORT_COLUMN_INTERESTS + " text default NA, "
			+ TABLE_INTERESTS_REPORT_COLUMN_SW + " text default 0.0, "
			+ "PRIMARY KEY (" + TABLE_INTERESTS_REPORT_COLUMN_DATE +", "
				+ TABLE_INTERESTS_REPORT_COLUMN_DEVICE_NAME + ")"
			+ ");";

	/** Social Report */
	private static final String CREATE_INTERESTS_SOCIAL_TABLE = "create table "
			+ TABLE_SOCIAL_REPORT + "("
			+ TABLE_SOCIAL_REPORT_DATE + " text, "
			+ TABLE_SOCIAL_REPORT_OWN_DEVICE_NAME + " text default OWNER, "
			+ TABLE_SOCIAL_REPORT_CONNECTED_DEVICE_NAME + " text default CONNECTED_DEVICE, "
			+ TABLE_SOCIAL_REPORT_DISTANCE + " text default 0.0, "
			+ TABLE_SOCIAL_REPORT_SOUND + " text default NA, "
			+ TABLE_SOCIAL_REPORT_PHYSICAL_ACTIVITY + " text default NA, "
			+ TABLE_SOCIAL_REPORT_TCT + " text default 0.0, "
			+ TABLE_SOCIAL_REPORT_SOCIAL_STRENGTH_MINUTE + " text default 0.0, "
			+ TABLE_SOCIAL_REPORT_SOCIAL_INTERACTION + " text default 0.0, "
			+ TABLE_SOCIAL_REPORT_PROPINQUITY + " text default 0.0, "
			+ TABLE_SOCIAL_REPORT_EMA_CD + " text default 0.0, "
			+ "PRIMARY KEY (" + TABLE_SOCIAL_REPORT_DATE +", "
			+ TABLE_SOCIAL_REPORT_CONNECTED_DEVICE_NAME + ")"
			+ ");";


	/** Route table */
	private static final String CREATE_ROUTE_TABLE = "create table "
			+ TABLE_ROUTE + "("
			+ TABLE_ROUTE_COLUMN_DATE + " long primary key, "
			+ TABLE_ROUTE_COLUMN_LATITUDE + " real, "
			+ TABLE_ROUTE_COLUMN_LONGITUDE + " real "
			+ ");";

	/** LocationPipeline Table */
	private static final String CREATE_LOCATION_TABLE = "create table "
			+ TABLE_LOCATION + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_MAC_ADDRESS + " text, "
			+ COLUMN_BT_MAC_ADDRESS + " text, "
			+ COLUMN_DEVICE_NAME + " text not null, "
			+ COLUMN_DISTANCE + " double not null, "
			+ COLUMN_LAST_UPDATE + " text not null default 0,"
			+ COLUMN_WIFI_UPDATE + " text not null default 1"
			+ ");";

	/** Microphone table */
	private static final String CREATE_MICROPHONE_TABLE = "create table "
			+ TABLE_MICROPHONE + "("
			+ TABLE_MICROPHONE_DATE + " text not null, "
			+ TABLE_MICROPHONE_SOUND + " text not null,"
			+ "FOREIGN KEY(" + TABLE_MICROPHONE_SOUND + ") REFERENCES "
			+ TABLE_SOUND_TYPE + "("+ TABLE_SOUND_TYPE_SOUND +") "
			+ ");";

	/** Microphone */
	private static final String CREATE_SOUND_TABLE = "create table "
			+ TABLE_SOUND + "("
			+ KEY_ROWID + " integer primary key autoincrement, "
			+ SOUND_DATE + " VARCHAR(128) not null,"
			+ QUIET_MILLIS + " long, " 
			+ NORMAL_MILLIS + " long, "
			+ ALERT_MILLIS + " long, "
			+ NOISE_MILLIS + " long "
			+ ");";

	/** Sound Types */
	private static final String CREATE_SOUND_TYPE_TABLE = "create table "
			+ TABLE_SOUND_TYPE + "("
			+ TABLE_SOUND_TYPE_SOUND + " text primary key "
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
	public void onCreate(SQLiteDatabase dataBase) {

		/** Own device info */
		dataBase.execSQL(CREATE_OWN_DEVICE_INFO_TABLE);

		/** stars tables */
		dataBase.execSQL(CREATE_STARS_TABLE);

		/** Reports */
		dataBase.execSQL(CREATE_INTERESTS_REPORT_TABLE);
		dataBase.execSQL(CREATE_INTERESTS_SOCIAL_TABLE);

		/** Route Table */
		dataBase.execSQL(CREATE_ROUTE_TABLE);

		/** Accelerometer Pipeline */
		dataBase.execSQL(CREATE_ACTIONS_TABLE);
		dataBase.execSQL(CREATE_ACCELEROMETER_TABLE);
		dataBase.execSQL(CREATE_ACTIONS_TYPE_TABLE);
		dataBase.execSQL(CREATE_TIME_FRAMES_TABLE);

		/** Bluetooth pipeline */
		dataBase.execSQL(CREATE_BTDEVICE_TABLE);
		dataBase.execSQL(CREATE_BTDEVICEENCOUNTERDURATION_TABLE);
		dataBase.execSQL(CREATE_BTDEVICEAVERAGEENCOUNTERDURATION_TABLE);
		dataBase.execSQL(CREATE_BTDEVICESOCIALWEIGHT_TABLE);

		/** LocationPipeline */
		dataBase.execSQL(CREATE_LOCATION_TABLE);

		/** Microphone */
		dataBase.execSQL(CREATE_SOUND_TABLE);
		dataBase.execSQL(CREATE_SOUND_TYPE_TABLE);
		dataBase.execSQL(CREATE_MICROPHONE_TABLE);

		/** Initialize actions domain */
		for(int i = 0; i < MotionGlobals.ACTIVITY_TYPES.length; i++) {
			fillDomainTables(dataBase, TABLE_ACTION_TYPE, MotionGlobals.ACTIVITY_TYPES[i]);
		}

		/** Initialize actions domain */
		for(int i = 0; i < MotionGlobals.TIME_FRAME_TYPES.length; i++) {
			fillDomainTables(dataBase, TABLE_TIME_FRAME, MotionGlobals.TIME_FRAME_TYPES[i]);
		}

		for(int i = 0; i < SoundGlobals.SOUND_TYPES.length; i++) {
			fillDomainTables(dataBase, TABLE_SOUND_TYPE, SoundGlobals.SOUND_TYPES[i]);
		}
	}

	/**
	 * This method update the SQLiteDatabase with the predefined tables
	 * @param dataBase SQLiteDatabase
	 * @param oldVersion Old version of SQLiteDatabase
	 * @param newVersion New version of SQLiteDatabase
	 */
	public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCESS_POINTS);
		/** Firdose */
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIONS);
		/** Bluetooth pipeline */
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICE);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICEENCOUNTERDURATION);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICEAVERAGEENCOUNTERDURATION);
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_BTDEVICESOCIALWEIGHT);

		/** LocationPipeline */
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);

		/** Microphone */
		dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_SOUND);

		onCreate(dataBase);
	}

}