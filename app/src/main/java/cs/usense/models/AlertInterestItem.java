/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.models;

import java.util.ArrayList;


/**
 * This class is a model and is used to store device name and interests in common
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public class AlertInterestItem {

    /** This ArrayList stores common interests to show */
    private ArrayList<String> mInterests = new ArrayList<>();

    /** This variable is used to store device name */
    private String mDeviceName;


    /**
     * Constructor of AlertInterest class
     * @param deviceName device name
     * @param interests interests in common
     */
    public AlertInterestItem(String deviceName, ArrayList<String> interests) {
        mDeviceName = deviceName;
        mInterests = interests;
    }

    /**
     * This method returns the device name
     * @return device name
     */
    public String getDeviceName() {
        return mDeviceName;
    }

    /**
     * This method returns a String with common interests
     * @return common interests
     */
    public String getInterests() {
        StringBuilder sb = new StringBuilder();
        for(String interest : mInterests) {
            sb.append(interest).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return mDeviceName + " " + mInterests;
    }

}


