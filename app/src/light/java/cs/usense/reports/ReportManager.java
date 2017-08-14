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

    private ServerReportSender mServerReportSender;

    /** This object is responsible to build interests report */
    private InterestsReport mInterestsReport;

    /** This object is responsible to build social report */
    private SocialReport mSocialReport;

    /**
     * This method is the constructor of ReportManager class
     * @param context application context
     * @param dataSource database reference
     */
    public ReportManager(Context context, NSenseDataSource dataSource) {
        Log.i(TAG, "ReportManager constructor");
        mServerReportSender = new ServerReportSender();
        mInterestsReport = new InterestsReport(dataSource);
        mSocialReport = new SocialReport(dataSource);
    }

    /**
     * This method is used to stop Reports
     */
    public void close() {
        Log.i(TAG, "closing ReportManager");
        mServerReportSender.close();
        mInterestsReport.close();
        mSocialReport.close();
    }
}
