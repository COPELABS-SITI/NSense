/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class is used to store device name and interests in common
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.models;

import java.util.ArrayList;


public class AlertInterestItem {

    /** This variable is used to store device name */
    private String mDeviceName;

    /** This ArrayList stores common interests to show */
    private ArrayList<String> mInterests = new ArrayList<>();

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


