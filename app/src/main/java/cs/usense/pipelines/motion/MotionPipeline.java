/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for accelerometer pipeline.
 * @author Saeik Firdose (COPELABS/ULHT)
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.pipelines.motion;


import android.os.AsyncTask;
import android.util.Log;

import com.meapsoft.FFT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;

import cs.usense.db.NSenseDataSource;
import cs.usense.services.NSenseService;
import cs.usense.utilities.DateUtils;
import cs.usense.utilities.Utils;

/**
 * This class provides various methods to provide Accelerometer sensor data
 */
public class MotionPipeline implements MotionListener {

    /** TAG to debug MotionPipeline class */
    private static final String TAG = "ACCELEROMETER";

    /** This is the filename to save the accelerometer logs */
    private static final String MOTION_FILE_NAME = "Motion";

    /** Number of samples to compute the activity type */
    private static final int NUMBER_OF_SAMPLES = 8;

    /** This is the value of the stationary activity type */
    private static final int STATIONARY_VALUE = 0;

    /** This class is to access functionality of Sensor Manager */
    private MotionManager mMotionManager;

    /** This class is to access functionality of NSense Data base */
    private NSenseDataSource mDataSource;

    /** This variable is to store the Buffer */
    private ArrayBlockingQueue<Double> mAccBuffer = new ArrayBlockingQueue<>(MotionGlobals.ACCELEROMETER_BUFFER_CAPACITY);

    /** This object stores the actions information, like activity type, activity duration */
    private MotionEntry mPreviousAction = null;

    /**
     * This class instantiate the MotionPipeline and initialize the listener and activity classification task
     * @param callback   Supply functionality for MainActivity to use
     * @param dataSource NSenseDataSource to access various methods and information of the NSense Data base
     */
    public MotionPipeline(NSenseService callback, NSenseDataSource dataSource) {
        Log.i(TAG, "MotionPipeline constructor was invoked");
        mDataSource = dataSource;
        mMotionManager = new MotionManager(callback, this);
        start();
    }

    /**
     * This method to filter the activities to provide the confidence level
     * @param activities Number of activity instances
     * @return activityType activityType Type of the activity
     */
    private int filterActivity(ArrayList<Integer> activities) {
        Map<Integer, Integer> counterMap = new HashMap<>();

        for (Integer activity : activities) {
            if (counterMap.containsKey(activity)) {
                counterMap.put(activity, counterMap.get(activity) + 1);
            } else {
                counterMap.put(activity, 1);
            }
        }

        Log.i(TAG, " CounterMap Activity ::" + counterMap);

        int maxValue = 0;
        for (int value : counterMap.values()) {
            if (value > maxValue) {
                maxValue = value;
            }
        }

        float confidence = (maxValue * 100) / NUMBER_OF_SAMPLES;

        Log.i(TAG, " confidence is :: " + confidence);

        int activityType = 0;
        for (Entry<Integer, Integer> entry : counterMap.entrySet()) {
            if (entry.getValue() == maxValue) {
                activityType = entry.getKey();
            }
        }

        return activityType;
    }

    /**
     * This method provides the real time activities with an asynchronous task
     */
    private void start() {
        Log.i(TAG, "start was invoked");
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Log.i(TAG, "doInBackground was invoked");
                ArrayList<Double> featureVectorArray = new ArrayList<>(MotionGlobals.ACCELEROMETER_BLOCK_CAPACITY + 2);
                FFT fft = new FFT(MotionGlobals.ACCELEROMETER_BLOCK_CAPACITY);
                double[] re = new double[MotionGlobals.ACCELEROMETER_BLOCK_CAPACITY];
                double[] im = new double[MotionGlobals.ACCELEROMETER_BLOCK_CAPACITY];
                ArrayList<Integer> listArray = new ArrayList<>();
                String actionType;
                while (true) {
                    try {
                        double max = 0;
                        for (int i = 0; i < MotionGlobals.ACCELEROMETER_BLOCK_CAPACITY; i++) {
                            re[i] = mAccBuffer.take();
                            if (max < re[i]) {
                                max = re[i];
                            }
                        }

                        fft.fft(re, im);

                        for (int i = 0; i < re.length; i++) {
                            featureVectorArray.add(Math.sqrt(re[i] * re[i] + im[i] * im[i]));
                            im[i] = .0;
                        }

                        /** Append max after frequency component */
                        featureVectorArray.add(max);

                        /** Adds the classification to the list */
                        listArray.add(classifyActivity(featureVectorArray));

                        Log.i(TAG, "The size of listArray is " + listArray.size());
                        if (listArray.size() == NUMBER_OF_SAMPLES) {
                            Log.i(TAG, "I will infer the movement type");
                            actionType = evaluateActionType(filterActivity(listArray));
                            saveData(actionType);
                            featureVectorArray.clear();
                            listArray.clear();
                            inferNextAction(actionType, listArray);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * This method classifies the activities by sensor type.
     * @param featureVectorArray samples array
     * @return classified value
     */
    private int classifyActivity(ArrayList<Double> featureVectorArray) {
        if (mMotionManager.isAccelerometerLinear()) {
            return (int) WekaClassifier.classifyLinear(featureVectorArray.toArray());
        } else {
            return (int) WekaClassifier.classify(featureVectorArray.toArray());
        }
    }

    /**
     * This method is used to print logs to a file and store data in the database.
     */
    private void saveData(String actionType) {
        Log.i(TAG, "saveData was invoked");
        if (mPreviousAction == null) {
            mPreviousAction = new MotionEntry(actionType);
        } else {
            Log.i(TAG, "The activity " + mPreviousAction.getActionType() + " duration time is : " + (System.currentTimeMillis() - mPreviousAction.getActionStartTime()));
            if (!mPreviousAction.getActionType().equals(actionType)) {
                updateAction(mPreviousAction);
                mPreviousAction = new MotionEntry(actionType);
            }
        }
        Utils.appendLogs(MOTION_FILE_NAME, actionType);
        mDataSource.insertMotionRegistry(DateUtils.getTimeNowAsStringSecond(), actionType);
    }

    /**
     * This method update the database when the activity is changed
     * @param previousAction Current action information
     */
    private void updateAction(MotionEntry previousAction) {
        Log.i(TAG, "Action to update is " + previousAction.getActionType());
        previousAction.setActionEndTime(System.currentTimeMillis());
        previousAction.setActionCounter(previousAction.getActionCounter() + 1);
        previousAction.setActionDuration(previousAction.getActionEndTime() - previousAction.getActionStartTime());
        if (mDataSource.hasActionEntry(previousAction.getActionType(), previousAction.getHour())) {
            mDataSource.updateActionEntry(previousAction);
        } else {
            mDataSource.registerNewActionEntry(previousAction);
        }
    }

    /**
     * This method is used to listen the accelerometer changes
     */
    @Override
    public void updateBuffer(float x, float y, float z) {
        double m = Math.sqrt(x * x + y * y + z * z);
        try {
            mAccBuffer.add(m);
        } catch (IllegalStateException e) {
            ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<>(mAccBuffer.size() * 2);
            mAccBuffer.drainTo(newBuf);
            mAccBuffer = newBuf;
            mAccBuffer.add(m);
        }
    }

    /**
     * This method evaluates the activity type.
     * @param actionType users action
     * @param dataToEvaluate list with data to evaluate the action type
     */
    private void inferNextAction(String actionType, ArrayList<Integer> dataToEvaluate) {
        if(actionType != null) {
            if (actionType.equals("STATIONARY")) {
                Log.i(TAG, "inferNextAction -> STATIONARY");
                dataToEvaluate.add(0);
                dataToEvaluate.add(0);
            } else {
                Log.i(TAG, "inferNextAction -> MOVING");
                dataToEvaluate.add(1);
                dataToEvaluate.add(1);
            }
        }
    }

    /**
     * This method is used to do the correlation between the values and the action type word.
     *
     * 0 -> STATIONARY
     * 1 -> WALKING
     * 2 -> RUNNING
     * 1|2 -> MOVING
     *
     * @param motionValue motion value to be checked
     */
    private String evaluateActionType(int motionValue) {
        String result;
        if(motionValue == STATIONARY_VALUE) {
            result = "STATIONARY";
        } else {
            result = "MOVING";
        }
        return result;
    }

    /**
     * This method close the context
     */
    public void close() {
        Log.i(TAG, "close was invoked");
        mMotionManager.close();
    }

}