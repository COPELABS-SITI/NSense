/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.inferenceModule;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import cs.usense.db.NSenseDataSource;
import cs.usense.pipelines.location.LocationEntry;
import cs.usense.pipelines.proximity.BTUserDevAverageEncounterDuration;
import cs.usense.pipelines.proximity.BTUserDevEncounterDuration;
import cs.usense.pipelines.proximity.BTUserDevice;
import cs.usense.pipelines.proximity.OnNewHourUpdate;
import cs.usense.preferences.GeneralPreferences;
import cs.usense.services.NSenseService;
import cs.usense.utilities.DateUtils;
import cs.usense.utilities.Utils;


/**
 * It provides support for NSenseService and MapActivity
 * classes to compute Social Interaction and Propinquity.
 * @author Saeik Firdose (COPELABS/ULHT),
 * @author Waldir Moreira (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class SocialInteraction {

	/** TAG for SocialInteraction class */
	private static final String TAG = "SocialInteraction";

	/** Time between computations by default is 1 minute = 60 * 1000 */
	private static final int TIME_BETWEEN_SI_COMPUTATION = 60 * 1000;

	/** This variable says how many hours has a day */
	private static final int HOURS_OF_A_DAY = 24;

	/** This variable represents midnight hour */
	private static final int MIDNIGHT = 24;

	/** This is EMA factor */
	private static final double FACTOR = 0.7;

	/** This list saves all social interaction information */
	private static ArrayList<SocialDetail> sListSocialDetails = new ArrayList<>();

	/** This list saves all social interaction information */
	private static ArrayList<SocialDetail> sCurrentUsingDevices = new ArrayList<>();

	/** This class is to access functionality of NSense Data base */
	private NSenseDataSource mDataSource;

	/** This object is used to call NSenseService methods */
	private NSenseService mCallback;

	/** This object is used to trigger alerts */
	private AlertManager mAlertManager;

	/** This handler is used to schedule the next computation of SI and Propinquity */
	private Handler mHandler = new Handler();

	/** This Runnable is used to compute the SI and Propinquity */
	private Runnable mRunnable = new Runnable() {

		public void run() {
			executeInBackground();
			mHandler.postDelayed(this, TIME_BETWEEN_SI_COMPUTATION);
		}
	};


	/**
	 * This class construct the SocialInteraction
	 * @param dataSource NSenseDataSource to access various methods and information of the NSenseDataSource
	 */
	public SocialInteraction(NSenseService callback, NSenseDataSource dataSource) {
		Log.i(TAG, "The SocialInteraction constructor was invoked");
		mAlertManager = new AlertManager(callback.getApplicationContext());
		mCallback = callback;
		mDataSource = dataSource;
		start();
	}

	/**
	 * This method returns the list of all current active devices.
	 * @return sCurrentUsingDevices
     */
	public static ArrayList<SocialDetail> getCurrentSocialInformation() {
		return sCurrentUsingDevices;
	}

	/**
	 * This method schedules when NSense fetchs the social data.
	 */
	private void start() {
		Log.i(TAG, "start was invoked");
		mHandler.postDelayed(mRunnable, TIME_BETWEEN_SI_COMPUTATION);
	}

	/**
	 * This method does all computation of this class
	 */
	private void executeInBackground() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			public Void doInBackground(Void... params) {
				Log.i(TAG, "Computing social interaction and propinquity");
				getSocialIntAndProp();
				if(sListSocialDetails.size() > 0) {
					computeEMA();
					SocialDetail.setSIPercentage(computeSocialPercentageValue("getSocialInteraction", SocialDetail.SI_STARS_FACTOR));
					SocialDetail.setPropPercentage(computeSocialPercentageValue("getPropinquity", SocialDetail.PROP_STARS_FACTOR));
					SocialDetail.setAvgSIPercentage(computeSocialPercentageValue("getSocialInteractionEMA", SocialDetail.SI_STARS_FACTOR));
					SocialDetail.setAvgPropPercentage(computeSocialPercentageValue("getmPropinquityEMA", SocialDetail.PROP_STARS_FACTOR));
					storeStarsInDataBase();
					storeDeviceInfo();
					filterInactiveDevices();
				}
				return null;
			}

			@Override
			public void onPostExecute(Void result) {
				Log.i(TAG, "Social interaction and propinquity data was computed successfully");
				mCallback.notifySociability(sCurrentUsingDevices);
			}
			
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void storeStarsInDataBase() {
		for(SocialDetail socialDetail : sListSocialDetails) {
			mDataSource.updateStars(
					DateUtils.getTodaysDayOfMonth(),
					socialDetail.getDeviceName(),
					socialDetail.getSocialInteractionStars(),
					socialDetail.getPropinquityStars()
			);
		}
	}

	/**
	 * This method stores on data base some device information for social report
	 */
	private void storeDeviceInfo() {
		for(SocialDetail socialDetail : sListSocialDetails) {
			mDataSource.storeSocialInfoInDataBase(
					socialDetail.getDeviceName(),
					socialDetail.getSocialInteraction(),
					socialDetail.getPropinquity(),
					computeEmaCd(socialDetail.getBtMacAddress())
			);
		}
	}

	private double computeEmaCd(String btMacAddress) {
		double result;
		int hoursRunning = GeneralPreferences.getHoursRunning(mCallback.getApplicationContext());
		if (hoursRunning < 24) {
			result = mDataSource.getBTDeviceEncounterDuration(btMacAddress).getAvgEncounterDuration(hoursRunning);
		} else {
			double k = 2 / (hoursRunning + 1);
			double current = mDataSource.getBTDeviceEncounterDuration(btMacAddress).getEncounterDuration(DateUtils.getTimeSlot());
			double last = mDataSource.getEmaCd(btMacAddress);
			result = k * current + (1 - k) * last;
		}
		return result;
	}

	/**
	 * This method filters the older devices so they do not appear in the list of
	 * devices in the neighborhood.
	 * One device is removed from the list if doesn't exists in the location database and
	 * if the bluetooth scan fails 10 times.
	 */
	private synchronized void filterInactiveDevices() {
		sCurrentUsingDevices.clear();
		ArrayList<LocationEntry> locationEntries = new ArrayList<> (mDataSource.getAllLocationEntries().values());

		Log.i(TAG, "Bluetooth devices:");
		Log.i(TAG, sListSocialDetails.toString());
		Log.i(TAG, "LocationPipeline devices:");
		Log.i(TAG, locationEntries.toString());

		for(SocialDetail socialDetail : sListSocialDetails) {
			if (socialDetail.getLastSeenEncDurationNow() != 0.0 || socialDetail.getEncDurationNow() != 0.0) {
				if (Math.abs(socialDetail.getEncDurationNow() - socialDetail.getLastSeenEncDurationNow()) > 0) {
					socialDetail.resetTimesCheckingEncDuration();
					socialDetail.setLastSeenEncDurationNow(socialDetail.getEncDurationNow());
					sCurrentUsingDevices.add(socialDetail);
				} else if (socialDetail.getTimesCheckingEncDuration() < 10) {
					socialDetail.incTimesCheckingEncDuration();
					sCurrentUsingDevices.add(socialDetail);
				} else {
					for (LocationEntry locationEntry : locationEntries) {
						if (socialDetail.getDeviceName().equalsIgnoreCase(locationEntry.getDeviceName())) {
							sCurrentUsingDevices.add(socialDetail);
						}
					}
				}
			} else {
				for (LocationEntry locationEntry : locationEntries) {
					if (socialDetail.getDeviceName().equalsIgnoreCase(locationEntry.getDeviceName())) {
						sCurrentUsingDevices.add(socialDetail);
					}
				}
			}
		}
	}

	/**
	 * This method computes the social percentage value.
	 * @param methodToCall this string pass the method to be called.
	 * @param factor this is the factor used to compute percentage.
     * @return percentage of social interaction and propinquity.
     */
	private double computeSocialPercentageValue(String methodToCall, double factor) {
		double sumValue = 0.0;
		double result;
		try {
			for (SocialDetail socialDetail : sCurrentUsingDevices) {
				Method method = socialDetail.getClass().getMethod(methodToCall);
				double tempValue = (double) method.invoke(socialDetail);
				if(tempValue > 0) {
					sumValue += tempValue;
				}
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if(sCurrentUsingDevices.size() == 0) {
			Log.i(TAG, "Cannot divide by zero");
			result = percentageValidation(((sumValue / 1) / factor) * 100.0);
		} else {
			result = percentageValidation(((sumValue / sCurrentUsingDevices.size()) / factor) * 100.0);
		}
		return result;
	}

	/**
	 * This method checks if the percentage value is valid.
	 * There's no values over 100% or bellow than 0%.
	 * @param percentage this is the percentage level.
	 * @return percentage value validated.
     */
	private double percentageValidation(double percentage) {
		if (percentage < 0 || percentage == Double.NaN) {
			percentage = 0;
		} else if (percentage > 100) {
			percentage = 100;
		}
		return percentage;
	}

	/**
	 * This method fetch the social interaction and propinquity values.
     */
	private void getSocialIntAndProp() {
		Log.i(TAG, "getSocialIntAndProp was invoked");
		ArrayList<SocialDetail> mSocialInteractionDetails = getDeviceDetails();
		for (SocialDetail socialDetail : mSocialInteractionDetails) {
			boolean found = false;
			for (SocialDetail mSocialDetail : sListSocialDetails) {
				if (socialDetail.getDeviceName().equals(mSocialDetail.getDeviceName())) {
					mSocialDetail.setDistance(socialDetail.getDistance());
					mSocialDetail.setSocialInteraction(computeSocialInteraction(socialDetail));
					mSocialDetail.setPropinquity(computePropinquity(socialDetail));
					mSocialDetail.setEncDurationNow(socialDetail.getEncDurationNow());
					mSocialDetail.setSocialWeight(socialDetail.getSocialWeight());
					mSocialDetail.setInterests(socialDetail.getInterests());
					found = true;
					break;
				}
			}
			/* I will add this device to the list. */
			if (!found) {
				sListSocialDetails.add(socialDetail);
			}
		}
	}

	/**
	 * This method computes the social interaction value.
	 * @param socialDetail this is the object we want to compute the social interaction value
     * @return social interaction value
     */
	private double computeSocialInteraction(SocialDetail socialDetail) {
		double expoFunction = Math.exp((-Math.pow((getEnvSound() - 5.333333333), 2)) / (2 * 20.33333));
		return Math.log10(Math.log10(socialDetail.getSocialWeight())) * (1 / (4.5092497528 * Math.sqrt(2 * Math.PI))) * expoFunction * (1 / (Math.log10(socialDetail.getDistance() + 10) * getMovementActivity()));
	}

	/**
	 * This method computes the propinquity interaction value.
	 * @param socialDetail this is the object we want to compute the propinquity value
	 * @return propinquity value
	 */
	private double computePropinquity(SocialDetail socialDetail) {
		return Math.log10(socialDetail.getSocialWeight()) * (1 / ((socialDetail.getDistance() + 2) * getMovementActivity()));
	}

	/**
	 * This method computes the EMA
	 */
	private void computeEMA() {
		for (SocialDetail entry : sListSocialDetails) {
			if (entry.getDistance() > 0.0 && entry.getmPropinquityEMA() > 0.0) {
				entry.setSocialInteractionEMA(Utils.computeEMA(entry.getSocialInteractionEMA(), entry.getSocialInteraction(), FACTOR));
				entry.setPropinquityEMA(Utils.computeEMA(entry.getmPropinquityEMA(), entry.getPropinquity(), FACTOR));
			} else {
				entry.setSocialInteractionEMA(entry.getSocialInteraction());
				entry.setPropinquityEMA(entry.getPropinquity());
			}
		}
	}

	/**
	 * This method returns a list with a newest data, like new devices if they exists,
	 * actual social weight and distance.
	 * @return devDetails
     */
	private ArrayList<SocialDetail> getDeviceDetails() {
		ArrayList<SocialDetail> devDetails = new ArrayList<>();

		Map<String, BTUserDevice> tempListOfDevice = mDataSource.getAllBTDevice();
		ArrayList<LocationEntry> mLocationList = new ArrayList<> (mDataSource.getAllLocationEntries().values());

		Iterator<String> devIterator = tempListOfDevice.keySet().iterator();
		int day = OnNewHourUpdate.day;

		Log.i(TAG,"List Of Bluetooth Devices: "+ tempListOfDevice.size());
		Log.i(TAG,"List Of LocationPipeline Devices: "+ mLocationList.size());

		while (devIterator.hasNext()){
			String btDev = devIterator.next();

			BTUserDevice btDevice = mDataSource.getBTDevice(btDev);
			BTUserDevEncounterDuration duration = mDataSource.getBTDeviceEncounterDuration(btDev);
			BTUserDevAverageEncounterDuration averageDuration = mDataSource.getBTDeviceAverageEncounterDuration(btDev);

			if(btDevice.getDevName() != null) {
				try {
					Log.i(TAG, "Name of the Bluetooth Device: " + btDevice.getDevName());
					double encDuration_now = duration.getEncounterDuration(DateUtils.getTimeSlot());
					double avgEncDuration_old = averageDuration.getAverageEncounterDuration(DateUtils.getTimeSlot());
					double avgEncDuration_now = (encDuration_now + (day - 1) * avgEncDuration_old) / day;

					Log.i(TAG, btDevice.getDevName());
					Log.i(TAG, "encDuration_now: " + encDuration_now);
					Log.i(TAG, "avgEncDuration_old: " + avgEncDuration_old);
					Log.i(TAG, "avgEncDuration_now: " + avgEncDuration_now);

					/* Compute current social weight */
					double sw_now = computeSocialWeight(averageDuration, avgEncDuration_now);
					mDataSource.updateSW(btDevice.getDevAdd(), sw_now);
					Log.i(TAG, "sw_now: " + sw_now);

					SocialDetail socialDetail = new SocialDetail(btDevice.getDevName(), btDevice.getDevAdd(),
							sw_now, encDuration_now, btDevice.getInterests());
					for (LocationEntry entry : mLocationList) {
						if (entry.getBtMac() != null) {
							if (entry.getBtMac().contains(btDevice.getDevAdd())) {
								socialDetail.setDistance(entry.getDistance());
							}
						}
						if (entry.getDeviceName().contains(btDevice.getDevName())) {
							socialDetail.setDistance(entry.getDistance());
						}
					}
					devDetails.add(socialDetail);
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}
		return devDetails;
	}

	/**
	 * This method computes the social weight.
	 * @param averageDuration average duration time.
	 * @param avgEncDuration_now average encounter duration time.
	 * @return social weight.
	 */
	private double computeSocialWeight(BTUserDevAverageEncounterDuration averageDuration, double avgEncDuration_now) {
		double sw = 0.0;
		int hourOfTheDay = DateUtils.getTimeSlot() + 1;
		for(int i  = 1; i < HOURS_OF_A_DAY; i++, hourOfTheDay++) {
			if(hourOfTheDay == MIDNIGHT) {
				hourOfTheDay = 0;
			}
			sw += (((double)HOURS_OF_A_DAY / (HOURS_OF_A_DAY + i)) * averageDuration.getAverageEncounterDuration(hourOfTheDay));
		}
		Log.i(TAG, "computeSocialWeight: "  + (sw + avgEncDuration_now));
		return sw + avgEncDuration_now;
	}

	/**
	 * This method provides the movement activity from accelerometer pipeline
	 * @return activity Current activity from the accelerometer pipeline
	 */
	private double getMovementActivity() {
		double mMovementActivity = 0.0;
		String actionType = mDataSource.fetchLastMotionRegistry();
		if (actionType != null) {
			if (actionType.equals("MOVING")) {
				mMovementActivity = 5;
			} else {
				mMovementActivity = 1;
			}
		}
		Log.i(TAG, "mMovementActivity value :: " + mMovementActivity);
		return mMovementActivity;
	}

	/**
	 * This method provides the Environment sound
	 * @return Environment sound from the Sound pi
	 */
	private double getEnvSound() {
		double mEnvironmentSound = 0.0;
		String envSoundType = mDataSource.fetchLastSoundRegistry();
		if (envSoundType != null) {
			if (envSoundType.equals("QUIET")) {
				mEnvironmentSound = 1;
			} else if (envSoundType.equals("NORMAL")) {
				mEnvironmentSound = 5;
			} else if (envSoundType.equals("ALERT")) {
				mEnvironmentSound = 10;
			} else if (envSoundType.equals("NOISY")) {
				mEnvironmentSound = 15;
			}
		}
		Log.i(TAG, "mEnvironmentSound is :: " + mEnvironmentSound);
		return mEnvironmentSound;
	}

	/**
	 * This method closes the SocialInteraction Pipeline.
	 */
	public void close() {
		Log.i(TAG, "close was invoked");
		mDataSource.closeDB();
		mAlertManager.close();
		mHandler.removeCallbacks(mRunnable);
	}

}