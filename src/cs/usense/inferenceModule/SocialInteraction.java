/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. It provides support for UsenseService and 
 * USenseActivity classes to compute Social Interaction and Propinquity.
 * @author Saeik Firdose (COPELABS/ULHT)
 * @author @author Waldir Moreira (COPELABS/ULHT)
 */
package cs.usense.inferenceModule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import cs.usense.UsenseService;
import cs.usense.accelerometer.AccelerometerPipeline;
import cs.usense.bluetooth.BTUserDevAverageEncounterDuration;
import cs.usense.bluetooth.BTUserDevEncounterDuration;
import cs.usense.bluetooth.BTUserDevice;
import cs.usense.bluetooth.BluetoothCore;
import cs.usense.bluetooth.OnNewHourUpdate;
import cs.usense.db.UsenseDataSource;
import cs.usense.location.LocationEntry;
import cs.usense.microphone.MicrophonePipeline;

/**
 * This class computes the social Interaction and propinquity
 */
public class SocialInteraction {

	private static final String TAG = "SOCIALInteraction";
	
	/** This class is to access functionality of Usense Data base */
	private UsenseDataSource dataSource;
	
	/** This class is to access core functionality of Bluetooth pipeline */
	private BluetoothCore mBluetoothCore;
	
	/** This class is to access functionality of Accelerometer pipeline */
	private AccelerometerPipeline mAccelerometerPipeline;
	
	/** This class is to send or process message */
	private Handler mHandler;
	
	/** This variable is used to access the listeners */
	public static ArrayList<DeviceDetails> mDeviceDetails;
	
	/** This variable is used to get the distance */
	public static Map<String, HashMap<String, Double>>  mDistance;
	
	/** This variable is used to get the environment sound */
	public static double mEnvronmentSound = 0.0;
	
	/** This variable is used to get the movement activity */
	public static double mMovementActivity = 0.0;
	
	/** for EMA */
	/** This variable is used to define factor */
	private double factor = 0.0;
	
	/** This variable is used to get Social interaction EMA */
	private double runningSiEMA = 0.0;
	
	/** This variable is used to get the Propinquity EMA*/
	private double runningPropEMA = 0.0;

	/** ArrayList of SocialityDetails object */
	/** This variable is used to get the ArrayList of Previous SocialityDetails object*/
	ArrayList<SocialityDetails> mPrevSocialDetails = new ArrayList<SocialityDetails> ();
	
	/** This variable is used to get the ArrayList of old Previous SocialityDetails object*/
	ArrayList<SocialityDetails> mOldPrevSocialDetails = new ArrayList<SocialityDetails> ();

	/**
	 * This method provides the getter and setter methods of device details
	 */
	public class DeviceDetails {
		public String mMACAddress;
		public String devName;
		public double mDistance;
		public double mSW;
		public double mSI;
		public double mPropinquity;

		/**
		 * This method get the MAC address
		 * @return mMACAddress the MAC address
		 */
		public String getmMACAddress() {
			return mMACAddress;
		}

		/**
		 * This method set the MAC address
		 * @param mMACAddress the MAC address
		 */
		public void setmMACAddress(String mMACAddress) {
			this.mMACAddress = mMACAddress;
		}

		/**
		 * This method get the device name
		 * @return devName the device name
		 */
		public String getDevName() {
			return devName;
		}

		/**
		 * This method set the device name
		 * @param devName the device name
		 */
		public void setDevName(String devName) {
			this.devName = devName;
		}

		/**
		 * This method get the distance
		 * @return mDistance the distance
		 */
		public double getmDistance() {
			return mDistance;
		}

		/**
		 * This method set the distance
		 * @param mDistance the distance
		 */
		public void setmDistance(double mDistance) {
			this.mDistance = mDistance;
		}

		/**
		 * This method get the social weight
		 * @return mSW the social weight
		 */
		public double getmSW() {
			return mSW;
		}

		/**
		 * This method set the social weight
		 * @param mSW the social weight
		 */
		public void setmSW(double mSW) {
			this.mSW = mSW;
		}

		/**
		 * This method get the social interaction
		 * @return mSI the Social interaction
		 */
		public double getmSI() {
			return mSI;
		}

		/**
		 * This method set the social interaction
		 * @param mSI the Social interaction
		 */
		public void setmSI(double mSI) {
			this.mSI = mSI;
		}

		/**
		 * This method get the propinquity
		 * @return mPropinquity the propinquity
		 */
		public double getmPropinquity() {
			return mPropinquity;
		}

		/**
		 * This method set the propinquity
		 * @param mPropinquity the propinquity
		 */
		public void setmPropinquity(double mPropinquity) {
			this.mPropinquity = mPropinquity;
		}

	}


	/**
	 * This class construct the SocialInteraction 
	 * @param callback Supply UsenseService object for UsenseActivity to use
	 * @param mBluetoothCore It contains the core functionalities of the application and 
	 * 						 can support to perform the social context analysis prior to storing the required information in the database.
	 * @param mAccelerometerPipeline It provides various methods to provide Accelerometer sensor data
	 * @param dataSource UsenseDataSource to access various methods and information of the USenseDataSource
	 */
	public SocialInteraction(final UsenseService callback, BluetoothCore mBluetoothCore, AccelerometerPipeline mAccelerometerPipeline, UsenseDataSource dataSource){
		callback.getApplicationContext();
		this.dataSource = dataSource;
		this.mBluetoothCore = mBluetoothCore;
		this.mAccelerometerPipeline = mAccelerometerPipeline;

		mHandler = new Handler();

		final Runnable r = new Runnable() {

			public int count = 0;
			public void run() {
				Log.i(TAG, "Checking");
				count++;

				/** for Ema */
				ArrayList<SocialityDetails> listSocialDetailsWithEMA = new ArrayList<SocialityDetails>();

				/** Getting propinquity and Social Interaction */
				mDeviceDetails= getDeviceDetails();
				mEnvronmentSound = getEnvSound();
				mMovementActivity = getMovementActivity();

				Log.i(TAG, "mDeviceDetails size # " + mDeviceDetails.size()+"");

				/** getSocialInteraction */
				ArrayList<DeviceDetails> arrayList = getSocialIntAndProp(mDeviceDetails,mEnvronmentSound, mMovementActivity);
				ArrayList<SocialityDetails> listSocialDetails = new ArrayList<SocialityDetails>();

				Log.i(TAG, "arrayList size ### " + arrayList.size()+"");

				ListIterator<DeviceDetails> li = arrayList.listIterator();

				while(li.hasNext()){

					DeviceDetails dev = li.next();
					SocialityDetails mSociDetails = new SocialityDetails();  

					mSociDetails.setDevName(dev.getDevName());
					mSociDetails.setmDistance(dev.getmDistance());
					mSociDetails.setmPropinquity(dev.getmPropinquity());
					mSociDetails.setmSI(dev.getmSI());

					listSocialDetails.add(mSociDetails);
				}

				Log.i(TAG, "listSocialDetails size ##### " + listSocialDetails.size()+"");

				//compute Ema
				listSocialDetailsWithEMA = computeEMA(listSocialDetails);

				Log.i(TAG, "listSocialDetailsWithEMA size ####### " + listSocialDetailsWithEMA.size()+"");
				for (SocialityDetails entry : listSocialDetailsWithEMA) {
					Log.e(TAG, "####### " + entry.getDevName() + " " + entry.getmDistance() + " "+  entry.getmSI() + " " + entry.getmPropinquity() + " " + entry.getmSiEMA() + " "+  entry.getmPropEMA() + " " + entry.getmPrevSiEMA() + " " + entry.getmPrevPropEMA() );  
				}

				//Notify to the service
				callback.notifySociality(listSocialDetailsWithEMA);

				if(count >= 30*24*60){
					Log.i(TAG, "count is greater than 30 * 24 Hours :::::");
					return;
				}
				mHandler.postDelayed(this, 60*1000);
				return;
			}
		};

		mHandler.postDelayed(r, 60*1000);


	}

	/**
	 * This method Provides the social Interaction of the neighbor devices
	 * @param mDevDetails List of DeviceDetails object
	 * @param mEnvronmentSound Environment sound from the microphone
	 * @param mMovementActivity Current activity from accelerometer pipeline
	 * @return avgSW The social Interaction towards nodes in the vicinity
	 */
	public ArrayList<DeviceDetails> getSocialIntAndProp(ArrayList<DeviceDetails> mDevDetails, double mEnvronmentSound, double mMovementActivity) {
		double socialInteraction = 0.0;

		double expoFunction = 0.0;
		double propinquity = 0.0;  

		ArrayList<DeviceDetails> mSocialInteractionDetails = new ArrayList<DeviceDetails>();

		ListIterator<DeviceDetails> li = mDevDetails.listIterator();

		Log.i(TAG,"List size is : " + mDevDetails.size());

		while(li.hasNext()){
			DeviceDetails dev = li.next();
			expoFunction = Math.exp((-Math.pow((mEnvronmentSound-5.333333333), 2))/(2*20.33333));
			socialInteraction = Math.log10(dev.getmSW())*(1/(4.5092497528* Math.sqrt(2*Math.PI)))*expoFunction * (1/(Math.log10(dev.getmDistance() + 10)*mMovementActivity));
			propinquity = dev.getmSW() * (1/((dev.getmDistance()+2) * mMovementActivity));

			Log.i(TAG,": Device name : " + dev.devName + "; " + dev.getmSW() + "; "+ dev.getmDistance() +" ; "+ mEnvronmentSound +" ; "+ mMovementActivity +" ; "+ socialInteraction +" ; "+ propinquity);

			DeviceDetails mDeviceDetails = new DeviceDetails();
			mDeviceDetails.setDevName(dev.getDevName());
			mDeviceDetails.setmDistance(dev.getmDistance());
			mDeviceDetails.setmSI(socialInteraction);
			mDeviceDetails.setmPropinquity(propinquity);
			mSocialInteractionDetails.add(mDeviceDetails);

		}

		Log.i(TAG,"List size mSocialInteractionDetails : " + mSocialInteractionDetails.size());
		return mSocialInteractionDetails;
	}



	/** This method computes the EMA
	 * @param listSocialDetails List of Sociality details objects
	 * @return List of sociality details object with computed EMA
	 */
	private ArrayList<SocialityDetails> computeEMA( ArrayList<SocialityDetails> listSocialDetails) {
		ArrayList<SocialityDetails> listEMA = new  ArrayList<SocialityDetails> ();

		double si = 0.0;
		double prop = 0.0;
		double prevSiEMA = 0.0;
		double prevPropEMA = 0.0;
		factor = 0.7;

		Log.i(TAG," factor value is : " + factor);

		Log.i(TAG," Is it mPrevSocialDetails.isEmpty() : " + mPrevSocialDetails.isEmpty());
		Log.i(TAG,"  mPrevSocialDetails size : " + mPrevSocialDetails.size());

		Log.i(TAG,"   #### listSocialDetails size : " + listSocialDetails.size());


		if(mPrevSocialDetails.isEmpty()){

			for(int j=0; j<listSocialDetails.size(); j++){
				SocialityDetails initDetails =listSocialDetails.get(j);

				SocialityDetails updateIntiDetails = new  SocialityDetails ();
				updateIntiDetails.setDevName(initDetails.getDevName());
				updateIntiDetails.setmDistance(initDetails.getmDistance());
				updateIntiDetails.setmSI(initDetails.getmSI());
				updateIntiDetails.setmPropinquity(initDetails.getmPropinquity());
				updateIntiDetails.setmSiEMA(initDetails.getmSiEMA());
				updateIntiDetails.setmPropEMA(initDetails.getmPropEMA());

				mPrevSocialDetails.add(updateIntiDetails);
			}


		}else{



			mPrevSocialDetails.clear();

			for(int j=0; j<listSocialDetails.size(); j++){
				SocialityDetails initDetails =listSocialDetails.get(j);

				SocialityDetails updateIntiDetails = new  SocialityDetails ();
				updateIntiDetails.setDevName(initDetails.getDevName());
				updateIntiDetails.setmDistance(initDetails.getmDistance());
				updateIntiDetails.setmSI(initDetails.getmSI());
				updateIntiDetails.setmPropinquity(initDetails.getmPropinquity());
				updateIntiDetails.setmSiEMA(initDetails.getmSiEMA());
				updateIntiDetails.setmPropEMA(initDetails.getmPropEMA());

				mPrevSocialDetails.add(updateIntiDetails);
			}


			ListIterator<SocialityDetails> li = mPrevSocialDetails.listIterator();

			Log.i(TAG," in side the else condition List size is : " + mPrevSocialDetails.size());


			while(li.hasNext()){
				SocialityDetails dev = li.next();
				for(SocialityDetails entry : listSocialDetails){
					if(dev.getDevName().equals(entry.getDevName())){

						if(dev.getmDistance() == 0.0){
							dev.setmSiEMA(dev.getmSI());
							dev.setmPropEMA(dev.getmPropinquity());
						}
						si = entry.getmSI();
						prevSiEMA = dev.getmSI();
						Log.i(TAG," si : " + si);
						Log.i(TAG," prevSiEMA : " + prevSiEMA);
						runningSiEMA = factor * prevSiEMA + (1 - factor) * si;
						dev.setmSiEMA(runningSiEMA);

						prop = entry.getmPropinquity();
						prevPropEMA = dev.getmPropinquity();
						Log.i(TAG," prop : " + prop);
						Log.i(TAG," prevPropEMA : " + prevPropEMA);
						runningPropEMA = (factor * prevPropEMA ) + (1 - factor) * prop;
						dev.setmPropEMA(runningPropEMA);

						dev.setDevName(entry.getDevName());
						dev.setmDistance(entry.getmDistance());
						dev.setmSI(entry.getmSI());
						dev.setmPropinquity(entry.getmPropinquity());

					}
				}

				Log.i(TAG,": Device name : " + dev.devName + "; "+ dev.getmDistance() +" ; "+ dev.getmSI() +" ; "+ dev.getmPropinquity() +" ; "+ dev.getmSiEMA() +" ; "+ dev.getmPropEMA());

				SocialityDetails updateEMA = new  SocialityDetails ();

				updateEMA.setDevName(dev.getDevName());
				updateEMA.setmDistance(dev.getmDistance());
				updateEMA.setmSI(dev.getmSI());
				updateEMA.setmPropinquity(dev.getmPropinquity());
				if(dev.getmDistance() != 0.0){
					updateEMA.setmSiEMA(runningSiEMA);
					updateEMA.setmPropEMA(runningPropEMA);

				}else{
					updateEMA.setmSiEMA(dev.getmSI());
					updateEMA.setmPropEMA(dev.getmPropinquity());
				}

				listEMA.add(updateEMA);
			}
		}

		return listEMA;
	}


	/**
	 * This method Provides the average of the current social weight values
	 * @return avgSW The average of the current social weights towards nodes in the vicinity
	 */
	public ArrayList<DeviceDetails> getDeviceDetails(){
		ArrayList<DeviceDetails> devDetails = new ArrayList<DeviceDetails>();


		Map<String,BTUserDevice> tempListOfDevice = dataSource.getAllBTDevice();
		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();
		int currentTimeSlot = getTimeSlot();
		int day = OnNewHourUpdate.day;
		double sw_now = 0.0;

		Log.i(TAG,"List Of Bluetooth Devices: "+ tempListOfDevice.size());



		while (devIterator.hasNext()){
			String btDev = devIterator.next();

			BTUserDevice btDevice = dataSource.getBTDevice(btDev);
			BTUserDevEncounterDuration duration = dataSource.getBTDeviceEncounterDuration(btDev);
			BTUserDevAverageEncounterDuration averageDuration = dataSource.getBTDeviceAverageEncounterDuration(btDev) ;

			Log.i(TAG,"Name of the Bluetooth Device: "+ btDevice.getDevName());

			ArrayList<LocationEntry> mLocationList = new ArrayList<LocationEntry> (dataSource.getAllLocationEntries().values());
			
			if(btDevice.getDevName()!=null){

				double encDuration_now = duration.getEncounterDuration(currentTimeSlot) ;
				double avgEncDuration_old = averageDuration.getAverageEncounterDuration(currentTimeSlot);  
				double avgEncDuration_now = (encDuration_now + (day-1) * avgEncDuration_old)/day;

				/** Compute current social weight */
				double k = 1;
				int index = currentTimeSlot+1;
				double dailySampleNumber = 24;
				double sw  = 0.0;


				while(k<24){
					if(index == 24)
						index = 0;

					double levels = dailySampleNumber/(dailySampleNumber+k);
					double avgDurationPreviousSlot = (double)averageDuration.getAverageEncounterDuration(index);

					sw = sw + (levels * avgDurationPreviousSlot);

					index++;
					k++;
				}

				sw_now = sw + avgEncDuration_now;

				Log.i(TAG,"sw_now: "+ sw_now);

				DeviceDetails swDeviceDetails = new DeviceDetails();
				swDeviceDetails.setmMACAddress(btDevice.getDevAdd());
				swDeviceDetails.setDevName(btDevice.getDevName());
				swDeviceDetails.setmSW(Math.log10(sw_now));


				Log.i(TAG,"Location Of Devices: "+ mLocationList.size());
				if (mLocationList.size() > 0) {
					for (LocationEntry entry : mLocationList){
						Log.i(TAG,"Device name : "+ entry.getDeviceName());
						if ( entry.getDeviceName().contains(btDevice.getDevName())){
							swDeviceDetails.setmDistance(entry.getDistance());
						}

					}
				}


				devDetails.add(swDeviceDetails);

			}
		}

		return devDetails;
	}

	/**
	 * This method provides the movement activity from accelerometer pipeline
	 * @return activity Current activity from the accelerometer pipeline
	 */
	public double getMovementActivity(){


		/** Current Activity */
		String actionType = "";
		double mMovementActivity = 0.0;

		actionType = AccelerometerPipeline.mpreviousActionType;


		if (actionType == null){
			return 0;
		}

		if(actionType == "WALKING" || actionType == "RUNNING"){
			mMovementActivity = 5;
		} else if (actionType == "STATIONARY"){
			mMovementActivity = 1;
		}
		Log.i(TAG,"mMovementActivity value ::"+ mMovementActivity);


		return mMovementActivity;
	}

	/**
	 * This method provides the Environment sound
	 * @return Environment sound from the Sound pi
	 */
	public double getEnvSound(){

		/** Environment Sound */
		String envSoundType = "";
		double mEnvironmentSound = 0.0;

		envSoundType = MicrophonePipeline.mEnvironmentalSound;
		if (envSoundType == null)
			return 0;

		if(envSoundType == "QUIET" ){
			mEnvironmentSound = 1;
		} else if (envSoundType == "NORMAL"){
			mEnvironmentSound = 5;
		} else if (envSoundType == "ALERT"){
			mEnvironmentSound = 10;
		} else {
			mEnvironmentSound = 15;
		}

		Log.i(TAG,"mEnvironmentSound is ::"+ mEnvironmentSound);
		return mEnvironmentSound;
	}

	/**
	 * Provides the current time slot
	 * @return currentTimeSlot The actual time slot
	 */
	public int getTimeSlot(){
		int currentTimeSlot = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	
		return currentTimeSlot;
	}

	/**
	 * This method sorting the propinquity from the sociality details list
	 */
	public class CustomComparator implements Comparator<SocialityDetails> {

		@Override
		public int compare(SocialityDetails entry1, SocialityDetails entry2) {
			if (entry1.getmPropinquity() >= entry2.getmPropinquity()) {
				return -1;
			} else {
				return 1;
			}
		}

	}

	/**
	 * This method close the context
	 * @param context Interface to global information about an application environment. 
	 */
	public void close(Context mContext) {
		mBluetoothCore.close(mContext);
		mAccelerometerPipeline.close(mContext);
		dataSource.closeDB();
	}

}


