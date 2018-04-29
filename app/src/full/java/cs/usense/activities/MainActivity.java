/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/25.
 * Class is part of the NSense application.
 */


package cs.usense.activities;

import android.content.ComponentName;

import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

import cs.usense.R;
import cs.usense.inferenceModule.SocialDetail;
import cs.usense.map.MapActivityListener;
import cs.usense.map.MapManager;
import cs.usense.preferences.InterestsPreferences;
import cs.usense.services.NSenseService;

/**
 * This class provides the layout of NSense application and initialize the NSense Service.
 * This class controls the User Interface and update it based on NSenseService results.
 * @author Saeik Firdose (COPELABS/ULHT),
 * @author Luis Lopes (COPELABS/ULHT),
 * @author Waldir Moreira (COPELABS/ULHT),
 * @author Reddy Pallavali (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2017
 */
public class MainActivity extends ActionBarActivity implements MapActivityListener,
        OnMapReadyCallback, ServiceConnection, AlarmReceiverInterface {

    /** This TAG is used to debug MainActivity class */
    private static final String TAG = "MainActivity";

    /** This flag goes up when the application starts */
    private static boolean mApplicationStarted = true;

    /** This class provides the NSense Service to bound service */
    private NSenseService mBoundService;

    /** This variable is request to bound service */
    private boolean mIsServiceBound;

    /** This variable is used to draw the items on the map */
    private MapManager mMapManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate was invoked");


        AlarmInterfaceManager.registerListener(this);
        cs.usense.pipelines.mobility.utils.Utils.setAlarm(this,11,0);

        setup();
    }

    /**
     * This method initializes all needed objects.
     */
    private void setup() {
        Log.i(TAG, "setup was invoked");
        /* These objects are used to show % of Social Interaction and Propinquity */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

         /* Checks if the service is already running */
        if (NSenseService.isMyServiceRunning(NSenseService.class, this)) {
            Log.i(TAG, "Service is running.");
            RelativeLayout one = (RelativeLayout) findViewById(R.id.waiting_for_location);
            one.setVisibility(View.INVISIBLE);
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

    public void onClickYellowZoom(View view) {
        mMapManager.yellowZoom();
    }

    public void onClickBlueZoom(View view) {
        mMapManager.blueZoom();
    }

    public void onClickMapInformation(View view) {
        startActivity(new Intent(this, MapInformationActivity.class));
    }

    /**
     * This method creates the service connection, which can be used to bind the NSense service
     */
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        Log.i(TAG, "onServiceConnected was invoked");
        updateActivity();
        mIsServiceBound = true;
        mBoundService = ((NSenseService.LocalBinder) service).getService();
        mBoundService.setOnStateChangeListener(this);
    }

    @Override
    public void onSociabilityChange(ArrayList<SocialDetail> socialInformation) {
        Log.i(TAG, socialInformation.toString());
        mMapManager.refreshMapInformation(socialInformation);
    }

    @Override
    public void onLocationChange(Location location) {
        Log.i(TAG, location.getLatitude() + " " + location.getLongitude());
        mMapManager.refreshLocation(location);
        RelativeLayout one = (RelativeLayout) findViewById(R.id.waiting_for_location);
        one.setVisibility(View.INVISIBLE);
        mApplicationStarted = false;
    }

    /**
     * This method update the activity after onDestroy
     */
    private void updateActivity() {
        Log.i(TAG, "updateActivity was invoked");
        if (!mApplicationStarted) {
            Log.i(TAG, "Map updated");
            mMapManager.refreshMap();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady");
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mMapManager = new MapManager(this, googleMap);
        mMapManager.refreshMap();
    }

    /**
     * This method is called when the service goes to the disconnected state.
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, "onServiceDisconnected was invoked");
        mBoundService = null;
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
        mBoundService = null;
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

    @Override
    public void onAlarme() {
        AlarmInterfaceManager.unRegisterListener(this);
        //startActivity(new Intent(MainActivity.this, MobilityActivity.class));
        //finish();
    }
}