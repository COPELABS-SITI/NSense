/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class manage and trigger the alerts.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.inferenceModule;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import cs.usense.R;
import cs.usense.activities.AlertInterestsActivity;
import cs.usense.preferences.InterestsPreferences;

class AlertManager {

    /** This variable is used to debug AlertManager class */
    private static final String TAG = "AlertManager";

    /** This variable is used to schedule a handler */
    private static final int ONE_MINUTE_IN_MILISEC = 60 * 1000;

    /** This variable is used to store users interests */
    private ArrayList<SocialDetail> mSocialDetails;

    /** This variable is used to store my interests */
    private ArrayList<String> mMyInterests;

    /** This variable is used to store the application context */
    private Context mContext;

    /** This handler is used to schedule the alerts trigger */
    private Handler mAlertHandler = new Handler();

    /** This thread is used to trigger de alerts */
    private Runnable mAlertThread = new Runnable() {

        @Override
        public void run() {
            checkAlerts(new ArrayList<>(SocialInteraction.getCurrentSocialInformation()));
            mAlertHandler.postDelayed(this, ONE_MINUTE_IN_MILISEC);
        }
    };


    /**
     * This function is the constructor of the AlertManager class.
     * @param context application context
     */
    AlertManager(Context context) {
        mContext = context;
        mAlertHandler.post(mAlertThread);
    }

    /**
     * This method is used to check if the alerts are enabled and trigger them.
     * @param socialDetails users interests
     */
    private void checkAlerts(ArrayList<SocialDetail> socialDetails) {
        Log.i(TAG, "social detail is: " + socialDetails.toString());

        /** Alert 1 */
        if(InterestsPreferences.isFriendsAroundAlertEnabled(mContext) && checkSocialSituation()) {
            alertPeersAroundMe(socialDetails);
        }

        /** Alert 2 */
        if(InterestsPreferences.isInterestsAroundAlertEnabled(mContext)) {
            alertSameInterestsAroundMe(socialDetails);
        }

    }

    /**
     * This method checks if the alert 1 must be triggered
     * @return true to trigger alert 1
     */
    private boolean checkSocialSituation() {
        boolean result = false;
        if((SocialDetail.getSiPercentage() < SocialDetail.getAvgSiPercentage() * 0.4) &&
                (SocialDetail.getPropPercentage() < SocialDetail.getAvgPropPercentage() * 0.2)) {
            result = true;
        }
        return result;
    }

    /**
     * Trigger Alert 1
     * @param socialDetails social information
     */
    private void alertPeersAroundMe(ArrayList<SocialDetail> socialDetails) {
        if(checkPeersAroundMe(socialDetails)) {
            createNotification(mContext.getString(R.string.friends_around_you),
                    mContext.getString(R.string.click_to_check_it),
                    new Intent(mContext, AlertInterestsActivity.class));
        }
    }

    /**
     * Trigger Alert 2
     * @param socialDetails social information
     */
    private void alertSameInterestsAroundMe(ArrayList<SocialDetail> socialDetails) {
        HashMap<String, String> interestsData = new HashMap<>();
        interestsCompare(interestsData, socialDetails);
        if(interestsData.size() > 0) {
            if(isInterestsChanged(socialDetails) || isMyInterestsChanged()) {
                mSocialDetails = new ArrayList<>(socialDetails);
                Intent intent = new Intent(mContext, AlertInterestsActivity.class).putExtra("interests", true);
                createNotification(mContext.getString(R.string.friends_around_you),
                        mContext.getString(R.string.with_same_interests_as_you), intent);
            }
        }
    }

    /**
     * Checks if the interests around me changed
     * @param socialDetails social information
     * @return true if the interests changed
     */
    private boolean isInterestsChanged(ArrayList<SocialDetail> socialDetails) {
        boolean interestsChanged = false;
        int devicesFound = 0;
        if(mSocialDetails != null) {
            if(mSocialDetails.size() == socialDetails.size()) {
                for(SocialDetail cacheSocialDetail : mSocialDetails) {
                    for(SocialDetail socialDetail : socialDetails) {
                        if(cacheSocialDetail.getDeviceName().equalsIgnoreCase(socialDetail.getDeviceName())) {
                            devicesFound++;
                            if(cacheSocialDetail.getInterests() != null && socialDetail.getInterests() != null) {
                                ArrayList<String> userInterests = new ArrayList<>(Arrays.asList(socialDetail.getInterests().split(",")));
                                ArrayList<String> cacheInterests = new ArrayList<>(Arrays.asList(cacheSocialDetail.getInterests().split(",")));
                                if (cacheInterests.size() != userInterests.size()) {
                                    interestsChanged = true;
                                    break;
                                } else {
                                    for (String interest : userInterests) {
                                        if (!cacheInterests.contains(interest)) {
                                            interestsChanged = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ((devicesFound != socialDetails.size()) || (interestsChanged));
    }

    /**
     * Checks if my interests changed
     * @return true if my interests changed
     */
    private boolean isMyInterestsChanged() {
        boolean result = false;
        ArrayList<String> myInterests = InterestsPreferences.readCategoriesFromCacheAsArrayList(mContext);
        if(mMyInterests != null) {
            if(mMyInterests.size() != myInterests.size()) {
                result = true;
            } else {
                for(String interest : myInterests) {
                    if(!mMyInterests.contains(interest)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        mMyInterests = myInterests;
        return result;
    }

    /**
     * This method compare my interests with users interests
     * @param interests HashMap with common interests
     * @param socialDetails social information
     */
    private void interestsCompare(HashMap<String, String> interests, ArrayList<SocialDetail> socialDetails) {
        ArrayList<String> myInterests = InterestsPreferences.readCategoriesFromCacheAsArrayList(mContext);
        for(SocialDetail socialDetail : socialDetails) {
            if(socialDetail.getInterests() != null) {
                ArrayList<String> categories = socialDetail.getCategories();
                for(String myInterest : myInterests) {
                    if(categories.contains(myInterest)) {
                        if(interests.containsKey(socialDetail.getDeviceName())) {
                            String newInterestValue = interests.get(socialDetail.getDeviceName());
                            newInterestValue += "," + myInterest;
                            interests.put(socialDetail.getDeviceName(), newInterestValue);
                        } else {
                            interests.put(socialDetail.getDeviceName(), myInterest);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if there are new users around me
     * @param socialDetails social information
     * @return true if are new users around me
     */
    private boolean checkPeersAroundMe(ArrayList<SocialDetail> socialDetails) {
        boolean result = false;
        int peersFound = 0;
        if(mSocialDetails != null && mSocialDetails.size() > 0) {
            for (SocialDetail latestSocialDetail : mSocialDetails) {
                for (SocialDetail socialDetail : socialDetails) {
                    if (socialDetail.getDeviceName().equals(latestSocialDetail.getDeviceName())) {
                        peersFound++;
                    }
                }
            }
        }
        if(socialDetails.size() > peersFound) {
            result = true;
        }
        return result;
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

        /** Checks if vibration feature is enabled */
        if(InterestsPreferences.isVibrationEnabled(mContext)) {
            builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        }

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.drawable.ic_launcher, builder.build());
    }

    /**
     * This method finalize the features implemented on this class
     */
    void close() {
        mAlertHandler.removeCallbacks(mAlertThread);
    }

}
