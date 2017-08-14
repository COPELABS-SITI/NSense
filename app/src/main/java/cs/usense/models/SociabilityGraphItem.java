/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.models;


/**
 * Class is part of the NSense application. This class instantiates
 * an activity to show an history of social interaction and propinquity
 * in a scale that starts on 0 to 5.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public class SociabilityGraphItem {

    /** This variable stores the date of data */
    private String mDate;

    /** This variable stores how many stars sociability has on that date */
    private float mSociabilityStars;

    /** This variable stores how many stars propinquity has on that date */
    private float mPropinquityStars;

    /**
     * This method is the constructor of SociabilityGraphItem class
     * @param date date of data
     * @param sociabilityStars quantity of sociability stars
     * @param propinquityStars quantity of propinquity stars
     */
    public SociabilityGraphItem(String date, float sociabilityStars, float propinquityStars) {
        mDate = date;
        mSociabilityStars = sociabilityStars;
        mPropinquityStars = propinquityStars;
    }

    /**
     * This method returns the date
     * @return date
     */
    public String getDate() {
        return mDate;
    }

    /**
     * This method returns quantity of sociability stars
     * @return quantity of sociability stars
     */
    public float getSociabilityStars() {
        return mSociabilityStars;
    }

    /**
     * This method returns quantity of propinquity stars
     * @return quantity of propinquity stars
     */
    public float getPropinquityStars() {
        return mPropinquityStars;
    }

}
