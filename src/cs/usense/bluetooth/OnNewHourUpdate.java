/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the NSense application.
 * This class is responsible for computing the social weight among users.
 * @author Waldir Moreira (COPELABS/ULHT)
 */

package cs.usense.bluetooth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cs.usense.db.DataBaseChangeListener;
import cs.usense.db.NSenseDataSource;

/**
 * This class provides the computing updates for Social weights among users on hourly basis
 * */
public class OnNewHourUpdate extends BroadcastReceiver{
	
	/** This class is to access functionality of NSense Data base */
	private NSenseDataSource datasource;
	
	/** This variable is to provide the day */
	public static int day = 1;

	/** This variable is to get the new hour */
	public static final String NEW_HOUR =
			"android.intent.action.NEWHOUR";

	/**
	 * This method is the constructor for OnNewHourUpdate.
	 * @param datasource2 NSenseDataSource to access various methods and information of the NSense Data base.
	 **/
	public OnNewHourUpdate(NSenseDataSource datasource2) {
		datasource = datasource2;
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

		if (OnNewHourUpdate.NEW_HOUR.equals(action)) {
		}

		long currentTime = System.nanoTime(); 

		int currentTimeSlot = getTimeSlot();
		int previousTimeSlot = currentTimeSlot-1;

		if(previousTimeSlot==-1){
			previousTimeSlot = 23;
			day++;
		}



		Map<String,BTUserDevice> tempListOfDevice = datasource.getAllBTDevice(); 
		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();

		while (devIterator.hasNext()){
			String btDev = devIterator.next();

			BTUserDevice btDevice = datasource.getBTDevice(btDev);
			BTUserDevEncounterDuration duration = datasource.getBTDeviceEncounterDuration(btDev);
			BTUserDevAverageEncounterDuration averageDuration = datasource.getBTDeviceAverageEncounterDuration(btDev) ;
			BTUserDevSocialWeight socialWeight = datasource.getBTDeviceSocialWeight(btDev);

			/** Set new encounter start */
			btDevice.setEncounterTime(currentTime);
			datasource.updateBTDeviceAndDuration(btDevice, duration);


			/** Update average encounter duration */
			double avgEncDuration_old = averageDuration.getAverageEncounterDuration(previousTimeSlot);	

			double avgEncDuration_new = (duration.getEncounterDuration(previousTimeSlot) + (day-1) * avgEncDuration_old)/day;

			averageDuration.setAverageEncounterDuration(previousTimeSlot, avgEncDuration_new);
			datasource.updateBTDevAvgEncounterDuration(averageDuration);

			/** Update social weight */
			double k = 0;
			int index = previousTimeSlot;
			double dailySampleNumber = 24;
			double sw  = 0.0;

			Log.i("BT","Calculating weight!!!");

			while(k<24){
				if(index == 24)
					index = 0;

				double levels = dailySampleNumber/(dailySampleNumber+k);
				double avgDurationPreviousSlot = (double)averageDuration.getAverageEncounterDuration(index);

				sw = sw + (levels * avgDurationPreviousSlot);

				index++;
				k++;
			}
			socialWeight.setSocialWeight(previousTimeSlot, sw);
			datasource.updateBTDevSocialWeight(socialWeight);

			notifyDataBaseChange ();
		}

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
	 * This method shows the Bluetooth devices stored on the DB. 
	 */
	public void showDevicesOnDB() {
	
		Map<String,BTUserDevice> tempListOfDevice = datasource.getAllBTDevice(); 
		Map<String,BTUserDevEncounterDuration> tempListOfDevEncounterDuration = datasource.getAllBTDevEncounterDuration(); 
		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();

		while (devIterator.hasNext()){
			String btDev = devIterator.next();

			int timeSlot = 0;
			while(timeSlot<24){	
				if((double)(datasource.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot)!=0.0){
				}
				timeSlot++;
			}
		}
	}

	/**
	 * This method notifies a database change to the listeners.
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
