/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/11.
 * Insert description here ...
 */

package cs.usense.pipelines.mobility.tasks;


import android.net.wifi.ScanResult;

import java.util.List;

import cs.usense.wifi.legacy.WifiLegacyListener;
import cs.usense.wifi.legacy.WifiLegacyListenerManager;

public class WifiScan implements WifiLegacyListener.ScanResultsAvailable {

    public WifiScan() {
        WifiLegacyListenerManager.registerListener(this);
    }

    @Override
    public void onScanResultsAvailable(List<ScanResult> scanResults) {
        /* Scan results */
    }

}
