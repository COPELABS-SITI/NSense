/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class builds the interests report
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.reports;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import cs.usense.db.NSenseDataSource;

public class InterestsReport {

    /** This variable is used to debug InterestsReport class */
    private static final String TAG = "InterestsReport";

    /** This variable stores the file name of interests report */
    private static final String INTERESTS_REPORT_NAME = "InterestsReport";

    /** Time to schedule interests report */
    private static final int SCHEDULING_TIME = 60 * 1000;

    /** This class is to access functionality of NSense Data base */
    private NSenseDataSource mDataSource;

    /** This handler is used to schedule database inserts */
    private Handler mHandler = new Handler();

    /** The code inside of this Runnable runs when the device is waked-up */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "The next wake was scheduled");
            insertDataOnTable();
            mHandler.postDelayed(this, SCHEDULING_TIME);
        }
    };

    /**
     * Interests ReportItem constructor
     * @param dataSource database
     */
    InterestsReport(NSenseDataSource dataSource) {
        mDataSource = dataSource;
        mHandler.postDelayed(mRunnable, SCHEDULING_TIME);
    }

    /**
     * This method builds the interests report on a csv file
     * @param context application context
     */
    public static void buildReport(final Context context) {
        NSenseDataSource dataSource = NSenseDataSource.getInstance(context);
        new BackgroundTask().buildReport(context, dataSource.fetchInterestsReportTable(), INTERESTS_REPORT_NAME);
    }

    /**
     * This method build and send the interests report on a csv file
     * @param context application context
     */
    public static void sendReport(final Context context) {
        NSenseDataSource dataSource = NSenseDataSource.getInstance(context);
        new BackgroundTask().buildReport(context, dataSource.fetchInterestsReportTable(), INTERESTS_REPORT_NAME);
        BackgroundTask.sendEmail(context, INTERESTS_REPORT_NAME);
    }

    /**
     * This method is used to store information on interests report table
     */
    private void insertDataOnTable() {
        mDataSource.insertDataOnInterestsReportTable();
    }

    /**
     * This method is used to stop interests report
     */
    public void close() {
        mHandler.removeCallbacks(mRunnable);
    }

}