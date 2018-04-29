/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/7.
 * Class is part of the NSense application.
 */

package cs.usense.wifi.p2p;

/**
 * This class contains all keys used to fill txtRecord map.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public abstract class TextRecordKeys {

    /** This variable is used to represent BT MAC information on txt record */
    public static final String BT_MAC_KEY = "0";

    /** This variable is used to represent interests information on txt record */
    public static final String INTERESTS_KEY = "1";

    /** This variable is used to represent an AP information on txt record */
    public static final String AP ="2";

    /** This variable is used to represent the probingFunctionsManager of an AP on txt record */
    public static final String RANK_FUNCTION_3 ="3";

    /** This variable is used to share the probingFunctionsManager value using formula 3 of AP on txt record*/
    public static final String RANK_FUNCTION_4 = "4";
}
