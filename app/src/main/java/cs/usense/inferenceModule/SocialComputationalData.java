/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.inferenceModule;

/**
 * It provides support for inference module and provides the
 * SocialDetailComputationalData object to store social weight
 * and the information to compute how many stars each node has.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
class SocialComputationalData {

    /** This variable stores the factor to compute SI stars */
    static final double SI_STARS_FACTOR = 0.0564139626;

    /** This variable stores the factor to compute mPropinquity stars */
    static final double PROP_STARS_FACTOR = 3.45900878;

    /** Social Interaction EMA variable of the SocialDetail object */
    protected double socialInteractionEMA;

    /** Propinquity EMA variable of the SocialDetail object */
    protected double propinquityEMA;

    /** Social Interaction variable of the SocialDetail object */
    private double mSocialInteraction;

    /** Propinquity variable of the SocialDetail object */
    private double mPropinquity;

    /** This variable stores the newest mEncDurationNow value */
    private double mEncDurationNow;

    /** This variable stores the previous mEncDurationNow value */
    private double mLastSeenEncDurationNow;

    /**
     * This variable stores how many times we compared without success the lastSeen and the newest
     * encDuration values
     */
    private int mTimesCheckingEncDuration;

    /** This variable stores the social weight value */
    private double mSocialWeight;

    /** This is the default constructor of the SocialComputationalData class */
    SocialComputationalData() {}

    /** This is the constructor of the SocialComputationalData class */
    SocialComputationalData(double socialWeight, double encDurationNow) {
        this.mSocialWeight = socialWeight;
        this.mEncDurationNow = encDurationNow;
    }

    /**
     * This method get the social interaction
     * @return mSocialInteraction the social interaction
     */
    public double getSocialInteraction() {
        return mSocialInteraction;
    }

    /**
     * This method get the mPropinquity
     * @return mPropinquity the mPropinquity
     */
    public double getPropinquity() {
        return mPropinquity;
    }

    /**
     * This method get the social interaction EMA
     * @return socialInteractionEMA the social interaction EMA
     */
    public double getSocialInteractionEMA() {
        return socialInteractionEMA;
    }

    /**
     * This method get the mPropinquity EMA
     * @return propinquityEMA the mPropinquity EMA
     */
    public double getmPropinquityEMA() {
        return propinquityEMA;
    }

    /**
     * This method returns the mEncDurationNow value
     * @return mEncDurationNow
     */
    public double getEncDurationNow() {
        return mEncDurationNow;
    }

    /**
     * This method returns the mLastSeenEncDurationNow
     * @return mLastSeenEncDurationNow
     */
    public double getLastSeenEncDurationNow() {
        return mLastSeenEncDurationNow;
    }

    /**
     * This method returns the value of mTimesCheckingEncDuration
     * @return mTimesCheckingEncDuration
     */
    public int getTimesCheckingEncDuration() {
        return mTimesCheckingEncDuration;
    }

    /**
     * This method returns the social weight value
     * @return mSocialWeight
     */
    public double getSocialWeight() {
        return mSocialWeight;
    }

    /**
     * This method sets a new value for mEncDurationNow
     * @param encDurationNow
     */
    public void setEncDurationNow(double encDurationNow) {
        this.mEncDurationNow = encDurationNow;
    }

    /**
     * This value sets a new value for mLastSeenEncDurationNow
     * @param lastSeenEncDurationNow
     */

    public void setLastSeenEncDurationNow(double lastSeenEncDurationNow) {
        this.mLastSeenEncDurationNow = lastSeenEncDurationNow;
    }

    /**
     * This method sets a new value for social weight
     * @param mSocialWeight
     */
    public void setSocialWeight(double mSocialWeight) {
        this.mSocialWeight = mSocialWeight;
    }

    /**
     * This method set the social interaction
     * @param socialInteraction the social interaction
     */
    public void setSocialInteraction(double socialInteraction) {
        this.mSocialInteraction = socialInteraction;
    }

    /**
     * This method set the mPropinquity
     * @param propinquity the mPropinquity
     */
    public void setPropinquity(double propinquity) {
        this.mPropinquity = propinquity;
    }

    /**
     * This method computes the stars quantity.
     * @param value this is the main value to compute the stars quantity, like SI or Propinquity
     * @param factor this value corresponds to the calculation factor of the stars
     * @return quantity of stars
     */
    protected int computeStars(double value, double factor) {
        return (int) (5 * value / factor);
    }

    /**
     * This method sets the mTimesCheckingEncDuration variable to zero.
     */
    public void resetTimesCheckingEncDuration() {
        mTimesCheckingEncDuration = 0;
    }

    /**
     * This method increments the mTimesCheckingEncDuration variable in one unit
     */
    public void incTimesCheckingEncDuration() {
        mTimesCheckingEncDuration++;
    }

}
