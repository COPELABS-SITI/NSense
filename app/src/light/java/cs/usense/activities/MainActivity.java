/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class controls the User Interface and update it
 * based on NSenseService results.
 * @author Saeik Firdose (COPELABS/ULHT),
 * @author Luis Lopes (COPELABS/ULHT),
 * @author Waldir Moreira (COPELABS/ULHT),
 * @author Reddy Pallavali (COPELABS/ULHT)
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import cs.usense.R;
import cs.usense.preferences.InterestsPreferences;
import cs.usense.services.NSenseService;

/**
 * This class provides the layout of NSense application and initialize the NSense Service
 */
public class MainActivity extends ActionBarActivity implements ServiceConnection {

    /** This TAG is used to debug MainActivity class */
    private static final String TAG = "MainActivity";

    /** This variable is request to bound service */
    private boolean mIsServiceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate was invoked");
        setup();
    }

    /**
     * This method initializes all needed objects.
     */
    private void setup() {
        Log.i(TAG, "setup was invoked");

         /** Checks if the service is already running */
        if (NSenseService.isMyServiceRunning(NSenseService.class, this)) {
            Log.i(TAG, "Service is running.");
            bindService(new Intent(MainActivity.this, NSenseService.class), this, BIND_AUTO_CREATE);
        } else {
            Log.i(TAG, "Service is not running.");
            doBindService();
        }

        if(InterestsPreferences.readCategoriesFromCacheAsArrayList(this).size() == 0) {
            startActivity(new Intent(this, CategoriesActivity.class));
            Toast.makeText(this, getString(R.string.set_your_interests_to_proceed), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * This method creates the service connection, which can be used to bind the NSense service
     */
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        Log.i(TAG, "onServiceConnected was invoked");
        mIsServiceBound = true;
    }

    /**
     * This method is called when the service goes to the disconnected state.
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, "onServiceDisconnected was invoked");
        mIsServiceBound = false;
    }

    /**
     * This method unbind the NSense service
     */
    private void doUnbindService() {
        Log.i(TAG, "doUnbindService was invoked");
        if (mIsServiceBound) {
            Log.i(TAG, "the unbindService was done");
            unbindService(this);
        }
        mIsServiceBound = false;
    }

    /**
     * This method bind the NSense service
     */
    private void doBindService() {
        Log.i(TAG, "doBindService was invoked");
        startService(new Intent(this, NSenseService.class));
        bindService(new Intent(this, NSenseService.class), this, BIND_AUTO_CREATE);
    }

    /**
     * This method is called when the activity is going to be destroyed
     */
    @Override
    public void onDestroy() {
        Log.i(TAG, "OnDestroy was invoked");
        doUnbindService();
        super.onDestroy();
    }

}
