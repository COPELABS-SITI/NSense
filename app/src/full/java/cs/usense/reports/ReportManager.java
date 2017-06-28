/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This is responsible to manage the different kind of reports.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.reports;


import android.content.Context;
import android.util.Log;

import cs.usense.db.NSenseDataSource;

public class ReportManager {

    /** This variable is used to debug ReportManager class */
    private static final String TAG = "ReportManager";

    /** This object is responsible to build interests report */
    private InterestsReport mInterestsReport;

    /** This object is responsible to build social report */
    private SocialReport mSocialReport;

    private ReportAlert mReportAlert;


    public ReportManager(Context context, NSenseDataSource dataSource) {
        Log.i(TAG, "ReportManager constructor");
        mInterestsReport = new InterestsReport(dataSource);
        mSocialReport = new SocialReport(dataSource);
        mReportAlert = new ReportAlert(context);
    }

    /**
     * This method is used to stop Reports
     */
    public void close() {
        Log.i(TAG, "closing ReportManager");
        mInterestsReport.close();
        mSocialReport.close();
        mReportAlert.close();
    }

}
