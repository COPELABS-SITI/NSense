/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/05/24.
 * Class is part of the NSense application.
 */


package cs.usense.reports;


import android.content.Context;
import android.util.Log;

import cs.usense.db.NSenseDataSource;


/**
 * This class is responsible to manage the different kind of reports.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class ReportManager {

    /** This variable is used to debug ReportManager class */
    private static final String TAG = "ReportManager";

    /** This object is responsible to build interests report */
    private InterestsReport mInterestsReport;

    /** This object is responsible to build social report */
    private SocialReport mSocialReport;

    /** This variable is used to trigger the report alert */
    private ReportAlert mReportAlert;

    /**
     * This method is the constructor of ReportManager class
     * @param context application context
     * @param dataSource data base reference
     */
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
