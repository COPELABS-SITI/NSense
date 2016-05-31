/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. It provides support for accelerometer pipeline.
 * @author Saeik Firdose (COPELABS/ULHT)
 */

package cs.usense.accelerometer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import com.meapsoft.FFT;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import cs.usense.UsenseService;
import cs.usense.db.DataBaseChangeListener;
import cs.usense.db.UsenseDataSource;

/**
 * This class provides various methods to provide Accelerometer sensor data 
 */
public class AccelerometerPipeline extends BroadcastReceiver{

	private static final String TAG = "ACCELEROMETER";
	
	/** Interface to global information about an application environment. */
	private Context mContext = null;
	
	/** This class is to access functionality from Activity Classification Task of accelerometer pipeline */
	private ActivityClassificationTask mActivityClassificationTask;
	
	/** This class is to access functionality of Sensor Manager */
	private AccelerSenManager accelerSenManager;
	
	/** This class is to access functionality of Accelerometer Listener */
	private AccelerometerListener accelerListener;
	
	/** This class is to access functionality of Usense Data base */
	private UsenseDataSource dataSource;
	
	/** This class is to access the functionality of Usense Service */
	private UsenseService callback = null;
	
	/** This class is to access functionality of Sensor Manager */
	private SensorManager mSensorManager;
	
	/** This variable is to store the Buffer */
	private static ArrayBlockingQueue<Double> mAccBuffer;
	
	/** This variable is to store action type */
	private static String actiontype = "";
	
	/** This variable is to get the action details  */
	private static ActionsEntry actualActionEntry = null;
	
	/** This variable is to get the database listeners  */
	private ArrayList<DataBaseChangeListener> listeners = new ArrayList<DataBaseChangeListener> ();

	/**
	 * This class provides various fields to set PreviousAction
	 *
	 */
	private class PreviousAction {
		public String mPrevAction;
		public int mId;
		public double mDuration;
		public long mStartTime;
		public int mHour;
		public int mDay;

		public String getmPrevAction() {
			return mPrevAction;
		}
	}


	private PreviousAction mPreviousAction = new PreviousAction();
	public static String mpreviousActionType = "";
	public static double mpreviousDuration = 0;


	/**
	 * This class instantiate the AccelerometerPipeline and initialize the listener and activity classification task
	 * @param callback Supply functionality for UsenseActivity to use
	 * @param dataSource UsenseDataSource to access various methods and information of the USense Data base
	 */
	public AccelerometerPipeline(UsenseService callback, UsenseDataSource dataSource) {
		Log.i(TAG,"Inside the AccelerometerPipeline constructor");
		this.callback = callback;
		this.mContext = callback.getBaseContext();

		this.dataSource = dataSource;


		mAccBuffer = new ArrayBlockingQueue<Double>(
				Globals.ACCELEROMETER_BUFFER_CAPACITY);		 

		accelerListener = new AccListener();
		accelerSenManager = new AccelerSenManager(callback);
		accelerSenManager.setAccelerListener(accelerListener);

		mActivityClassificationTask = new ActivityClassificationTask();
		mActivityClassificationTask.execute();

	}


	/**
	 * This method close the context
	 * @param context Interface to global information about an application environment. 
	 */
	public void close(Context contect) {
		accelerSenManager.close();
		mActivityClassificationTask.cancel(true);
	}


	/**
	 * This class provides the real time activities by extending with asynchronous task
	 *
	 */
	public class ActivityClassificationTask extends AsyncTask<Void, Void, Void>
	{

		private static final int mFeatLen = Globals.ACCELEROMETER_BLOCK_CAPACITY + 2;
		Handler mActionsUpdateHandler = new Handler();


		public ActivityClassificationTask() {

		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		protected Void doInBackground(Void... arg0)
		{

			ArrayList<Double> featureVectorArray = new ArrayList<Double>(mFeatLen);
			int blockSize = 0;
			FFT fft = new FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY);
			double[] accBlock = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
			double[] re = accBlock;
			double[] im = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
			double max = Double.MIN_VALUE;
			int k1 = 0; 
			int k3 = 0;
			ArrayList<Integer> listArray = new ArrayList<Integer>();
			int[] k2 = new int[0];

			long start = 0;
			long end = 0;
			int tempduration = 0;
			double mDuration = 0;

			/** Get Hour and Day */
			Calendar mDate = Calendar.getInstance();
			mDate.setTimeInMillis(start);
			int mHour = mDate.get(Calendar.HOUR_OF_DAY);
			int mDay = mDate.get(Calendar.DAY_OF_MONTH);


			while (true) {
				try {

					/*// need to check if the AsyncTask is cancelled or not in the while loop
					 */					
					if (isCancelled () == true)
					{
						return null;
					}

					/*// Dumping buffer
					 * */
					accBlock[blockSize++] = mAccBuffer.take().doubleValue();

					if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
						blockSize = 0;

						max = .0;
						for (double val : accBlock) {
							if (max < val) {
								max = val;
							}
						}

						fft.fft(re, im);

						for (int i = 0; i < re.length; i++) {
							double mag = Math.sqrt(re[i] * re[i] + im[i]
									* im[i]);
							featureVectorArray.add(Double.valueOf(mag));
							im[i] = .0; 
							/** Clear the field */
						}

						/**Append max after frequency component */
						featureVectorArray.add(Double.valueOf(max));

						mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
						if ((mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null)){

							k1 = (int)WekaClassifier.classify1(featureVectorArray.toArray());

						} else if((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null))
						{
							k1 = (int)WekaClassifier.classify(featureVectorArray.toArray());

						}


						/** Initialize array of 16 size and Store the entries
						 * computing the relevant activity and clear the array size and reinitialize array
						 * */

						listArray.add(k1);


						if(listArray.size() == 16){
							start = System.currentTimeMillis();

							k2 = convertIntegers(listArray);

							listArray.clear();

							end  = System.currentTimeMillis();

							k3 = filterActivity(k2);

							switch (k3){
							case 0:
								actiontype = "STATIONARY";
								break;

							case 1:
								actiontype = "WALKING";
								break;

							case 2:
								actiontype = "RUNNING";
								break;

							default:
								break;

							}

							tempduration = (int) (end- start);

							long now = System.currentTimeMillis();


							if (mPreviousAction.mPrevAction == null) {
								mPreviousAction.mPrevAction = actiontype;
								mPreviousAction.mDuration =mDuration;
								mPreviousAction.mId =-1;
								mPreviousAction.mStartTime = now;
								mPreviousAction.mHour = mHour;
								mPreviousAction.mDay = mDay;
							} else {

								if(mPreviousAction.mPrevAction != actiontype) {
									mDuration = tempduration;
									end = now;
									Log.i(TAG," mDuration if action is different  : "+mDuration);
									updateAction(actiontype,mDuration,start);

								}else {
									mDuration +=tempduration;
									Log.i(TAG," mDuration when the activity is same  : "+mDuration);
								}

								mpreviousActionType = mPreviousAction.getmPrevAction();
								mPreviousAction.mPrevAction = actiontype;
								mPreviousAction.mId =-1;
								mPreviousAction.mDuration = 0 ;
								mPreviousAction.mStartTime = now;
							}

							String currentTime = (String) DateFormat.format("dd/MM - HH:mm:ss.sss", Calendar.getInstance().getTime());
							appendLog(currentTime, actiontype);

						}

						/** Notifying the actiontype to the service */
						callback.notifyAction(actiontype);

						/** clearing feature vector */
						featureVectorArray.clear();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}


		/**
		 * This method update the database when the activity is changed
		 * @param actiontype Current action type
		 * @param mDuration Duration of the current activity
		 * @param start Start time of the activity
		 */
		private void updateAction(String actiontype, double mDuration, long start) {
			/** Get Hour and Day */
			Calendar mDate = Calendar.getInstance();
			mDate.setTimeInMillis(start);
			int mHour = mDate.get(Calendar.HOUR_OF_DAY);
			int mDay = mDate.get(Calendar.DAY_OF_MONTH);

			long now = System.currentTimeMillis();
			if (mPreviousAction.mPrevAction == null) {
				mPreviousAction.mPrevAction = actiontype;
				mPreviousAction.mDuration =mDuration;
				mPreviousAction.mId =-1;
				mPreviousAction.mStartTime = now;
				mPreviousAction.mHour = mHour;
				mPreviousAction.mDay = mDay;
			} else {

				mActionsUpdateHandler.removeCallbacks(null);

				registerActiononDB(actiontype,mDuration, start);

				if(dataSource.hasActionEntry(mPreviousAction.mPrevAction)){
					ActionsEntry action = dataSource.getActionEntry(mPreviousAction.mPrevAction);
					action.setActionCounter(action.getActionCounter()+1);
					action.setAverageDuration(action.getAverageDuration()*0.9 + (now-mPreviousAction.mStartTime)* 0.1);
					action.setHour(mHour);
					action.setDay(mDay);
					Log.i(TAG," Updated details  : "+ action);
					dataSource.updateActionEntry(action);
					notifyDataBaseChange();
				}
				mPreviousAction.mPrevAction = actiontype;
				mPreviousAction.mId =-1;
				mPreviousAction.mDuration = 0 ;
				mPreviousAction.mStartTime = now;

			}

		}



		/**
		 * This method to register actions in data base
		 * @param actiontype Current action type
		 * @param mDuration Duration of the current activity
		 * @param start Start time of the activity
		 */
		public void registerActiononDB(String actiontype, double mDuration, long start) {

			
			Calendar mDate = Calendar.getInstance();
			mDate.setTimeInMillis(start);
			int mHour = mDate.get(Calendar.HOUR_OF_DAY);
			int mDay = mDate.get(Calendar.DAY_OF_MONTH);

			/** logic for Weekdays and weekend */
			String timeframe;
			SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.UK);
			Calendar calendar = Calendar.getInstance();
			timeframe = dayFormat.format(calendar.getTime());

			if(timeframe.compareTo("Sunday")==0 ||timeframe.compareTo("Saturday")==0 ){
				timeframe = "WeekEnd";
			}
			else{
				timeframe = "WeekDay";
			}	

			long now = System.currentTimeMillis();
			if(actualActionEntry == null){
				Log.i(TAG," Inside if condition, initial entry: ");
				actualActionEntry = new ActionsEntry();
				actualActionEntry.setActionType(actiontype);
				actualActionEntry.setActionStartTime(start);
				actualActionEntry.setActionEndTime(now);
				actualActionEntry.setAverageDuration(mDuration);
				actualActionEntry.setHour(mHour);
				actualActionEntry.setActionCounter(0);
				actualActionEntry.setDay(mDay);
				actualActionEntry.setTimeFrame(timeframe);
				dataSource.registerNewActionEntry(actualActionEntry);

			}

			if(mPreviousAction.mPrevAction != actiontype) {
				Log.i(TAG," secondary entry: ");
				boolean hasAE= dataSource.hasActionEntry(actiontype);
				Log.i(TAG," hasAE: "+hasAE);
				mDuration = 0;

				if(hasAE == false ){
					actualActionEntry = new ActionsEntry();

					actualActionEntry.setActionType(actiontype);
					actualActionEntry.setActionStartTime(start);
					actualActionEntry.setActionEndTime(now);
					actualActionEntry.setAverageDuration(mDuration);
					actualActionEntry.setHour(mHour);
					actualActionEntry.setActionCounter(0);
					actualActionEntry.setDay(mDay);
					actualActionEntry.setTimeFrame(timeframe);
					dataSource.registerNewActionEntry(actualActionEntry);
					Log.i(TAG,"Second or third  Record INSERTED##::"+actualActionEntry.getActionType());
					Log.i(TAG,"Second or third  Record INSERTED duration is ##::"+actualActionEntry.getAverageDuration());
				}
				mPreviousAction.mPrevAction = actiontype;
				mPreviousAction.mId =-1;
				mPreviousAction.mDuration = 0 ;
				mPreviousAction.mStartTime = now;
				Log.i(TAG," mPreviousAction Details are ###  : "+ mPreviousAction.mDuration);

			}

		}



	}

	/**
	 * This method converts list of integers
	 * @param integers list with integers
	 * @return ret list with integers
	 */
	public static int[] convertIntegers(List<Integer> integers)
	{
		int[] ret = new int[integers.size()];
		for (int i=0; i < ret.length; i++)
		{
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}


	/**
	 * This class provide the method to listen the accelerometer changes
	 *
	 */
	public class AccListener implements AccelerometerListener {

		/* (non-Javadoc)
		 * @see cs.usense.accelerometer.AccelerometerListener#updateBuffer(float, float, float)
		 */
		public void updateBuffer(float x, float y, float z) {
			double m = Math.sqrt(x * x + y * y + z* z);
			try {
				mAccBuffer.add(m);
			} catch (IllegalStateException e) {

				/**
				 * Exception happens when reach the capacity. Doubling the buffer. 
				 * ListBlockingQueue has no such issue, But generally has worse performance
				 * */
				
				ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
						mAccBuffer.size() * 2);

				mAccBuffer.drainTo(newBuf);
				mAccBuffer = newBuf;
				mAccBuffer.add(new Double(m));
			}

		}

	}


	/**
	 * This method to filter the activities to provide the confidence level
	 * @param k1 Number of activity instances
	 * @return activityType activityType Type of the activity
	 */
	public static int filterActivity(int[] k1) {

		int[] array = k1;

		Map<Integer,Integer> counterMap = new HashMap<Integer, Integer>();

		for(int i=0;i<array.length;i++){
			if(counterMap.containsKey(array[i])){
				counterMap.put(array[i], counterMap.get(array[i])+1 );
			}else{
				counterMap.put(array[i], 1);
			}
		}

		Log.i(TAG," CounterMap Activity ::"+counterMap);

		int maxValue = Integer.MIN_VALUE;
		for (int value : counterMap.values()) {
			if (value > maxValue) {
				maxValue = value;
			}
		}
		float confidence = (float)((maxValue*100)/16);

		Log.i(TAG," confidence is ::"+confidence);

		int activityType = 0;
		for (Entry<Integer, Integer> entry : counterMap.entrySet()) {  
			if (entry.getValue()==maxValue) {
				activityType = entry.getKey();
			}

		}

		return activityType;  

	}


	/**
	 * This method provides the notifications when the database has new entries or updates
	 */
	private void notifyDataBaseChange () {

		for (DataBaseChangeListener listener : this.listeners) 
		{
			listener.onDataBaseChangeAcc(new ArrayList<ActionsEntry>(dataSource.getAllActionEntry().values()));
		}
	}



	/**
	 * This method provides the current time slot
	 * @return currentTimeSlot current time slot
	 */
	public int getTimeSlot(){
		int currentTimeSlot = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		Log.i(TAG,"Current hour(slot): " + currentTimeSlot);

		return currentTimeSlot;
	}

	/**
	 * This method provides Hour Change Alarm Receiver
	 */
	public class HourChangeAlarmReceiver extends BroadcastReceiver {

		public static final String HOUR_CHANGE = "android.intent.action.HOUR_CHANGE";

		@Override
		public void onReceive(Context context, Intent intent) {}
	}


	/**
	 * This method store the logs into USenseActivity.txt file  
	 * @param text Activity Type
	 * @param start Activity Start time
	 */
	public void appendLog(String text, String start)
	{       
		File logFile = new File(Environment.getExternalStorageDirectory()+File.separator+"Experiment","USenseActivity.txt");
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			} 
			catch (IOException e)
			{

				e.printStackTrace();
			}
		}
		try
		{
			String oneChar = ";";

			/** BufferedWriter for performance, true to set append to file flag */
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.write(text);
			buf.write(oneChar);
			buf.write(start);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
	}

	/**
	 * This method of the receiver is called by the Android system  
	 * @param context Interface to global information about an application environment. 
	 * @param intent  The intent.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		return;

	}

}
