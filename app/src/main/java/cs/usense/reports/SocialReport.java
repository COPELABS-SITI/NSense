/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/02/01.
 * Class is part of the NSense application.
 */

package cs.usense.reports;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import cs.usense.db.NSenseDataSource;

/**
 * This class builds the social report
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class SocialReport {

    /** This variable is used to debug SocialReport class */
    private static final String TAG = "SocialReport";

    /** This variable stores the file name of social report */
    public static final String SOCIAL_REPORT_NAME = "SocialReport";

    /** Time to schedule social report */
    private static final int SCHEDULING_TIME = 60 * 1000;

    /** This variable is used to access functionality of NSense Data base */
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
     * Social ReportItem constructor
     * @param dataSource database
     */
    SocialReport(NSenseDataSource dataSource) {
        mDataSource = dataSource;
        mHandler.postDelayed(mRunnable, SCHEDULING_TIME);
    }

    /**
     * This method builds the social report on a csv file
     * @param context application context
     */
    public static void buildReport(final Context context) {
        NSenseDataSource dataSource = NSenseDataSource.getInstance(context);
        new BackgroundTask().buildReport(context, dataSource.fetchSocialReportTable(), SOCIAL_REPORT_NAME);
    }

    /**
     * This method build and send the social report on a csv file
     * @param context application context
     */
    public static void sendReport(final Context context) {
        NSenseDataSource dataSource = NSenseDataSource.getInstance(context);
        new BackgroundTask().buildReport(context, dataSource.fetchSocialReportTable(), SOCIAL_REPORT_NAME);
        BackgroundTask.sendEmail(context, SOCIAL_REPORT_NAME);
    }

    /**
     * This method is used to store information on social report table
     */
    private void insertDataOnTable() {
        mDataSource.insertDataOnSocialReportTable();
    }

    /**
     * This method is used to stop social report
     */
    public void close() {
        mHandler.removeCallbacks(mRunnable);
    }

}