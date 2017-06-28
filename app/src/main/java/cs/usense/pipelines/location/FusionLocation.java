/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class provides users location.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.pipelines.location;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import cs.usense.activities.ActionBarActivity;
import cs.usense.db.NSenseDataSource;
import cs.usense.services.NSenseService;
import cs.usense.utilities.Utils;

class FusionLocation implements LocationListener, ConnectionCallbacks,
        OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    /** This variable is used to debug LocationPipeline class */
    private static final String TAG = "LocationPipeline";

    /** Time between updates, 40 * 1000 = 40 seconds */
    private static final int TIME_BETWEEN_UPDATES = 50 * 1000;

    /** This variable is used set Fusion Location API */
    private GoogleApiClient mGoogleApiClient;

    /** This variable is used to fetch locations */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /** This variable is used to callback NSenseService */
    private NSenseService mCallback;

    /** This variable is used to access functionality of NSense Data base */
    private NSenseDataSource mDataSource;


    /**
     * LocationPipeline constructor
     * @param callback NSenseService reference
     */
    FusionLocation(NSenseService callback, NSenseDataSource dataSource) {
        mCallback = callback;
        mDataSource = dataSource;
        start();
        checkLocationSettings();
    }

    /**
     * Starts the pipeline
     */
    private void start() {
        //addEventListener(this);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        mGoogleApiClient.connect();
    }

    /**
     * Starts fusion location API
     */
    private synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(mCallback.getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * This method creates location requests
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(TIME_BETWEEN_UPDATES);

        // Sets the location accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    /**
     * This method starts location updates
     */
    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * This method is called when user's location changed
     * @param location user's location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.getLatitude() + " " + location.getLongitude());
        Utils.appendLogs(TAG, location.getLatitude() + " " + location.getLongitude());
        mDataSource.insertPlace(location);
        mCallback.notifyLocation(location);
    }

    /**
     * This pipeline is called when connect with success
     * @param bundle bundle received
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
    }

    /**
     * This method is used when this pipeline is suspended
     * @param i reason
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * This method is called when this pipeline has an error
     * @param connectionResult error object
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * This method stops this pipeline
     */
    public void close() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                    status.startResolutionForResult((Activity) ActionBarActivity.getActivityContext(), 0x1);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                break;
        }
    }
}