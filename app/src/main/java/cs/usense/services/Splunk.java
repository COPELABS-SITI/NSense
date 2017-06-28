/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class sends an error report to Splunk Mint Cloud.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.services;

import android.app.Application;

import com.splunk.mint.Mint;

public class Splunk extends Application {
	
    @Override
    public void onCreate() {
        super.onCreate();
        // The following line triggers the initialization of Splunk Mint.
        Mint.initAndStartSession(this, "4fe0914a");
    }

}
