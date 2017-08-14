/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/02/01.
 * Class is part of the NSense application.
 */


package cs.usense.services;

import android.app.Application;

import com.splunk.mint.Mint;


/**
 * This class sends an error report to Splunk Mint.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class Splunk extends Application {
	
    @Override
    public void onCreate() {
        super.onCreate();
        // The following line triggers the initialization of Splunk Mint.
        Mint.initAndStartSession(this, "4fe0914a");
    }

}
