/**
 *  Copyright (C) 2013 ULHT
 *  Author(s): jonnahtan.saltarin@ulusofona.pt
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by  the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 * ULOOP Mobility tracking plugin: Mtracker
 *
 * Mtracker is an Android app that collects information concerning visited APs
 * It computes a probingFunctionsManager and then estimates a potential handover - time and target AP
 * v1.0 - pre-prototype, D3.3, July 2012
 * v2.0 - prototype on September 2012 - D3.6
 * v3.0 - prototype on June 2013
 *
 * @author Jonnahtan Saltarin
 * @author Rute Sofia
 * @author Christian da Silva Pereira
 * @author Luis Amaral Lopes
 *
 * @version 3.0
 *
 * @file Contains MTrackerDataSource class. This class provides methods to insert, update and
 * query the application database. It also provide methods to compute certain values, like the
 * ProbingFunctionsManager and the Stationary Time, among others.
 *
 */

package cs.usense.pipelines.mobility.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cs.usense.pipelines.mobility.models.MTrackerAP;
import cs.usense.pipelines.mobility.models.MTrackerVisit;

/**
 * This class provides methods to insert, update and
 * query the application database. It also provide
 * methods to compute certain values, like the
 * ProbingFunctionsManager and the Stationary Time, among others.
 *
 * @author Jonnahtan Saltarin (ULHT)
 * @author Rute Sofia (ULHT)
 * @author Christian da Silva Pereira (ULHT)
 * @author Luis Amaral Lopes (ULHT)
 *
 * @version 3.0
 *
 *
 */
public class MTrackerDataSource {
	
	/*
	 * COMMON OPERATIONS
	 */
	private SQLiteDatabase db;
	private MTrackerSQLiteHelper dbHelper;
	private boolean isDbOpen;
	private GregorianCalendar cal;
	private static final String TAG = MTrackerDataSource.class.getSimpleName();
	
	/**
	 * Constructor that takes Android Context as input.
	 * 
	 * @param context
	 */
	public MTrackerDataSource (Context context) {
		dbHelper = new MTrackerSQLiteHelper(context);
		isDbOpen = false;
		cal = new GregorianCalendar();

	}

	/**
	 * Opens the predefined MTracker database.
	 * 
	 * @param writable
	 * @throws SQLException
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
	 * Close the predefined MTracker database.
	 */
	public void closeDB() {
		dbHelper.close();
		isDbOpen = false;
	}
	
	/*
	 * ACCESS_POINTS TABLE: This table stores information
	 * regarding each access point visited in the past.
	 * The information stored is:
	 * 		- SSID of the network
	 * 		- BSSID of the network
	 * 		- Attractiveness of the network.
	 *      - IP of the gateway.
	 */
	
	
	
	/**
	 * List of all columns on the ACCESS_POINTS table.
	 */
	private String[] allColumnsAccessPoint = {
			MTrackerSQLiteHelper.COLUMN_BSSID,
			MTrackerSQLiteHelper.COLUMN_SSID,
			MTrackerSQLiteHelper.COLUMN_ATTRACTIVENESS,
			MTrackerSQLiteHelper.COLUMN_LASTGATEWAYIP,
			MTrackerSQLiteHelper.COLUMN_TIMEDOWNLOAD,
			MTrackerSQLiteHelper.COLUMN_DEVICESONNETWORK,
			MTrackerSQLiteHelper.COLUMN_REJECTIONS,
			MTrackerSQLiteHelper.COLUMN_REJECTED
	};

	/**
	 * List of all columns on the RANKING_TABLE table.
	 */
	private String[] allColumnsRanking = {
			MTrackerSQLiteHelper.COLUMN_BSSID,
			MTrackerSQLiteHelper.COLUMN_SSID,
			MTrackerSQLiteHelper.COLUMN_ATTRACTIVENESS,
			MTrackerSQLiteHelper.COLUMN_LASTGATEWAYIP,
			MTrackerSQLiteHelper.COLUMN_TIMEDOWNLOAD,
			MTrackerSQLiteHelper.COLUMN_DEVICESONNETWORK,
			MTrackerSQLiteHelper.COLUMN_REJECTIONS,
			MTrackerSQLiteHelper.COLUMN_VISIT_DURATION,
			MTrackerSQLiteHelper.COLUMN_VISIT_GAP,
			MTrackerSQLiteHelper.COLUMN_GANMA,
			MTrackerSQLiteHelper.COLUMN_VISITS,
			MTrackerSQLiteHelper.COLUMN_RANK,
			MTrackerSQLiteHelper.COLUMN_QUALITY,
			MTrackerSQLiteHelper.COLUMN_CONNECTION,
			MTrackerSQLiteHelper.COLUMN_RECOMMENCDATION,
			MTrackerSQLiteHelper.COLUMN_NUM_RECOMMENDATIONS,
			MTrackerSQLiteHelper.COLUMN_FUNCTION,
			MTrackerSQLiteHelper.COLUMN_GAMMA_GAP,
			MTrackerSQLiteHelper.COLUMN_GAMMA_RANk,
			MTrackerSQLiteHelper.COLUMN_TIME,
			MTrackerSQLiteHelper.COLUMN_DATE,
			MTrackerSQLiteHelper.COLUMN_BATTERY
	};
	
	/**
	 * Converts a cursor pointing to a record in the ACCESS_POINTS table to a MTrackerAP object.
	 * 
	 * @param cursor Cursor pointing to a record of the ACESS_POINTS table.
	 * @return the MTrackerAP object
	 */
	private MTrackerAP cursorToAP(Cursor cursor) {
		MTrackerAP ap = new MTrackerAP();
		ap.setBSSID(cursor.getString(0));
		ap.setSSID(cursor.getString(1));
		ap.setAttractiveness(cursor.getDouble(2));
		ap.setLastGatewayIp(cursor.getInt(3));
		ap.setNetworkUtilization(cursor.getLong(4));
		ap.setNetworkUtilization(cursor.getLong(5));
		ap.setRejections(cursor.getInt(6));
		ap.setRejected(cursor.getInt(7));
		return ap;
	}

	public synchronized long registerNewRank (MTrackerAP ap, long connectionUptime, long gapConnection, float gamma, float gammaGap, int function, double rank, double gammaRank, String time, String date, float battery) {

		ContentValues values = new ContentValues();
		values.put(MTrackerSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
		values.put(MTrackerSQLiteHelper.COLUMN_SSID, ap.getSSID());
		values.put(MTrackerSQLiteHelper.COLUMN_ATTRACTIVENESS, ap.getAttractiveness());
		values.put(MTrackerSQLiteHelper.COLUMN_LASTGATEWAYIP, ap.getLastGatewayIp());
		values.put(MTrackerSQLiteHelper.COLUMN_TIMEDOWNLOAD, ap.getNetworkUtilization());
		values.put(MTrackerSQLiteHelper.COLUMN_DEVICESONNETWORK, ap.getDevicesOnNetwork());
		values.put(MTrackerSQLiteHelper.COLUMN_REJECTIONS, ap.getRejections());
		values.put(MTrackerSQLiteHelper.COLUMN_VISIT_DURATION, connectionUptime);
		values.put(MTrackerSQLiteHelper.COLUMN_VISIT_GAP, gapConnection);
		values.put(MTrackerSQLiteHelper.COLUMN_GANMA, gamma);
		values.put(MTrackerSQLiteHelper.COLUMN_VISITS, countVisits(ap));
		values.put(MTrackerSQLiteHelper.COLUMN_RANK, rank);
		values.put(MTrackerSQLiteHelper.COLUMN_QUALITY, ap.getQuality());
		values.put(MTrackerSQLiteHelper.COLUMN_CONNECTION, ap.getConnection());
		values.put(MTrackerSQLiteHelper.COLUMN_RECOMMENCDATION, ap.getRecommendation());
		values.put(MTrackerSQLiteHelper.COLUMN_NUM_RECOMMENDATIONS, ap.getNumRecommendations());
		values.put(MTrackerSQLiteHelper.COLUMN_FUNCTION, function);
		values.put(MTrackerSQLiteHelper.COLUMN_GAMMA_GAP,gammaGap);
		values.put(MTrackerSQLiteHelper.COLUMN_GAMMA_RANk, gammaRank);
		values.put(MTrackerSQLiteHelper.COLUMN_TIME, time);
		values.put(MTrackerSQLiteHelper.COLUMN_DATE,date);
		values.put(MTrackerSQLiteHelper.COLUMN_BATTERY,battery);

		return db.insert(MTrackerSQLiteHelper.TABLE_RANKING, null, values);
	}

	/**
	 * Gets the number of records in the ACCESS_POINTS table. This is, the number of AP registered on the application.
	 * 
	 * @return the number of AP registered by the application.
	 */
	public long getNumAP(){
		return DatabaseUtils.queryNumEntries(db, MTrackerSQLiteHelper.TABLE_ACCESSPOINTS);
	}
	
	/**
	 * Register a new AP in the application. It creates a new record on the ACCESS_POINTS table, with the information passed as MTrackerAP.
	 * 
	 * @param ap Access point information.
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
	public long registerNewAP (MTrackerAP ap) {
		Log.d(TAG, "new acces porint: " + ap.getDevicesOnNetwork() + ap.getRejections() + ap.getSSID());
		ContentValues values = new ContentValues();

	    values.put(MTrackerSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
	    values.put(MTrackerSQLiteHelper.COLUMN_SSID, ap.getSSID());
	    values.put(MTrackerSQLiteHelper.COLUMN_ATTRACTIVENESS, ap.getAttractiveness());
	    values.put(MTrackerSQLiteHelper.COLUMN_LASTGATEWAYIP, ap.getLastGatewayIp());
		values.put(MTrackerSQLiteHelper.COLUMN_DEVICESONNETWORK, ap.getDevicesOnNetwork());
		values.put(MTrackerSQLiteHelper.COLUMN_REJECTIONS, ap.getRejections());
		values.put(MTrackerSQLiteHelper.COLUMN_REJECTED, ap.getRejected());
	    return db.insert(MTrackerSQLiteHelper.TABLE_ACCESSPOINTS, null, values);
	}
	
	/**
	 * Update an AP already registered by the application. This modifies the corresponding record to the AP in the ACCESS_POINTS table.
	 * 
	 * @param ap Access point information.
	 * @return true, if successful.
	 */
	public boolean updateAP(MTrackerAP ap) {

		String identifier = MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'";
		ContentValues values = new ContentValues();
		values.put(MTrackerSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
	    values.put(MTrackerSQLiteHelper.COLUMN_SSID, ap.getSSID());
	    values.put(MTrackerSQLiteHelper.COLUMN_ATTRACTIVENESS, ap.getAttractiveness());
	    values.put(MTrackerSQLiteHelper.COLUMN_LASTGATEWAYIP, ap.getLastGatewayIp());
		values.put(MTrackerSQLiteHelper.COLUMN_REJECTED, ap.getRejected());
	    int rows = db.update(MTrackerSQLiteHelper.TABLE_ACCESSPOINTS, values, identifier, null);
		
	    return ((rows != 0)? true : false);
	}
	public boolean updateAttractivenessAP(MTrackerAP ap) {
		Log.d(TAG, "Attractiveness: " + ap.getAttractiveness());
		String identifier = MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'";
		ContentValues values = new ContentValues();
		values.put(MTrackerSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
		values.put(MTrackerSQLiteHelper.COLUMN_SSID, ap.getSSID());
		values.put(MTrackerSQLiteHelper.COLUMN_ATTRACTIVENESS, ap.getAttractiveness());

		int rows = db.update(MTrackerSQLiteHelper.TABLE_ACCESSPOINTS, values, identifier, null);

		return ((rows != 0)? true : false);
	}
	public boolean updateAPRejected(MTrackerAP ap) {

		MTrackerAP ap1 = getAP(ap.getBSSID());
		int rejections= getAP(ap.getBSSID()).getRejections();
		Log.d(TAG, "rejections" + ap1.getRejections() + " " + ap1.getSSID());

		rejections++;

		String identifier = MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'";
		ContentValues values = new ContentValues();
		values.put(MTrackerSQLiteHelper.COLUMN_BSSID, ap.getBSSID());
		values.put(MTrackerSQLiteHelper.COLUMN_SSID, ap.getSSID());
		values.put(MTrackerSQLiteHelper.COLUMN_ATTRACTIVENESS, ap.getAttractiveness());
		values.put(MTrackerSQLiteHelper.COLUMN_LASTGATEWAYIP, ap.getLastGatewayIp());
		values.put(MTrackerSQLiteHelper.COLUMN_REJECTIONS,rejections);
		values.put(MTrackerSQLiteHelper.COLUMN_REJECTED,ap.getRejected());

		int rows = db.update(MTrackerSQLiteHelper.TABLE_ACCESSPOINTS, values, identifier, null);

		return ((rows != 0)? true : false);
	}


	public boolean updateParameters(MTrackerAP ap) {
		String identifier = MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'";
		ContentValues values = new ContentValues();
		values.put(MTrackerSQLiteHelper.COLUMN_TIMEDOWNLOAD,ap.getNetworkUtilization());
		values.put(MTrackerSQLiteHelper.COLUMN_DEVICESONNETWORK,ap.getDevicesOnNetwork());

		int rows = db.update(MTrackerSQLiteHelper.TABLE_ACCESSPOINTS, values, identifier, null);

		return ((rows != 0)? true : false);
	}

	/**
	 * Gets an AP already registered by the application. 
	 * 
	 * @param bssid The ssid of the AP which information should be returned
	 * @return the MTrackerAP object, null if not found.
	 */
	public MTrackerAP getAP(String bssid) {
		MTrackerAP ap;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_ACCESSPOINTS, allColumnsAccessPoint, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null, null, null, null);
		if (cursor.moveToFirst())
			ap = cursorToAP(cursor);
		else
			ap = null;	
		
		cursor.close();
		return ap;
	}
	
	/**
	 * Checks if a given AP has already been registered by the application.
	 * 
	 * @param bssid The ssid of the AP
	 * @return true, if AP has already been registered by the application, false otherwise.
	 */
	public boolean     hasAP (String bssid) {
        return (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + MTrackerSQLiteHelper.TABLE_ACCESSPOINTS + " WHERE " + MTrackerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null) == 0)? false : true;
	}
	
	/**
	 * Gets the all the AP recorded by the application on the ACCESS_POINTS table.
	 * 
	 * @return A map with the AP objects, and the bssid as key.
	 */
	public Map<String, MTrackerAP> getAllAP() {
		Map<String, MTrackerAP> apMap = new TreeMap<String, MTrackerAP>();

		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_ACCESSPOINTS, allColumnsAccessPoint, null, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			MTrackerAP ap = cursorToAP(cursor);
			ap.setRank(getRank(ap));
			apMap.put(ap.getBSSID(), ap);
			cursor.moveToNext();
		}

	    cursor.close();
	    return apMap;
	}
	
	/**
	 * Gets the all the AP recorded by the application on the ACCESS_POINTS table, and the return only the ones that are also available in the List of ScanResult.
	 * 
	 * @return A map with the AP objects, and the bssid as key.
	 */
	public Map<String, MTrackerAP> getAllAP(List<ScanResult> availableAP) {
		Map<String, MTrackerAP> apMap = new TreeMap<String, MTrackerAP>();
		Set<String> scanUniques = new LinkedHashSet<String>();

		for (ScanResult result : availableAP) {

        	scanUniques.add(result.SSID);
    	}
	
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_ACCESSPOINTS,
			allColumnsAccessPoint, null, null, null, null, null);
	
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			MTrackerAP ap = cursorToAP(cursor);
			if (scanUniques.contains(ap.getBSSID())) {
				apMap.put(ap.getBSSID(), ap);
			}
			cursor.moveToNext();
		}

	    cursor.close();
	    return apMap;
	}
	/**
	 * Gets the all ranking recorded by the application on the RANKING table, and the return only the ones that are also available in the List of ScanResult.
	 *
	 * @return A map with the AP objects, and the bssid as key.
	 */
	public  void getAllRANK() {

		for(int function=0; function<5; function++) {
			File root = Environment.getExternalStorageDirectory();
			File file = new File(root, "MTracker" + "_" + function + ".txt");

			FileOutputStream fOut;
			try {
				fOut = new FileOutputStream(file);

				OutputStreamWriter osw;
				osw = new OutputStreamWriter(fOut);

				Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_RANKING,
						allColumnsRanking, MTrackerSQLiteHelper.COLUMN_FUNCTION + "='" + function + "'", null, null, null, null);

				cursor.moveToFirst();

				while (!cursor.isAfterLast()) {

					osw.write(cursor.getString(20) + ";" +
							cursor.getString(19) + ";" +
							cursor.getFloat(21) + ";" +
							cursor.getString(1) + ";" +
							cursor.getString(0) + ";" +
							cursor.getInt(16) + ";" +

							cursor.getFloat(2) + ";" +
							cursor.getInt(6) + ";" +
							cursor.getInt(7) + ";" +
							cursor.getInt(8) + ";" +
							cursor.getFloat(17) + ";" +
							cursor.getFloat(9) + ";" +
							cursor.getInt(10) + ";" +
							cursor.getInt(15) + ";" +
							cursor.getFloat(14) + ";" +


							cursor.getFloat(4) + ";" +
							cursor.getInt(5) + ";" +
							cursor.getInt(13) + ";" +
							cursor.getInt(12) + ";" +
							cursor.getDouble(11) + ";" +
							cursor.getDouble(18) + "\n");
					/*

					switch (function) {
						case 0:
							osw.write(cursor.getString(20) + ";" +
									cursor.getString(19) + ";" +
									cursor.getFloat(21) + ";" +
									cursor.getString(1) + ";" +
									cursor.getFloat(2) + ";" +
									cursor.getInt(6) + ";" +
									cursor.getInt(7) + ";" +
									cursor.getInt(8) + ";" +
									cursor.getFloat(17) + ";" +
									cursor.getFloat(9) + ";" +
									cursor.getInt(10) + ";" +

									cursor.getFloat(4) + ";" +
									cursor.getInt(5) + ";" +
									cursor.getInt(13) + ";" +
									cursor.getInt(12) + ";" +

									cursor.getDouble(11) + ";" +
									cursor.getDouble(18) + "\n");
							break;
						case 1:
							osw.write(cursor.getString(20) + ";" +
									cursor.getString(19) + ";" +
									cursor.getFloat(21) + ";" +
									cursor.getString(1) + ";" +

									cursor.getFloat(2) + ";" +
									cursor.getInt(6) + ";" +
									cursor.getInt(7) + ";" +
									cursor.getInt(8) + ";" +
									cursor.getFloat(17) + ";" +
									cursor.getFloat(9) + ";" +
									cursor.getInt(10) + ";" +
									cursor.getInt(15) + ";" +
									cursor.getFloat(14) + ";" +
									cursor.getString(16) + ";" +

									cursor.getFloat(4) + ";" +
									cursor.getInt(5) + ";" +
									cursor.getInt(13) + ";" +
									cursor.getInt(12) + ";" +
									cursor.getDouble(11) + ";" +
									cursor.getDouble(18) + "\n");
							break;
						case 2:
							osw.write(cursor.getString(20) + ";" +
									cursor.getString(19) + ";" +
									cursor.getFloat(21) + ";" +
									cursor.getString(1) + ";" +
									cursor.getFloat(4) + ";" +
									cursor.getInt(15) + ";" +
									cursor.getInt(13) + ";" +
									cursor.getInt(12) + ";" +
									cursor.getInt(15) + ";" +
									cursor.getDouble(11) + ";" +
									cursor.getDouble(18) + "\n");
							break;

						case 3:
							osw.write(cursor.getString(20) + ";" +
									cursor.getString(19) + ";" +
									cursor.getFloat(21) + ";" +
									cursor.getString(1) + ";" +
									cursor.getFloat(4) + ";" +
									cursor.getInt(15) + ";" +
									cursor.getInt(13) + ";" +
									cursor.getInt(12) + ";" +
									cursor.getFloat(14) + ";" +
									cursor.getDouble(11) + ";" +
									cursor.getDouble(18) + "\n");
							break;
						case 4:
							osw.write(cursor.getString(20) + ";" +
									cursor.getString(19) + ";" +
									cursor.getFloat(21) + ";" +
									cursor.getString(1) + ";" +
									cursor.getFloat(2) + ";" +
									cursor.getInt(6) + ";" +
									cursor.getInt(7) + ";" +
									cursor.getInt(8) + ";" +
									cursor.getFloat(17) + ";" +
									cursor.getFloat(9) + ";" +
									cursor.getInt(10) + ";" +
									cursor.getFloat(14) + ";" +
									cursor.getDouble(11) + ";" +
									cursor.getDouble(18) + "\n");
							break;

					}*/

					Log.d(TAG, "File with function: " + function + " was created");
					cursor.moveToNext();
				}

				cursor.close();
				osw.close();
				fOut.close();
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}


		}

	}

	/**
	 * Checks all the AP registered by the application and return the one with the highest ProbingFunctionsManager.
	 * 
	 * @return the best AP registered by the application.
	 */
	public MTrackerAP getBestAP() {
		Map<String, MTrackerAP> aps = getAllAP();
		if (!aps.isEmpty()) {
			MTrackerAP bestAp = new MTrackerAP();
			double bestRank = -1.0;
			double rank;
			for (MTrackerAP ap : aps.values()) {
				rank = getRank(ap);
				if (rank > bestRank) {
					bestRank = rank;
					bestAp = ap;
				}
			}
			return bestAp;
		} else {
			return null;
		}
	}
	
	/**
	 * Checks the APs registered by the application and available in the List of ScanResult, and the return the one with the highest probingFunctionsManager.
	 * 
	 * @return the best AP registered by the application.
	 */
	public MTrackerAP getBestAP(List<ScanResult> availableAP) {
		Map<String, MTrackerAP> aps = getAllAP(availableAP);
		if (!aps.isEmpty()) {
			MTrackerAP bestAp = new MTrackerAP();
			double bestRank = -1.0;
			double rank;
			for (MTrackerAP ap : aps.values()) {

					rank = getRank(ap);
					if (rank > bestRank) {
						bestRank = rank;
						bestAp = ap;
					}

			}
			return bestAp;
		} else {
			return null;
		}
	}
	
	/**
	 * Write all the AP registered by the application into a text file (MTracker.txt), located in the root of the directory.
	 */
	public void writeAPListToFile (){
		 
		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, "MTracker.txt");
			 
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);
		
			OutputStreamWriter osw = null;
			osw = new OutputStreamWriter(fOut);
			Map<String,MTrackerAP> apEntries = getAllAP();
			for (MTrackerAP ap : apEntries.values()) {
				osw.write(ap.toString());
				osw.write(".....\n");
			}
			osw.close();
			fOut.close();
		}
		catch(Exception e){
			 e.printStackTrace(System.err);
		}
	}
	/**
	 * Write all the AP registered by the application into a text file (MTracker.txt), located in the root of the directory.
	 */
	public void writeRankingListToFile (){

		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, "MTracker.txt");

		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);

			OutputStreamWriter osw = null;
			osw = new OutputStreamWriter(fOut);
			Map<String,MTrackerAP> apEntries = getAllAP();
			for (MTrackerAP ap : apEntries.values()) {
				osw.write(ap.toString());
				osw.write(".....\n");
			}
			osw.close();
			fOut.close();
		}
		catch(Exception e){
			e.printStackTrace(System.err);
		}
	}
	
	/*
	 * VISITS TABLE: This table stores the visits
	 * to each access point. The information stored
	 * is:
	 * 		- SSID of the network
	 * 		- BSSID of the network
	 * 		- Time at which the connection started.
	 * 		- Time at which the connection ended.
	 * 		- Day of the Week (Start).
	 * 		- Hour of the Day (Start).
	 * 
	 */
	
	
	private String[] allColumnsVisit = {
			MTrackerSQLiteHelper.COLUMN_SSID,
			MTrackerSQLiteHelper.COLUMN_BSSID,
			MTrackerSQLiteHelper.COLUMN_TIMEON,
			MTrackerSQLiteHelper.COLUMN_TIMEOUT,
			MTrackerSQLiteHelper.COLUMN_DAYOFTHEWEEK,
			MTrackerSQLiteHelper.COLUMN_HOUR
	};
	
	private MTrackerVisit cursorToVisit(Cursor cursor) {
		MTrackerVisit visit = new MTrackerVisit();
		visit.setSSID(cursor.getString(0));
		visit.setBSSID(cursor.getString(1));
		visit.setStartTime(cursor.getLong(2));
		visit.setEndTime(cursor.getLong(3));
		visit.setDayOfTheWeek(cursor.getInt(2));
		visit.setHourOfTheDay(cursor.getInt(3));
		return visit;
	}

    /**
     * Computes the Stationary Time for a given AP.
     *
     * @param ap The MTrackerAP whose Stationary Time is to be computed.
     * @return The stationary time for the given AP.
     */
	public long  getStationaryTime(MTrackerAP ap) {
		String bssid = ap.getBSSID();
		long sationaryTime = 0;
		long count = 0;
		long startTime = 0;
		long endTime = 0;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_VISITS, allColumnsVisit, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null, null, null, null);

		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				startTime = cursor.getLong(2);
				endTime = cursor.getLong(3);

				if ((endTime - startTime) > 0) {
					sationaryTime = sationaryTime + (endTime - startTime);
					count++;
				}
				cursor.moveToNext();
			}
			cursor.close();
			
			if (count > 0)
				sationaryTime = sationaryTime/count;
	
		}
		else {
			cursor.close();
		}
		
		return sationaryTime/1000;
	}

	public long  getLastDesconnection (MTrackerAP ap) {
		String bssid = ap.getBSSID();
		long sationaryTime = 0;
		long count = 0;
		long startTime = 0;
		long endTime = 0;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_VISITS, allColumnsVisit, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null, null, null, null);

		cursor.moveToLast();
			if(cursor.getCount()>0) {

				startTime = cursor.getLong(2);
				endTime = cursor.getLong(3);
				Log.d(TAG, startTime + " " + endTime);
				if ((endTime - startTime) > 0) {
					sationaryTime = endTime - startTime;

				}
				cursor.moveToNext();
			}
			cursor.close();


		Log.d(TAG, sationaryTime+"");

		return endTime;
	}

    /**
     * Computes the Stationary Time for a given AP, only taking into consideration records for a given Day of the Week.
     *
     * @param ap The MTrackerAP whose Stationary Time is to be computed.
     * @param dayOfTheWeek Day of the week that will restrict the computation of the stationary time.
     * @return The stationary time for the given AP.
     */
	public long getStationaryTimeByMoment (MTrackerAP ap, int dayOfTheWeek) {		
		String bssid = ap.getBSSID();
		long sationaryTime = 0;
		long count = 0;
		long startTime = 0;
		long endTime = 0;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_VISITS, allColumnsVisit, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "' AND " + MTrackerSQLiteHelper.COLUMN_DAYOFTHEWEEK + "=" + dayOfTheWeek, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				startTime = cursor.getLong(2);
				endTime = cursor.getLong(3);
				if ((endTime - startTime) > 0) {
					sationaryTime = sationaryTime + (endTime - startTime);
					count++;
				}
				cursor.moveToNext();
			}
			cursor.close();
			
			if (count > 0)
				sationaryTime = sationaryTime/count;
	
		}
		else {
			cursor.close();
		}
		
		return sationaryTime/1000;
	}

    /**
     * Computes the Number of visits that the node has done to a given AP.
     *
     * @param ap The MTrackerAP whose Stationary Time is to be computed.
     * @return The number of visits.
     */
	public long countVisits(MTrackerAP ap) {
		String bssid = ap.getBSSID();
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + MTrackerSQLiteHelper.TABLE_VISITS + " WHERE " + MTrackerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null);
	}
	public long rejectConnections(MTrackerAP ap) {
		String bssid = ap.getBSSID();
		return DatabaseUtils.longForQuery(db, "SELECT "+ MTrackerSQLiteHelper.COLUMN_REJECTIONS +" FROM " + MTrackerSQLiteHelper.TABLE_ACCESSPOINTS + " WHERE " + MTrackerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null);
	}

	public long devicesOnNetwork(MTrackerAP ap) {
		String bssid = ap.getBSSID();

		return DatabaseUtils.longForQuery(db, "SELECT "+ MTrackerSQLiteHelper.COLUMN_DEVICESONNETWORK  +" FROM " + MTrackerSQLiteHelper.TABLE_ACCESSPOINTS + " WHERE " + MTrackerSQLiteHelper.COLUMN_BSSID + "='" + bssid + "'", null);
	}

    /**
     * Computes the ProbingFunctionsManager of this node towards a given AP. The ProbingFunctionsManager is computed as
     *
     * @param ap The MTrackerAP whose Stationary Time is to be computed.
     * @return The number of visits.
     */
	public double getRank (MTrackerAP ap)
	{
		double result;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_RANKING,
				allColumnsRanking, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'"  , null, null, null, null);

		cursor.moveToLast();
		if(cursor.getCount()!=0)
			result=cursor.getDouble(18);
		else
			result=0;
		Log.d(TAG, "Getting probingFunctionsManager  " + result);
		return result;
		//return ap.getAttractiveness() * getStationaryTime(ap) * countVisits(ap) * devicesOnNetwork(ap);
	}

	public double getRank (MTrackerAP ap, int function)
	{
		double result;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_RANKING,
				allColumnsRanking, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'" + " AND " + MTrackerSQLiteHelper.COLUMN_FUNCTION + "='" + function + "'", null, null, null, null);

		cursor.moveToLast();
		if(cursor.getCount()!=0)
			result=cursor.getDouble(11);
		else
			result=0;
		Log.d(TAG, "Getting probingFunctionsManager " + result);
		return result;

	}
	public double getRankEMA (MTrackerAP ap, int function)
	{
		double result;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_RANKING,
				allColumnsRanking, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'" + " AND " + MTrackerSQLiteHelper.COLUMN_FUNCTION + "='" + function + "'", null, null, null, null);

		cursor.moveToLast();
		if(cursor.getCount()!=0)
			result=cursor.getDouble(18);
		else
			result=0;
		Log.d(TAG, "Getting probingFunctionsManager " + result);
		return result;

	}
    /**
     * Test Method to compute the ProbingFunctionsManager of this node towards a given AP, taking into consideration the current
     * visit time.
     *
     * @param ap The MTrackerAP whose Stationary Time is to be computed.
     * @param currentDuration current connection time.
     * @return The number of visits.
     */
	public double getInstantaneousRank(MTrackerAP ap, Long currentDuration) {
		 if (ap == null) {
			 return currentDuration;
		 }
		 else {
			 return (0.3*getStationaryTime(ap) + 0.7*currentDuration) * (countVisits(ap) + 1) * ap.getAttractiveness();
		 }
	}

    /**
     * Register a new visit into the database.
     *
     * @param SSID SSID
     * @param BSSID BSSID
     * @param startTime Time at which the connection started.
     * @param endTime Time at which the connection ended.
     * @return id of the created record, -1 if an error occurs.
     */
	public long registerNewVisit (String SSID, String BSSID, Long startTime, Long endTime) {
		Log.d("Data Base", "Register a new visit" + startTime+"  " +endTime);
		cal.setTimeInMillis(startTime);
		ContentValues values = new ContentValues();
	    values.put(MTrackerSQLiteHelper.COLUMN_SSID, SSID);
	    values.put(MTrackerSQLiteHelper.COLUMN_BSSID, BSSID);
	    values.put(MTrackerSQLiteHelper.COLUMN_TIMEON, startTime);
	    values.put(MTrackerSQLiteHelper.COLUMN_TIMEOUT, endTime);
	    
	    try {
	    	int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
	    	values.put(MTrackerSQLiteHelper.COLUMN_DAYOFTHEWEEK, dayOfTheWeek);
	    } catch (Exception e) {
	    	//STORE DEFAULT AND WRITE TO LOG
	    }
	    
	    try {
	    	int hourOfTheDay = cal.get(Calendar.HOUR_OF_DAY);
	    	values.put(MTrackerSQLiteHelper.COLUMN_HOUR, hourOfTheDay);
	    } catch (Exception e) {
	    	//STORE DEFAULT AND WRITE TO LOG
	    }
	    
	    return db.insert(MTrackerSQLiteHelper.TABLE_VISITS, null, values);
	}

    /**
     * Updates an existing visit in the database.
     *
     * @param _id id of the record to update
     * @param SSID SSID
     * @param BSSID BSSID
     * @param startTime Time at which the connection started.
     * @param endTime Time at which the connection ended.
     * @return id of the created record, -1 if an error occurs.
     */
	public boolean updateVisit (long _id, String SSID, String BSSID, Long startTime, Long endTime) {
		String identifier = MTrackerSQLiteHelper.COLUMN_ID + "=" + _id;
		
		ContentValues values = new ContentValues();
		
		if (SSID != null)
			values.put(MTrackerSQLiteHelper.COLUMN_SSID, SSID);
		
		if (SSID != null)
			values.put(MTrackerSQLiteHelper.COLUMN_BSSID, BSSID);
	    
		if (startTime != null) {
			values.put(MTrackerSQLiteHelper.COLUMN_TIMEON, startTime);
			
			cal.setTimeInMillis(startTime);
		    
			try {
		    	int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
		    	values.put(MTrackerSQLiteHelper.COLUMN_DAYOFTHEWEEK, dayOfTheWeek);
		    } catch (Exception e) {
		    	//STORE DEFAULT AND WRITE TO LOG
		    }
		    
		    try {
		    	int hourOfTheDay = cal.get(Calendar.HOUR_OF_DAY);
		    	values.put(MTrackerSQLiteHelper.COLUMN_HOUR, hourOfTheDay);
		    } catch (Exception e) {
		    	//STORE DEFAULT AND WRITE TO LOG
		    }
		}
		
		if (endTime != null)
			values.put(MTrackerSQLiteHelper.COLUMN_TIMEOUT, endTime);
	    	    
		int rows;
		
		if (values.size() > 0)
	    	rows = db.update(MTrackerSQLiteHelper.TABLE_VISITS, values, identifier, null);
		else
			rows = 0;
		
	    return ((rows != 0)? true : false);
	}

    /**
     * Get a List with all the visit objects stored in the database.
     *
     */
	public List<MTrackerVisit> getAllVisits() {
		List<MTrackerVisit> visitList = new LinkedList<MTrackerVisit>();
	
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_VISITS,
			allColumnsVisit, null, null, null, null, null);
	
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			MTrackerVisit visit = cursorToVisit(cursor);
			visitList.add(visit);
			cursor.moveToNext();
		}

	    cursor.close();
	    return visitList;
	}
	
	public List<String> getAllVisitsString(MTrackerAP ap) {
		List<String> visitList = new LinkedList<String>();
	
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_VISITS, allColumnsVisit, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'", null, null, null, null);
	
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			MTrackerVisit visit = cursorToVisit(cursor);
			visitList.add(visit.toString());
			cursor.moveToNext();
		}

	    cursor.close();
	    return visitList;
	}
	public long getLastVisitDuration(MTrackerAP ap) {

		long time;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_VISITS, allColumnsVisit, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'", null, null, null, null);

		cursor.moveToLast();
		if(cursor.getCount() >0) {
			//Log.d("Data base", cursor.getLong(2) + "++++" + cursor.getLong(3));
			time = cursor.getInt(3) - cursor.getInt(2);
		}else{
			time=0;
		}
		cursor.close();
		return time;
	}

	public long getLastMesurement(MTrackerAP ap){
		long time;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_RANKING, allColumnsRanking, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'", null, null, null, null);

		cursor.moveToLast();
		if(cursor.getCount() >0) {
			//Log.d("Data base", cursor.getLong(2) + "++++" + cursor.getLong(3));
			time = cursor.getLong(7);
		}else{
			time=0;
		}
		cursor.close();
		return time;

	}
	public float getLastGAMMA(MTrackerAP ap){
		float time;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_RANKING, allColumnsRanking, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'", null, null, null, null);

		cursor.moveToLast();
		if(cursor.getCount() >0) {
			//Log.d("Data base", cursor.getLong(2) + "++++" + cursor.getLong(3));
			time = cursor.getFloat(9);
		}else{
			time=0;
		}
		cursor.close();
		return time;

	}
	public float getLastGAMMA(MTrackerAP ap, int function){
		float time;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_RANKING, allColumnsRanking, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'" + " AND " + MTrackerSQLiteHelper.COLUMN_FUNCTION + "='" + function + "'", null, null, null, null);

		cursor.moveToLast();
		if(cursor.getCount() >0) {
			//Log.d("Data base", cursor.getLong(2) + "++++" + cursor.getLong(3));
			time = cursor.getFloat(9);
		}else{
			time=0;
		}
		cursor.close();
		return time;

	}

	public float getLastGAMMAGAP(MTrackerAP ap, int function){
		float time;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_RANKING, allColumnsRanking, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'" + " AND " + MTrackerSQLiteHelper.COLUMN_FUNCTION + "='" + function + "'", null, null, null, null);

		cursor.moveToLast();
		if(cursor.getCount() >0) {
			//Log.d("Data base", cursor.getLong(2) + "++++" + cursor.getLong(3));
			time = cursor.getFloat(17);
		}else{
			time=0;
		}
		cursor.close();
		return time;

	}

	public float getLastGammaRank(MTrackerAP ap, int function){
		float time;
		Cursor cursor = db.query(MTrackerSQLiteHelper.TABLE_RANKING, allColumnsRanking, MTrackerSQLiteHelper.COLUMN_BSSID + "='" + ap.getBSSID() + "'" + " AND " + MTrackerSQLiteHelper.COLUMN_FUNCTION + "='" + function + "'", null, null, null, null);

		cursor.moveToLast();
		if(cursor.getCount() >0) {
			//Log.d("Data base", cursor.getLong(2) + "++++" + cursor.getLong(3));
			time = cursor.getFloat(18);
		}else{
			time=0;
		}
		cursor.close();
		return time;

	}
    /**
     * Get the number of visits registered in the database.
     *
     */
	public long getNumVisits(){
		return DatabaseUtils.queryNumEntries(db, MTrackerSQLiteHelper.TABLE_VISITS);
	}

    /**
     * Writes the Visit List to the file MTrackerVisits.txt.
     *
     */
	public void writeVisitListToFile (){
		 
		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, "MTrackerVisits.txt");
			 
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);
		
			OutputStreamWriter osw = null;
			osw = new OutputStreamWriter(fOut);
			List<MTrackerVisit> visitEntries = getAllVisits();
			for (MTrackerVisit visitEntry : visitEntries) {
				osw.write(visitEntry.toStringTabFormat());

			}
			osw.close();
			fOut.close();
		}
		catch(Exception e){
			 e.printStackTrace(System.err);
		}
	}
}