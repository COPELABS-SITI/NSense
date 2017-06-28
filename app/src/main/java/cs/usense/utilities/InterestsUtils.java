/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This provides utilities about users interests
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.utilities;


import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cs.usense.R;

public abstract class InterestsUtils {

    public static final int CATEGORIES_OFFSET = 10;

    public static final int NUMBER_OF_CATEGORIES = 20;

    private static final HashMap<Integer, String> interestsAsString = new HashMap<>();

    private static final HashMap<Integer, String> interestsRating = new HashMap<>();

    private static final HashMap<Integer, Integer> interestsReportPosition = new HashMap<>();


    public static void setup(Context context) {
        initializeInterests(context);
        initializeInterestsRating(context);
    }

    /**
     * This method initializes the HashMap of interests
     * @param context some context
     */
    private static void initializeInterests(Context context) {
        if(interestsAsString.isEmpty()) {
            String[] categories = context.getResources().getStringArray(R.array.categories);
            for(int i = 0, offset = 0; i < categories.length; i++, offset += CATEGORIES_OFFSET) {
                interestsAsString.put(offset, categories[i]);
                interestsReportPosition.put(offset, i);
            }
        }
    }

    private static void initializeInterestsRating(Context context) {
        String[] Ratings = context.getResources().getStringArray(R.array.ratings);
        for(int i = 0; i < Ratings.length; i++) {
            interestsRating.put(i + 1, Ratings[i]);
        }
    }

    public static String getRatingValue(String Rating) {
        String result = null;
        for (Map.Entry<Integer, String> entry : interestsRating.entrySet()) {
            if(entry.getValue().equals(Rating)) {
                result = entry.getKey().toString();
                break;
            }
        }
        return result;
    }

    public static String getInterestValue(String interest) {
        String result = null;
        for (Map.Entry<Integer, String> entry : interestsAsString.entrySet()) {
            if(entry.getValue().equals(interest)) {
                result = entry.getKey().toString();
                break;
            }
        }
        return result;
    }

    /**
     * This method is used to map interests number to a interest name
     * @param interestReceived interest number
     * @return interest name
     */
    public static String getInterestAsString(String interestReceived) {
        return interestsAsString.get(Integer.parseInt(interestReceived));
    }

    public static String getRatingAsString(String interestReceived) {
        return interestsRating.get(Integer.parseInt(interestReceived));
    }

    /**
     * This method returns interests categories split by semi-colon
     * @return interests categories split by semi-colon
     */
    public static String getInterestsAsCsv() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int j = 0; j < NUMBER_OF_CATEGORIES; j++) {
            String interest = interestsAsString.get(j * CATEGORIES_OFFSET);
            if(interest != null) {
                stringBuilder.append(interest.replace(",", " /")).append(";");
            }
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * This method converts interests split by comma to semi-colon on table format
     * @param interests interests split by comma
     * @return interests split by semi-colon on table format
     */
    public static String getInterestsDataAsCsv(String interests) {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> interestsTable = new ArrayList<>(Collections.nCopies(interestsAsString.size(), "0"));
        if(!interests.equals("null")) {
            String[] interestsSplit = interests.split(",");
            for (String interestSplit : interestsSplit) {
                int stars = Integer.valueOf(interestSplit) % 10;
                if (stars != 0) {
                    interestsTable.set(interestsReportPosition.get(Integer.parseInt(getCategoryOfRating(interestSplit))), String.valueOf(stars));
                }
            }
        }
        for(int i = 0; i < interestsTable.size(); i++) {
            stringBuilder.append(interestsTable.get(i));
            if(i < interestsTable.size() - 1) {
                stringBuilder.append(";");
            }
        }
        return stringBuilder.toString();
    }

    public static String getCategoryOfRating(String category) {
        return String.valueOf((Integer.parseInt(category) / CATEGORIES_OFFSET) * CATEGORIES_OFFSET);
    }

    public static ArrayList<String> getRatingsAsString(Context context, String category) {
        ArrayList<String> Ratings = new ArrayList<>();
        Collections.addAll(Ratings, context.getResources().getStringArray(R.array.ratings));
        return Ratings;
    }

}
