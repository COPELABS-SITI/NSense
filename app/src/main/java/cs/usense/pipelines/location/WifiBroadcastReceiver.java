/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/16.
 * Class is part of the NSense application. It provides support for location pipeline.
 */

package cs.usense.pipelines.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;


/**
 * This broadcast receiver is triggered when the WI-FI state changes
 * If the WI-FI goes down, this method will put it up again.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifimanager.setWifiEnabled(true);
        }
    }

}