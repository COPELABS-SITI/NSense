/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for inference module and
 * provides the SocialDetailComputationalData object to store social weight and the information to
 * compute how many stars each node has.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.inferenceModule;


class SocialComputationalData {

    /** This variable stores the factor to compute SI stars */
    static final double SI_STARS_FACTOR = 0.0564139626;

    /** This variable stores the factor to compute propinquity stars */
    static final double PROP_STARS_FACTOR = 3.45900878;

    /** Social Interaction variable of the SocialDetail object */
    private double socialInteraction;

    /** Propinquity variable of the SocialDetail object */
    private double propinquity;

    /** Social Interaction EMA variable of the SocialDetail object */
    protected double socialInteractionEMA;

    /** Propinquity EMA variable of the SocialDetail object */
    protected double propinquityEMA;

    /** This variable stores the newest encDurationNow value */
    private double encDurationNow;

    /** This variable stores the previous encDurationNow value */
    private double lastSeenEncDurationNow;

    /**
     * This variable stores how many times we compared without success the lastSeen and the newest
     * encDuration values
     */
    private int timesCheckingEncDuration;

    /** This variable stores the social weight value */
    private double socialWeight;

    /** This is the constructor of the SocialComputationalData class */
    SocialComputationalData(double socialWeight, double encDurationNow) {
        this.socialWeight = socialWeight;
        this.encDurationNow = encDurationNow;
    }

    /** This is the default constructor of the SocialComputationalData class */
    SocialComputationalData() {

    }

    /**
     * This method get the social interaction
     * @return socialInteraction the social interaction
     */
    public double getSocialInteraction() {
        return socialInteraction;
    }

    /**
     * This method get the propinquity
     * @return propinquity the propinquity
     */
    public double getPropinquity() {
        return propinquity;
    }

    /**
     * This method get the social interaction EMA
     * @return socialInteractionEMA the social interaction EMA
     */
    public double getSocialInteractionEMA() {
        return socialInteractionEMA;
    }

    /**
     * This method get the propinquity EMA
     * @return propinquityEMA the propinquity EMA
     */
    public double getPropinquityEMA() {
        return propinquityEMA;
    }

    /**
     * This method returns the encDurationNow value
     * @return encDurationNow
     */
    public double getEncDurationNow() {
        return encDurationNow;
    }

    /**
     * This method returns the lastSeenEncDurationNow
     * @return lastSeenEncDurationNow
     */
    public double getLastSeenEncDurationNow() {
        return lastSeenEncDurationNow;
    }

    /**
     * This method returns the value of timesCheckingEncDuration
     * @return timesCheckingEncDuration
     */
    public int getTimesCheckingEncDuration() {
        return timesCheckingEncDuration;
    }

    /**
     * This method returns the social weight value
     * @return socialWeight
     */
    public double getSocialWeight() {
        return socialWeight;
    }

    /**
     * This method sets a new value for encDurationNow
     * @param encDurationNow
     */
    public void setEncDurationNow(double encDurationNow) {
        this.encDurationNow = encDurationNow;
    }

    /**
     * This value sets a new value for lastSeenEncDurationNow
     * @param lastSeenEncDurationNow
     */

    public void setLastSeenEncDurationNow(double lastSeenEncDurationNow) {
        this.lastSeenEncDurationNow = lastSeenEncDurationNow;
    }

    /**
     * This method sets a new value for social weight
     * @param socialWeight
     */
    public void setSocialWeight(double socialWeight) {
        this.socialWeight = socialWeight;
    }

    /**
     * This method set the social interaction
     * @param socialInteraction the social interaction
     */
    public void setSocialInteraction(double socialInteraction) {
        this.socialInteraction = socialInteraction;
    }

    /**
     * This method set the propinquity
     * @param propinquity the propinquity
     */
    public void setPropinquity(double propinquity) {
        this.propinquity = propinquity;
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
     * This method sets the timesCheckingEncDuration variable to zero.
     */
    public void resetTimesCheckingEncDuration() {
        timesCheckingEncDuration = 0;
    }

    /**
     * This method increments the timesCheckingEncDuration variable in one unit
     */
    public void incTimesCheckingEncDuration() {
        timesCheckingEncDuration++;
    }

}
