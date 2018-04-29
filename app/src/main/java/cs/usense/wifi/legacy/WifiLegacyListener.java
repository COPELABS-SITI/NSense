/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/11.
 * Class is part of the NSense application.
 */

package cs.usense.wifi.legacy;


import android.net.wifi.ScanResult;

import java.util.List;

/**
 * This interface allows the communication between the legacy wifi features and your class
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public interface WifiLegacyListener {

    interface ScanResultsAvailable extends WifiLegacyListener {
        void onScanResultsAvailable(List<ScanResult> scanResults);
    }

}
