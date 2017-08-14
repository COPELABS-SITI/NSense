/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/04/06.
 * Class is part of the NSense application. It provides support for proximity pipeline.
 */


package cs.usense.pipelines.proximity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cs.usense.R;
import cs.usense.db.NSenseDataSource;
import cs.usense.exceptions.SensorNotFoundException;
import cs.usense.services.NSenseService;


/**
 * The BTManager provides all the information from Bluetooth adapter so this class can
 * perform the social context analysis prior to storing the required information in the database.
 * @author Waldir Moreira (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class BluetoothCore {

	private final static String TAG = "Social Proximity";

	/** This class is to access functionality of BTManager */
	private BTManager myBTManager;

	/** This class is to access functionality of ServiceBTListener */
	private ServiceBTListener btListener;

	/** This class is to access functionality of AlarmManager */
	private AlarmManager alarmMgr;

	/** This class is to access functionality of PendingIntent */
	private PendingIntent alarmIntent;

	public static boolean appRestarted = false;

	public static boolean SWupdated = false;

	/** This class is to access functionality of OnNewHourUpdate */
	private OnNewHourUpdate newHour;

	/** This class is to access functionality of NSense Data base */
	private NSenseDataSource datasource;

	public static final String DATABASE_CHANGE = "com.social.proximity.CHANGE";

	/** This class is to access the functionality of NSense Service */
	private NSenseService callback = null;

	private Context context;

	/** This variable is to check the null counter */
	int devNameNullCounter = 0;

	// for debugging purposes
	private static boolean debug = true;

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
	 * @param callback Supply NSenseService object for MainActivity to use
	 * @param context Interface to global information about an application environment.
	 * @param datasource NSenseDataSource to access various methods and information of the NSense Data base
	 **/
	public BluetoothCore(NSenseService callback, Context context, NSenseDataSource datasource) throws SensorNotFoundException {
		this.callback = callback;
		this.datasource = datasource;
		this.context = callback.getApplicationContext();

		checkIfProximityPipelineCanBeInstantiated();

		if(debug){
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				//handle case of no SDCARD present
			} else {
				String dir = Environment.getExternalStorageDirectory()+File.separator+"BluetoothCore";
				//create folder
				File folder = new File(dir); //folder name
				folder.mkdirs();
				Log.i(TAG,"!!!!!! ESCREVENDO ARQUIVO !!!!!! em " + dir);
				//create file
				File file = new File(dir, "BluetoothCoreOutput.txt");
				try {
					if(!file.exists())
						file.createNewFile();
					else
						file.delete();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		myBTManager = new BTManager(context);

		storeOwnDeviceInformation();

		myBTManager.startPeriodicScanning();
		btListener = new ServiceBTListener();
		myBTManager.setOnBTChangeListener(btListener);
		newHour = new OnNewHourUpdate(datasource, this);
		context.registerReceiver(newHour, new IntentFilter(cs.usense.pipelines.proximity.OnNewHourUpdate.NEW_HOUR));

		//Create preference file to understand when app restarted
		SharedPreferences daySample = context.getSharedPreferences("DayTime", Context.MODE_PRIVATE);
		createPrefFile(daySample);

		Calendar calendar = Calendar.getInstance();

		// Change to hours
		calendar.add(Calendar.HOUR, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		Intent intent = new Intent(cs.usense.pipelines.proximity.OnNewHourUpdate.NEW_HOUR);
		alarmIntent = PendingIntent.getBroadcast(context, 345335, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600000, alarmIntent);

	}

	private void checkIfProximityPipelineCanBeInstantiated() throws SensorNotFoundException {
		PackageManager pm = context.getPackageManager();
		if(!pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
			throw new SensorNotFoundException(context.getString(R.string.sensor_not_found_message, "Bluetooth"));
	}

	private void storeOwnDeviceInformation() {
		List<String> deviceInfo = getLocalInfo();
		datasource.insertDeviceInfo(deviceInfo.get(0), deviceInfo.get(1));
	}

	/**
	 * This method resets flag for when app was restarted
	 */
	public static void appRestartReset(){
		appRestarted = false;
		if(debug){
			//writeToSD("appRestarted set to false");
		}
	}

	/**
	 * This method resets flag for when SW was updated
	 */
	public static void SWupdatedReset(){
		SWupdated = false;
		if(debug){
			//writeToSD("SWupdated set to false");
		}
	}

	/**
	 * This method creates the preference file with information about the day and time slot when the app was started.
	 * If the file exists, updates it accordingly
	 */
	private void createPrefFile(SharedPreferences daySample ) {

		Calendar c = Calendar.getInstance();
		String day = "day";
		String dayNumber = "dayNumber";
		String timeSlot = "timeSlot";

		int dayCounter = 1;
		int dNumber = c.get(Calendar.DAY_OF_YEAR);
		int slot = c.get(Calendar.HOUR_OF_DAY);


		//check if preference already contains values

		if (daySample.contains(day)){
			//Application has started
			long currentTime = System.nanoTime();

			if(debug) writeToSD("Application restarted !!!");
			Log.i(TAG, "Application restarted !!!");
			appRestarted = true;

			int savedDay = daySample.getInt(day, 0);
			int savedDayNumber = daySample.getInt(dayNumber, 0);
			int savedSample = daySample.getInt(timeSlot, 0);

			if(debug){
				writeToSD("Day when app stopped: " +  savedDay + " with number " + savedDayNumber);
				writeToSD("sample when app stopped: " + savedSample);
			}

			if(dNumber == savedDayNumber){ //Application restarted in the same day
				if(debug){
					writeToSD("We are in the same day !!!");
				}
				if(slot != savedSample){ //Application restarted in the same time slot
					if(debug){
						writeToSD("We are in the same slot !!! - Update encounter time to all connected devices");
					}
					int currentSample = slot;
					String currentDay = String.valueOf(OnNewHourUpdate.day);
					int samplesToUpdate = currentSample-savedSample;

					if(debug){
						writeToSD("Current sample: " + currentSample );
						writeToSD("Current day: " + currentDay );
						writeToSD("Number of slots to update: " + samplesToUpdate );
					}

					int updateControl = 0;

					while(updateControl<=samplesToUpdate){
						if(debug) writeToSD("Updating social weights in daily sample " + (savedSample+updateControl) + " of Day " + savedDay);
						newHour.computeSocialWeight(savedDay,savedDay, savedSample+updateControl, currentTime, appRestarted, false);
						updateControl++;
					}
				}
			} else{//Application restarted in a new day
				if(debug){
					writeToSD("We are NOT in the same day !!!");
				}

				if(debug){
					writeToSD("Current time !!!! " + currentTime );
				}

				int currentSample = slot;
				int samplesToUpdatePreviousDay = 24 - savedSample;
				int samplesToUpdateCurrentDay = currentSample;
				int totalSamplesToUpdate = samplesToUpdatePreviousDay+samplesToUpdateCurrentDay;

				if(dNumber-savedDayNumber>1){
					int completeDays = dNumber-savedDayNumber-1;
					int moreSamples = completeDays*24;
					totalSamplesToUpdate = totalSamplesToUpdate + moreSamples;
				}

				if(debug){
					writeToSD("totalSamplesToUpdate: " + totalSamplesToUpdate);
				}

				int updateControl = 0;
				int dayControl = savedDay;
				int indexUpdate = savedSample;

				boolean updateOverDiffDays = false;

				//Updating slots
				while(updateControl<=totalSamplesToUpdate){

					if(indexUpdate==24){
						indexUpdate=0;
						dayControl++;
						newHour.resetDuration();
					}

					if(debug){
						writeToSD("Updating social weights in daily sample " + indexUpdate + " of Day " + dayControl);
					}

					newHour.computeSocialWeight(savedDay, dayControl, indexUpdate, currentTime, appRestarted, updateOverDiffDays);

					updateOverDiffDays = true;

					updateControl++;
					indexUpdate++;
				}

				//Update day in OnNewHourUpdate
				OnNewHourUpdate.day = dayControl;
				//dayCounter=dNumber-savedDayNumber+1;
				//daySample.putInt(day, dayControl);
				dayCounter = dayControl;

			}
		}
		//Update preference file
		SharedPreferences.Editor daySampleEdit = daySample.edit();
		daySampleEdit.clear();
		daySampleEdit.putInt(day, dayCounter);
		daySampleEdit.putInt(dayNumber, dNumber);
		daySampleEdit.putInt(timeSlot, slot);
		daySampleEdit.commit();

		if(debug){
			Log.i(TAG,"New Day stored: " +  daySample.getInt(day, 0) + " with number " + daySample.getInt(dayNumber, 0));
			Log.i(TAG,"New sample stored: " + daySample.getInt(timeSlot, 0));
			writeToSD("New Day stored: " +  daySample.getInt(day, 0) + " with number " + daySample.getInt(dayNumber, 0));
			writeToSD("New sample stored: " + daySample.getInt(timeSlot, 0));
		}

	}

	/**
	 * This method checks whether the device is in database.
	 * @param deviceAdd The device MAC address.
	 * @return true, if device is in DB, false otherwise.
	 */
	public boolean isDeviceOnDB(String deviceAdd){
		return datasource.hasBTDeviceByMac(deviceAdd);
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
	 * This method provides the name and the MAC Address of local Bluetooth adapter.
	 * @return The local info or null
	 */
	public List<String> getLocalInfo(){
		if(myBTManager == null)
			return null;
		return myBTManager.getLocalInfo();
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

					if(debug){
						writeToSD("Device name: " + device.getName() + " now added in the database");
						writeToSD("**************************");
					}

					writeToSD("duration IS "+duration);
					datasource.registerNewBTDevice(btDev, duration, averageDuration, socialWeight);
				}else{
					BTUserDevice btDev = datasource.getBTDevice(device.getAddress());
					BTUserDevEncounterDuration duration = datasource.getBTDeviceEncounterDuration(device.getAddress());

					Log.i(TAG,"onDeviceFound - SocialProximity");
					Log.i(TAG,"btDev.getDevName(): " + btDev.getDevName());
					Log.i(TAG,"btDev.getDevAdd(): " + btDev.getDevAdd());

					if(debug){
						Log.i(TAG,"Updating encounter duration for: " + btDev.getDevName() + " - MAC: " + btDev.getDevAdd());
						writeToSD("Updating encounter duration for: " + btDev.getDevName() + " - MAC: " + btDev.getDevAdd());
					}

					long encounterEnd = currentTime;
					int currentTimeSlot = getTimeSlot();

					if(appRestarted || SWupdated){ // || SWupdated){ *************
						btDev.setEncounterTime(currentTime);
						datasource.updateBTDeviceAndDuration(btDev, duration);
					}

					//double newEncounterDuration = (encounterEnd-btDev.getEncounterStart())/1000000000.0;

					double timeInContact = (encounterEnd-btDev.getEncounterStart())/1000000000.0;

					if(timeInContact<0.0){

						writeToSD("Negative value for encounter duration detected !! Resorting to previous value !! ** BluetoothCore **");

						timeInContact = 0.0;

					}

					writeToSD("timeInContact when restartd "+timeInContact);

					double newEncounterDuration = duration.getEncounterDuration(currentTimeSlot) + timeInContact;

					writeToSD("newEncounterDuration when restartd "+newEncounterDuration);

					if(debug){
						writeToSD("Old value for encounter duration: " + duration.getEncounterDuration(currentTimeSlot));
						writeToSD("encounterEnd - BTCore: " + encounterEnd);
						writeToSD("EncounterStart - BTCore: " + btDev.getEncounterStart());
						writeToSD("timeInContact - BTCore: " + timeInContact);
					}

					btDev.setEncounterTime(currentTime);
					duration.setEncounterDuration(currentTimeSlot, newEncounterDuration);
					datasource.updateBTDeviceAndDuration(btDev, duration);


					showDevicesOnDB();
				}
			}
		}

		/**
		 * This method shows the Bluetooth devices stored on the DB.
		 */
		public void showDevicesOnDB() {
			int numberDevOnDB = datasource.getAllBTDevice().entrySet().size();

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

		/**
		 * This method unregisters BroadcastReceiver, cancels the alarm, and closes the Bluetooth manager.
		 */
		public void stop() {
			context.unregisterReceiver(newHour);
			alarmMgr.cancel(alarmIntent);
			myBTManager.close(context);
		}


	}
}

