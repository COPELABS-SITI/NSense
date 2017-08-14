/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/05/24.
 * Class is part of the NSense application.
 */

package cs.usense.reports;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import cs.usense.R;
import cs.usense.activities.ReportsActivity;
import cs.usense.preferences.InterestsPreferences;
import cs.usense.utilities.DateUtils;


/**
 * This class triggers a notification to alert the user to
 * send his reports.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
class ReportAlert implements Runnable {

    /** This variable is used to debug ReportAlert class */
    private static final String TAG = "ReportAlert";

    /** This variable stores the time when the alert is triggered */
    private static final int SCHEDULING_TIME = 60 * 1000;

    /** This variable is used to schedule the alert notification */
    private Handler mHandler = new Handler();

    /** This variable stores the application context */
    private Context mContext;

    /**
     * This method is the constructor of ReportAlert class
     * @param context application context
     */
    ReportAlert(Context context) {
        mContext = context;
        mHandler.postDelayed(this, SCHEDULING_TIME);
    }

    /**
     * This method creates a notification
     * @param title notification title
     * @param message notification message
     * @param intent what notification does
     */
    private void createNotification(String title, String message, Intent intent) {
        Log.i(TAG, "Notification created");
        Log.i(TAG, "Title: " + title + " Message: " + message);

        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setTicker("NSense");
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentIntent(PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_nsense));
        builder.setAutoCancel(true);

        /* Checks if vibration feature is enabled */
        if(InterestsPreferences.isVibrationEnabled(mContext)) {
            builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        }

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.drawable.ic_launcher, builder.build());
    }

    @Override
    public void run() {
        if(DateUtils.isEndOfWeek()) {
            createNotification(
                    mContext.getString(R.string.reports),
                    mContext.getString(R.string.please_send_your_reports),
                    new Intent(mContext, ReportsActivity.class)
            );
        }
        mHandler.postDelayed(this, SCHEDULING_TIME);
    }

    /**
     * This method is used to stop the alert scheduling
     */
    public void close() {
        mHandler.removeCallbacks(this);
    }
}
