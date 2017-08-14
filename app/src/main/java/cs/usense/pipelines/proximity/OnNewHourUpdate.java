/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/04/06.
 * Class is part of the NSense application. It provides support for proximity pipeline.
 */

package cs.usense.pipelines.proximity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import cs.usense.db.DataBaseChangeListener;
import cs.usense.db.NSenseDataSource;
import cs.usense.preferences.GeneralPreferences;

/**
 * This class provides the computing updates for Social weights among users on hourly basis
 * @author Waldir Moreira (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class OnNewHourUpdate extends BroadcastReceiver{

	/** This class is to access functionality of NSense Data base */
	private NSenseDataSource datasource;

	/** This variable is to provide the day */
	public static int day = 1;

	/** Used to notify updates */
	BluetoothCore callback;

	/** Flag used for debugging purposes */
	private boolean debug = true;

	/** This variable is to get the new hour */
	public static final String NEW_HOUR = "android.intent.action.NEWHOUR";

	/**
	 * This method is the constructor for OnNewHourUpdate.
	 * @param datasource2 NSenseDataSource to access various methods and information of the NSense Data base.
	 **/
	public OnNewHourUpdate(NSenseDataSource datasource2, BluetoothCore callback) {
		datasource = datasource2;
		this.callback = callback;
	}

	private ArrayList<DataBaseChangeListener> listeners = new ArrayList<DataBaseChangeListener> ();

	/**
	 * This method receives action concerning social weight updates.
	 * @param context Interface to global information about an application environment. 
	 * @param intent The intent.
	 **/
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		GeneralPreferences.increaseHoursRunning(context);

		if (OnNewHourUpdate.NEW_HOUR.equals(action)) {
			if(debug){
				writeToSD("!!!!! Social weight update on hour");
			}
		}

		long currentTime = System.nanoTime(); 

		if(debug){
			writeToSD("!!!!! currentTime "+currentTime);
		}

		int currentTimeSlot = getTimeSlot();
		int previousTimeSlot = currentTimeSlot-1;   //  **************** Uncomment for hour-based SW updates
		//int previousTimeSlot = currentTimeSlot;       // 4-minute-based SW updates, Comment this for hour-based SW updates

		//To allow the computation for last daily sample
		if(previousTimeSlot==-1){
			previousTimeSlot = 23;
		}

		boolean appRestart = BluetoothCore.appRestarted;

		//This means the beginning of a new day		// 4-minute-based SW updates, Comment this for hour-based SW updates		
		if(previousTimeSlot==0){
			day++;
			// To do : go to the database and clear the total connect time on the new day
			resetDuration();

		}

		BluetoothCore.SWupdated = true;

		computeSocialWeight(day, day, previousTimeSlot, currentTime, appRestart, false);

		//Update preference file
		Calendar c = Calendar.getInstance();
		String d = "day";
		String dayNumber = "dayNumber";
		String timeSlot = "timeSlot";
		int dNumber = c.get(Calendar.DAY_OF_YEAR);
		if(debug){
			writeToSD("Updating pref files with Day " + day + " with dayNumber " + dNumber + " at new timeSlot " + currentTimeSlot);
		}
		SharedPreferences.Editor daySample = context.getSharedPreferences("DayTime", Context.MODE_PRIVATE).edit();	
		daySample.clear();
		daySample.putInt(d, day);
		daySample.putInt(dayNumber, dNumber);
		daySample.putInt(timeSlot, currentTimeSlot);
		daySample.commit();

		if(debug){
			SharedPreferences prefs = context.getSharedPreferences("DayTime", Context.MODE_PRIVATE);
			writeToSD("Updated prefFiles: Day " + prefs.getInt(d, 0) + " with number " + prefs.getInt(dayNumber, 0) + " at new timeSlot " + prefs.getInt(timeSlot, 0));
		}
	}

	public void resetDuration() {

		writeToSD("Reseting duration !!");

		Map<String,BTUserDevice> tempListOfDevice = datasource.getAllBTDevice(); 

		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();

		while (devIterator.hasNext()){

			String btDev = devIterator.next();

			BTUserDevice btDevice = datasource.getBTDevice(btDev);

			BTUserDevEncounterDuration duration = datasource.getBTDeviceEncounterDuration(btDev);

			int slot = 0;

			while(slot < 24){

				duration.setEncounterDuration(slot, 0.0);
				slot++;

			}
			datasource.updateBTDeviceAndDuration(btDevice, duration);

		}

	}

	/**
	 * This method computes the social weight towards all encountered nodes 
	 *  @param savDay The day the app started running.
	 *  @param currDay The current day when the app restarted.
	 *  @param currTimeSlot The current time slot when the app restarted.
	 *  @param currTime The current time when the app restarted.
	 *  @param appR The flag that tells whether the app restarted.
	 *  @param updOverDiffDays The flag that indicates the updates occur over different days.
	 */
	public void computeSocialWeight(int savedDay, int currentDay, int currentTimeSlot, long currentTime, boolean appRestart, boolean updateOverDiffDays){
		//		int savedDay = savDay;
		//		int currentDay = currDay;
		//		int currentTimeSlot = currTimeSlot;
		//		long currentTime = currTime;
		//		boolean appRestart = appR;
		//		boolean updateOverDiffDays = updOverDiffDays;
		Map<String,BTUserDevice> tempListOfDevice = datasource.getAllBTDevice(); 
		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();

		while (devIterator.hasNext()){
			String btDev = devIterator.next();

			BTUserDevice btDevice = datasource.getBTDevice(btDev);
			BTUserDevEncounterDuration duration = datasource.getBTDeviceEncounterDuration(btDev);
			BTUserDevAverageEncounterDuration averageDuration = datasource.getBTDeviceAverageEncounterDuration(btDev) ;
			BTUserDevSocialWeight socialWeight = datasource.getBTDeviceSocialWeight(btDev);

			if(appRestart || BluetoothCore.SWupdated){
				if(debug)
					writeToSD("AppRestarted in OnNewHourUpdate - Update encounter time to current time: " + currentTime);
				btDevice.setEncounterTime(currentTime);

				if(currentDay!=savedDay || updateOverDiffDays){
					if(debug){
						writeToSD("Different day or Old value of encounterDuration found, so zero the encounter duration!!!");
					}
					duration.setEncounterDuration(currentTimeSlot, 0.0);
				}
				datasource.updateBTDeviceAndDuration(btDevice, duration);
			}

			if(debug){
				writeToSD("Device name: " + btDevice.getDevName() + " in the database");
				writeToSD("Device MAC: " + btDev);
				writeToSD("Encounter start: " +  btDevice.getEncounterStart());
				writeToSD("Encounter duration - timeSlot " +  (currentTimeSlot) + ": " + duration.getEncounterDuration(currentTimeSlot));
				writeToSD("Average encounter duration - timeSlot " +  (currentTimeSlot) + ": "+ averageDuration.getAverageEncounterDuration(currentTimeSlot));
				writeToSD("Social weight - timeSlot " +  (currentTimeSlot) + ": "+ socialWeight.getSocialWeight(currentTimeSlot));
				writeToSD("Current day: " + currentDay);
			}

			double timeInContact = (currentTime-btDevice.getEncounterStart())/1000000000.0; // ************ zero this

			///* A negative value indicates the application restarted.
			//* To avoid a negative effect, the new encounter duration remains with the previous value.
			//* May not be necessary as the appRestarted takes care of that.
			//*/
			//if(timeInContact<0.0){
			//	writeToSD("Negative value for encounter duration detected !! Resorting to previous value !! **** OnHourUpdate ****");
			//	timeInContact = 0.0;	
			//}

			double newEncounterDuration = duration.getEncounterDuration(currentTimeSlot) + timeInContact;

			/** Set new encounter start */
			btDevice.setEncounterTime(currentTime);
			duration.setEncounterDuration(currentTimeSlot, newEncounterDuration);
			datasource.updateBTDeviceAndDuration(btDevice, duration);

			if(debug){
				writeToSD("new encounter start " + btDevice.getEncounterStart() );
			}

			/** Update average encounter duration */
			double avgEncDuration_old = averageDuration.getAverageEncounterDuration(currentTimeSlot);	

			if(debug){
				writeToSD("avgEncDuration_old  " + avgEncDuration_old);
			}

			double avgEncDuration_new = (duration.getEncounterDuration(currentTimeSlot) + ((currentDay-1) * avgEncDuration_old))/currentDay;

			if(debug){
				writeToSD("avgEncDuration_new  " + avgEncDuration_new);
			}

			averageDuration.setAverageEncounterDuration(currentTimeSlot, avgEncDuration_new);
			datasource.updateBTDevAvgEncounterDuration(averageDuration);

			if(debug){
				writeToSD("New avg encouterDuration from DB  " + datasource.getBTDeviceAverageEncounterDuration(btDev).getAverageEncounterDuration(currentTimeSlot));
			}

			/** Update social weight */
			double k = 0;
			int index = currentTimeSlot;
			double dailySampleNumber = 24;
			double sw  = 0.0;

			Log.i("BT","Calculating weight!!!" + " currentDay " + currentDay + " currentTimeSlot " + currentTimeSlot);

			while(k<24){
				if(index == 24)
					index = 0;

				double levels = dailySampleNumber/(dailySampleNumber+k);
				double avgDurationPreviousSlot = averageDuration.getAverageEncounterDuration(index);

				if(debug){

					writeToSD("k: " + k);
					writeToSD("index: " + index);
					writeToSD("avgDurationPreviousSlot: " + avgDurationPreviousSlot);
					writeToSD("sw initial: " + sw);
					writeToSD("dailySampleNumber/(dailySampleNumber+k): " + levels);
					writeToSD("ds*ad: " + levels * avgDurationPreviousSlot);

				}
				sw = sw + (levels * avgDurationPreviousSlot);

				if(debug) writeToSD("sw new: " + sw);


				index++;
				k++;
			}
			socialWeight.setSocialWeight(currentTimeSlot, sw);
			datasource.updateBTDevSocialWeight(socialWeight);

			if(debug){
				writeToSD("Social weight: " + socialWeight.getSocialWeight(currentTimeSlot));
				writeToSD("Social weight from DB: " + datasource.getBTDeviceSocialWeight(btDev).getSocialWeight(currentTimeSlot));
				writeToSD("-------------------------------");

			}


		}
		notifyDataBaseChange ();
		showDevicesOnDB();
	}

	/**
	 * This method provides the current time slot.
	 * @return currentTimeSlot The actual time slot.
	 */
	public int getTimeSlot(){
		int currentTimeSlot = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		return currentTimeSlot;
	}

	/**
	 * Writes on a file for Bluetooth debugging
	 */
	private static void writeToSD(String text){
		File file = new File(Environment.getExternalStorageDirectory()+File.separator+"BluetoothCore","BluetoothCoreOutput.txt");
		String currentTime = (String) DateFormat.format("dd/MM - hh:mm:ss.sss", Calendar.getInstance().getTime());
		try {
			FileWriter writer = new FileWriter(file, true);
			String line = currentTime + " " + text + "\n";
			writer.write(line); 
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This method shows the Bluetooth devices stored on the DB. 
	 */
	public void showDevicesOnDB() {
		int numberDevOnDB = datasource.getAllBTDevice().entrySet().size();

		Map<String,BTUserDevice> tempListOfDevice = datasource.getAllBTDevice(); 
		Map<String,BTUserDevEncounterDuration> tempListOfDevEncounterDuration = datasource.getAllBTDevEncounterDuration(); 
		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();

		while (devIterator.hasNext()){
			String btDev = devIterator.next();

			int timeSlot = 0;
			while(timeSlot<24){	
				if((datasource.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot) !=0.0){
				}
				timeSlot++;
			}
		}
	}

	/**
	 * This method notifies a database change to the mListeners.
	 */
	private void notifyDataBaseChange () {
		for (DataBaseChangeListener listener : this.listeners) 
		{
			listener.onDataBaseChangeBT(new ArrayList<BTUserDevice>(datasource.getAllBTDevice().values()));
			listener.onDataBaseChangeBTEncDur(new ArrayList<BTUserDevEncounterDuration>(datasource.getAllBTDevEncounterDuration().values()));
			listener.onDataBaseChangeBTAvgEncDur(new ArrayList<BTUserDevAverageEncounterDuration>(datasource.getAllBTDevAverageEncounterDuration().values()));
			listener.onDataBaseChangeBTSocialWeight(new ArrayList<BTUserDevSocialWeight>(datasource.getAllBTDevSocialWeight().values()));
		}
	}


}
