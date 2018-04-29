/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.activities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Calendar;

import cs.usense.R;
import cs.usense.services.NSenseService;
import cs.usense.utilities.Utils;


/**
 * This class provides some generic methods to activities like the action bar menu
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class ActionBarActivity extends AppCompatActivity {

    /** This variable is used to debug ActionBarActivity class */
    private static final String TAG = "ActionBarActivity";

    /** Main Activity context */
    private static Context sContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(sContext == null) {
            sContext = ActionBarActivity.this;
        }
    }

    public static Context getActivityContext() {
        return sContext;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu was invoked");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        menu.findItem(R.id.done).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Log.i(TAG, "onOptionsItemSelected was invoked");
        switch (menuItem.getItemId()) {
            case R.id.activity_main:
                Utils.dbBackup(this);
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.activity_sociability:
                startActivity(new Intent(this, SociabilityActivity.class));
                finish();
                break;
            case R.id.activity_mobility:
                startActivity(new Intent(this, MobilityActivity.class));
                finish();
                break;
            case R.id.activity_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                break;
            case R.id.turn_off:
                turnOff();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    /**
     * This method is used to customize the action bar
     * @param title action bar title
     */
    protected void setActionBarTitle(String title) {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
    }

    /**
     * This method is used to turn off the application
     */
    void turnOff() {
        Log.i(TAG, "turnOff was invoked");
        stopService(new Intent(this, NSenseService.class));
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
        finishAffinity();
    }

}
