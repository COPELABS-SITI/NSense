/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/8.
 * Class is part of the NSense application.
 */

package cs.usense.wifi.p2p;


import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;

import java.util.Map;

/**
 * This interface allows the communication between the wifi p2p features and your class
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public interface WifiP2pListener {

    interface ServiceAvailable extends WifiP2pListener {
        void onServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice);
    }

    interface TxtRecordAvailable extends WifiP2pListener {
        void onTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice);
    }

    interface PeersAvailable extends WifiP2pListener {
        void onPeersAvailable(WifiP2pDeviceList peers);
    }

}
