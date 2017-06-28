/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class is responsible to manage interests preferences
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import static android.content.Context.MODE_PRIVATE;


public abstract class GeneralPreferences {

    /** This variable is used as a key to write and read data on preferences */
    private static final String GENERAL = "General";

    /** This variable is used to store how many hours the application is running */
    private static final String HOURS_RUNNING = "Hours_Running";

    /** This variable is used to store the email where report emails are delivered */
    private static final String EMAIL_REPORT = "Email_Report";

    private static final String LOCATION = "Location";

    private static final String LOCATION_LATITUDE = "Latitude";

    private static final String LOCATION_LONGITUDE = "Longitude";

    /**
     * This method is used to store user's interests on preferences
     * @param context application context
     */
    public static void increaseHoursRunning(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(GENERAL, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int hoursRunning = getHoursRunning(context);
        editor.putInt(HOURS_RUNNING, ++hoursRunning);
        editor.apply();
    }

    /**
     * This method reads user's interests from preferences as String splitted by commas
     * @param context application context
     * @return interests as String splitted by commas
     */
    public static int getHoursRunning(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(GENERAL, MODE_PRIVATE);
        return sharedPreferences.getInt(HOURS_RUNNING, 0);
    }

    /**
     * This method stores the email where emails are delivered
     * @param context application context
     * @param email email where reports are delivered
     */
    public static void setReportEmail(Context context, String email) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(GENERAL, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL_REPORT, email);
        editor.apply();
    }

    /**
     * This method returns the mail where reports are delivered
     * @param context application context
     * @return email where reports are delivered
     */
    public static String getReportEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(GENERAL, MODE_PRIVATE);
        return sharedPreferences.getString(EMAIL_REPORT, null);
    }

    public static void setLastLocation(Context context, Location location) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOCATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOCATION_LATITUDE, String.valueOf(location.getLatitude()));
        editor.putString(LOCATION_LONGITUDE, String.valueOf(location.getLongitude()));
        editor.apply();
    }

    public static Location getLastLocation(Context context) {
        Location location = new Location("map");
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOCATION, MODE_PRIVATE);
        location.setLatitude(Double.parseDouble(sharedPreferences.getString(LOCATION_LATITUDE, null)));
        location.setLongitude(Double.parseDouble(sharedPreferences.getString(LOCATION_LONGITUDE, null)));
        return location;
    }

}
