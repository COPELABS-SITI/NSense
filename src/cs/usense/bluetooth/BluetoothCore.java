/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the NSense application.
 * This class is contains the core functionalities of the application. 
 * The BTManager provides all the information from Bluetooth adapter so this class can 
 * perform the social context analysis prior to storing the required information in the database.
 * @author Waldir Moreira (COPELABS/ULHT)
 */
package cs.usense.bluetooth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import cs.usense.NSenseService;
import cs.usense.db.DataBaseChangeListener;
import cs.usense.db.NSenseDataSource;

public class BluetoothCore {

	/** This class is to access functionality of BTManager */
	private BTManager myBTManager;
	
	/** This class is to access functionality of ServiceBTListener */
	private ServiceBTListener btListener;
	
	/** This class is to access functionality of AlarmManager */
	private AlarmManager alarmMgr;
	
	/** This class is to access functionality of PendingIntent */
	private PendingIntent alarmIntent;
	
	/** This class is to access functionality of OnNewHourUpdate */
	private OnNewHourUpdate newHour;
	
	/** This class is to access functionality of NSense Data base */
	private NSenseDataSource datasource;
	
	/** This class is to access the functionality of NSense Service */
	private NSenseService callback = null;

	/** This variable is to check the null counter */
	int devNameNullCounter = 0;

	/**
	 * This class holds information about a device: name, MAC address, and social weight towards it.
	 */
	public class socialWeight{

		
		public String mMacAddress;
		public String mDeviceName;
		public int mSocialWeight;

		/**
		 * This method returns the MAC Address
		 * @param mMacAddress Bluetooth MAC address from the socialWeight object  
		 * */
		public String getmMacAddress() {
			return mMacAddress;
		}

		/**
		 * This method returns the Device name
		 * @param mDeviceName Device name from the socialWeight object  
		 * */
		public String getmDeviceName() {
			return mDeviceName;
		}

		/**
		 * This method returns the Social weight
		 * @param mSocialWeight Device name from the socialWeight object  
		 * */
		public int getmSocialWeight() {
			return mSocialWeight;
		}


	}

	/**
	 * This method is the constructor for BluetoothCore.
	 * @param callback Supply NSenseService object for NSenseActivity to use
	 * @param context Interface to global information about an application environment. 
	 * @param datasource NSenseDataSource to access various methods and information of the NSense Data base
	 **/
	public BluetoothCore(NSenseService callback, Context context, NSenseDataSource datasource) {
		this.callback = callback;
		this.datasource = datasource;
		myBTManager = new BTManager(context);
		myBTManager.startPeriodicScanning();
		btListener = new ServiceBTListener();
		myBTManager.setOnBTChangeListener(btListener);
		newHour = new OnNewHourUpdate(datasource);
		context.registerReceiver(newHour, new IntentFilter(cs.usense.bluetooth.OnNewHourUpdate.NEW_HOUR));

		Calendar calendar = Calendar.getInstance();

		// Change to hours
		calendar.add(Calendar.HOUR, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		Intent intent = new Intent(cs.usense.bluetooth.OnNewHourUpdate.NEW_HOUR);
		alarmIntent = PendingIntent.getBroadcast(context, 345335, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600000, alarmIntent);

	}
	/**
	 * This method checks whether the device is in database.
	 * @param deviceAdd The device MAC address.
	 * @return true, if device is in DB, false otherwise.
	 */
	public boolean isDeviceOnDB(String deviceAdd){
		return datasource.hasBTDevice(deviceAdd);
	}

	/**
	 * This method notifies a database change to the listeners.
	 */
	private void notifyDataBaseChange () {
		callback.notifySocialWeight(getSocialWeightDetails());
	}

	/**
	 * This method provides social weight information for encountered devices.
	 * @return listWeights The list of devices and social weights towards them in descending order. 
	 */
	public ArrayList<socialWeight> getSocialWeightDetails(){

		Map<String,BTUserDevice> tempListOfDevice = datasource.getAllBTDevice();

		int currentTimeSlot = getTimeSlot();
		ArrayList<socialWeight> listWeights = new ArrayList<socialWeight> ();

		Iterator<String> devHighestWeight = tempListOfDevice.keySet().iterator();

		/**Getting highest scial weight value */
		double highestSocialWeight = 0.0;
		while (devHighestWeight.hasNext()){
			String btDev = devHighestWeight.next();

			if(datasource.getBTDeviceSocialWeight(btDev).getSocialWeight(currentTimeSlot) > highestSocialWeight)
				highestSocialWeight = datasource.getBTDeviceSocialWeight(btDev).getSocialWeight(currentTimeSlot);
		}

		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();

		while (devIterator.hasNext()){
			socialWeight table = new socialWeight();
			String btDev = devIterator.next();
			table.mDeviceName = datasource.getBTDevice(btDev).getDevName();
			table.mMacAddress = datasource.getBTDevice(btDev).getDevAdd();
			table.mSocialWeight = (int) ((datasource.getBTDeviceSocialWeight(btDev).getSocialWeight(currentTimeSlot)*5)/highestSocialWeight);
			listWeights.add(table);
		}

		Collections.sort(listWeights, new CustomComparator());
		return listWeights;
	}

	/**
	 * This class sorts the devices by social weight.
	 */
	public class CustomComparator implements Comparator<socialWeight> {
		int currentTimeSlot = getTimeSlot();
		@Override
		public int compare(socialWeight entry1, socialWeight entry2) {
			if (entry1.mSocialWeight >= entry2.mSocialWeight) {
				return -1;
			} else {
				return 1;
			}
		}

	}

	/**
	 * This method unregisters BroadcastReceiver and closes the Bluetooth manager.
	 * @param context Interface to global information about an application environment.
	 */
	public void close(Context context) {
		myBTManager.close(context);
		context.unregisterReceiver(newHour);
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
	 * This class allows is used to notify BluetoothCore that a Bluetooth device has been found.
	 **/
	class ServiceBTListener implements BTDeviceFinder {

		/**
		 * This method handles information about the found Bluetooth device.
		 * @param device The BluetoothDevice information.
		 * @param btClass The BluetoothClass of the found device.
		 **/
		public void onDeviceFound(BluetoothDevice device, BluetoothClass btClass) {

			long currentTime = System.nanoTime();

			if (filterDevice(device, btClass) && device.getName()!=null){
				if(!isDeviceOnDB(device.getAddress())){

					BTUserDevice btDev = new BTUserDevice();
					BTUserDevEncounterDuration duration = new BTUserDevEncounterDuration();
					BTUserDevAverageEncounterDuration averageDuration = new BTUserDevAverageEncounterDuration();
					BTUserDevSocialWeight socialWeight = new BTUserDevSocialWeight();

					btDev.setDevAdd(device.getAddress());
					btDev.setDevName(device.getName());
					btDev.setEncounterTime(currentTime);

					duration.setDevAdd(device.getAddress());
					averageDuration.setDevAdd(device.getAddress());
					socialWeight.setDevAdd(device.getAddress());
					for(int timeSlot = 0; timeSlot < 24; timeSlot++){
						duration.setEncounterDuration(timeSlot, 0.0);
						averageDuration.setAverageEncounterDuration(timeSlot, 0.0);
						socialWeight.setSocialWeight(timeSlot, 0.0);
					}

					datasource.registerNewBTDevice(btDev, duration, averageDuration, socialWeight);
				}else{
					BTUserDevice btDev = datasource.getBTDevice(device.getAddress());
					BTUserDevEncounterDuration duration = datasource.getBTDeviceEncounterDuration(device.getAddress());

					long encounterEnd = currentTime;

					int currentTimeSlot = getTimeSlot();

					double newEncounterDuration = (encounterEnd-btDev.getEncounterStart())/1000000000.0;

					duration.setEncounterDuration(currentTimeSlot, newEncounterDuration);

					datasource.updateBTDeviceAndDuration(btDev, duration);

					notifyDataBaseChange ();

					showDevicesOnDB();
				}
			}
		}

		/**
		 * This method shows the Bluetooth devices stored on the DB. 
		 */
		public void showDevicesOnDB() {
			
			Map<String,BTUserDevice> tempListOfDevice = datasource.getAllBTDevice();
			Iterator<String> devIterator = tempListOfDevice.keySet().iterator();

			while (devIterator.hasNext()){
				String btDev = devIterator.next();

				int timeSlot = 0;
				while(timeSlot<24){  
					if((datasource.getBTDeviceEncounterDuration(btDev)).getEncounterDuration(timeSlot)!=0.0){
					}                  
					timeSlot++;
				}
			}
		}


		/**
		 * This method checks whether the device is of interest.
		 * Bluetooth devices can be of different types but only smart phones are considered.
		 * @param btClass The device class.
		 * @return true, if device is of interest, false otherwise.
		 */
		public boolean filterDevice(BluetoothDevice device, BluetoothClass btClass){
			String devtype;

			switch (btClass.getDeviceClass()) {
			case 1076: devtype = "AUDIO VIDEO CAMCORDER"; break;
			case 1056: devtype = "AUDIO VIDEO CAR AUDIO"; break;
			case 1032: devtype = "AUDIO VIDEO HANDSFREe"; break;
			case 1048: devtype = "AUDIO VIDEO HEADPHONES"; break;
			case 1064: devtype = "AUDIO VIDEO HIFI AUDIO"; break;
			case 1044: devtype = "AUDIO VIDEO LOUDSPEAKER"; break;
			case 1040: devtype = "AUDIO VIDEO MICROPHONe"; break;
			case 1052: devtype = "AUDIO VIDEO PORTABLE AUDIO"; break;
			case 1060: devtype = "AUDIO VIDEO SET TOP BOX"; break;
			case 1024: devtype = "AUDIO VIDEO UNCATEGORIZED"; break;
			case 1068: devtype = "AUDIO VIDEO VCR"; break;
			case 1072: devtype = "AUDIO VIDEO VIDEO CAMERA"; break;
			case 1088: devtype = "AUDIO VIDEO VIDEO CONFERENCING"; break;
			case 1084: devtype = "AUDIO VIDEO VIDEO DISPLAY AND LOUDSPEAKER"; break;
			case 1096: devtype = "AUDIO VIDEO VIDEO GAMING TOY"; break;
			case 1080: devtype = "AUDIO VIDEO VIDEO MONITOR"; break;
			case 1028: devtype = "AUDIO VIDEO WEARABLE HEADSET"; break;
			case 260: devtype = "COMPUTER DESKTOP"; break;
			case 272: devtype = "COMPUTER HANDHELD PC PDA"; break;
			case 268: devtype = "COMPUTER LAPTOP"; break;
			case 276: devtype = "COMPUTER PALM SIZE PC PDA"; break;
			case 264: devtype = "COMPUTER SERVER"; break;
			case 256: devtype = "COMPUTER UNCATEGORIZED"; break;
			case 280: devtype = "COMPUTER WEARABLe"; break;
			case 2308: devtype = "HEALTH BLOOD PRESSURe"; break;
			case 2332: devtype = "HEALTH DATA DISPLAY"; break;
			case 2320: devtype = "HEALTH GLUCOSe"; break;
			case 2324: devtype = "HEALTH PULSE OXIMETER"; break;
			case 2328: devtype = "HEALTH PULSE RATe"; break;
			case 2312: devtype = "HEALTH THERMOMETER"; break;
			case 2304: devtype = "HEALTH UNCATEGORIZED"; break;
			case 2316: devtype = "HEALTH WEIGHING"; break;
			case 516: devtype = "PHONE CELLULAR"; break;
			case 520: devtype = "PHONE CORDLESS"; break;
			case 532: devtype = "PHONE ISDN"; break;
			case 528: devtype = "PHONE MODEM OR GATEWAY"; break;
			case 524: devtype = "PHONE SMART"; break;
			case 512: devtype = "PHONE UNCATEGORIZED"; break;
			case 2064: devtype = "TOY CONTROLLER"; break;
			case 2060: devtype = "TOY DOLL ACTION FIGURe"; break;
			case 2068: devtype = "TOY GAMe"; break;
			case 2052: devtype = "TOY ROBOT"; break;
			case 2048: devtype = "TOY UNCATEGORIZED"; break;
			case 2056: devtype = "TOY VEHICLe"; break;
			case 1812: devtype = "WEARABLE GLASSES"; break;
			case 1808: devtype = "WEARABLE HELMET"; break;
			case 1804: devtype = "WEARABLE JACKET"; break;
			case 1800: devtype = "WEARABLE PAGER"; break;
			case 1792: devtype = "WEARABLE UNCATEGORIZED"; break;
			case 1796: devtype = "WEARABLE WRIST WATCH"; break;
			default: devtype="Other type of device"; break;
			}

			return true;
		}



	}
}


