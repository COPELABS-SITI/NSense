package cs.usense.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.util.Log;

/**
 * Created by copelabs on 20/03/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Alarm", "******************************************************** " +   intent.getType());
        AlarmInterfaceManager.notifyScanResultsAvailable();
    }

}
