/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class builds a merged report
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.reports;


import android.content.Context;

import cs.usense.db.NSenseDataSource;

public abstract class MergedReport {

    /** This variable stores the file name of merged report */
    private static final String MERGED_REPORT_NAME = "MergedReport";

    /**
     * This method builds the interests report on a csv file
     * @param context application context
     */
    public static void buildReport(final Context context) {
        NSenseDataSource dataSource = NSenseDataSource.getInstance(context);
        new BackgroundTask().buildReport(context, dataSource.fetchMergedReport(), MERGED_REPORT_NAME);
    }

    /**
     * This method build and send the merged report on a csv file
     * @param context application context
     */
    public static void sendReport(final Context context) {
        NSenseDataSource dataSource = NSenseDataSource.getInstance(context);
        new BackgroundTask().buildReport(context, dataSource.fetchMergedReport(), MERGED_REPORT_NAME);
        BackgroundTask.sendEmail(context, MERGED_REPORT_NAME);
    }

}