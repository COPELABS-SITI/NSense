/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import cs.usense.utilities.InterestsUtils;

import static android.content.Context.MODE_PRIVATE;
import static cs.usense.wifi.p2p.TextRecordKeys.INTERESTS_KEY;


/**
 * This class is responsible to manage interests preferences
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public abstract class InterestsPreferences {

    /** This variable is used as a key to write and read data on preferences */
    private static final String CATEGORIES = "Categories";

    /** This variable is used as a key to write and read data on preferences */
    private static final String RATINGS = "RATINGS";


    /**
     * This method is used to sort the interests.
     * @param interests interests to be sorted
     */
    private static void sortInterests(ArrayList<String> interests) {
        ArrayList<Integer> temp = new ArrayList<>();
        for(String interest : interests) {
            temp.add(Integer.parseInt(interest));
        }
        Collections.sort(temp);
        interests.clear();
        for(int interest : temp) {
            interests.add(String.valueOf(interest));
        }
    }

    /**
     * This method checks if the interests are different than
     * the interests that was stored on cache
     * @param context application context
     * @param interests new interests
     * @return true if the interests changed, false if not
     */
    public static boolean isInterestsChanged(Context context, ArrayList<String> interests) {
        boolean result = false;
        ArrayList<String> cacheInterests = readCategoriesFromCacheAsArrayList(context);
        for(String cacheInterest : cacheInterests) {
            if(!interests.contains(cacheInterest)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * This method is used to store user's interests on preferences
     * @param context application context
     * @param categories interests to store
     */
    public static void saveCategoriesOnCache(Context context, ArrayList<String> categories) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CATEGORIES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        sortInterests(categories);
        for(int i = 0; i < categories.size() - 1; i++) {
            categories.set(i, categories.get(i).trim());
        }
        editor.putString(CATEGORIES, categories.toString());
        editor.apply();
    }

    /**
     * This method is used to store user's ratings on preferences
     * @param context application context
     * @param ratings interests to store
     */
    public static void saveRatingsOnCache(Context context, ArrayList<String> ratings) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(RATINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        sortInterests(ratings);
        for(int i = 0; i < ratings.size() - 1; i++) {
            ratings.set(i, ratings.get(i).trim());
        }
        editor.putString(RATINGS, ratings.toString());
        editor.apply();
        WifiP2pTxtRecordPreferences.setRecord(context, INTERESTS_KEY, readInterestsFromCacheAsString(context));
    }

    /**
     * This method reads user's interests from preferences as String splitted by commas
     * @param context application context
     * @return interests as String splitted by commas
     */
    public static String readInterestsFromCacheAsString(Context context) {
        return readInterestsFromCacheAsArrayList(context).toString().replace("[", "").replace("]", "").replace(" ", "");
    }

    /**
     * This method reads user's categories from preferences as String splitted by commas
     * @param context application context
     * @return interests as String split by commas
     */
    public static String readCategoriesFromCacheAsString(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CATEGORIES, MODE_PRIVATE);
        String rawInterests = sharedPreferences.getString(CATEGORIES, null);
        if(rawInterests != null) {
            rawInterests = rawInterests.replace("[", "").replace("]", "").replace(" ", "");
        }
        return rawInterests;
    }

    /**
     * This method reads user's ratings from preferences as String split by commas
     * @param context application context
     * @return interests as String split by commas
     */
    public static String readRatingsFromCacheAsString(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(RATINGS, MODE_PRIVATE);
        String rawInterests = sharedPreferences.getString(RATINGS, null);
        if(rawInterests != null) {
            rawInterests = rawInterests.replace("[", "").replace("]", "").replace(" ", "");
        }
        return rawInterests;
    }

    /**
     * This method reads user's categories from preferences as ArrayList<String>
     * @param context application context
     * @return interests as ArrayList<String>
     */
    public static ArrayList<String> readCategoriesFromCacheAsArrayList(Context context) {
        ArrayList<String> interests = new ArrayList<>();
        String rawInterests = readCategoriesFromCacheAsString(context);
        if(rawInterests != null) {
            interests = new ArrayList<>(Arrays.asList(rawInterests.split(",")));
            interests.remove("");
        }
        sortInterests(interests);
        return interests;
    }

    /**
     * This method reads user's sub-categories from preferences as ArrayList<String>
     * @param context application context
     * @return interests as ArrayList<String>
     */
    public static ArrayList<String> readInterestsFromCacheAsArrayList(Context context) {
        ArrayList<String> interests = new ArrayList<>();
        String rawInterests = readRatingsFromCacheAsString(context);
        if(rawInterests != null) {
            interests = new ArrayList<>(Arrays.asList(rawInterests.split(",")));
            interests.remove("");
        }
        sortInterests(interests);
        return interests;
    }

    public static void refreshRatings(Context context, ArrayList<String> categories) {
        ArrayList<String> subCategories = readInterestsFromCacheAsArrayList(context);
        ArrayList<String> refreshedRatings = new ArrayList<>();
        for(String subCategory : subCategories) {
            if(categories.contains(InterestsUtils.getCategoryOfRating(subCategory))) {
                refreshedRatings.add(subCategory);
            }
        }
        saveRatingsOnCache(context, refreshedRatings);
    }

    /**
     * This method checks if the vibration feature is enabled
     * @param context application context
     * @return true if vibration is enabled, false if is not enabled
     */
    public static boolean isVibrationEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("prefVibrate", true);
    }

    /**
     * This method checks if the friends around alert is enabled
     * @param context application context
     * @return true if this alert is enabled, false if is not enabled
     */
    public static boolean isFriendsAroundAlertEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("prefFriendsAround", true);
    }

    /**
     * This method checks if the same interests around alert is enabled
     * @param context application context
     * @return true if this alert is enabled, false if is not enabled
     */
    public static boolean isInterestsAroundAlertEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("prefSameInterestsAround", true);
    }

}
