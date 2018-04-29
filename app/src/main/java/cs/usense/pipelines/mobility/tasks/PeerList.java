/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/11.
 * Insert description here ...
 */

package cs.usense.pipelines.mobility.tasks;

import android.net.wifi.p2p.WifiP2pDeviceList;

import cs.usense.wifi.p2p.WifiP2pListener;
import cs.usense.wifi.p2p.WifiP2pListenerManager;

public class PeerList implements WifiP2pListener.PeersAvailable {

    public PeerList() {
        WifiP2pListenerManager.registerListener(this);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        /* peer list */
    }
}

