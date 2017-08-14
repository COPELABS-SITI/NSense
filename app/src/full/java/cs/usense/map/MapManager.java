/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This responsible to manage the map. Draw circles, markers, etc.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.map;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;

import cs.usense.R;
import cs.usense.activities.DeviceCategoriesActivity;
import cs.usense.inferenceModule.SocialDetail;
import cs.usense.utilities.Utils;

public class MapManager implements GoogleMap.OnInfoWindowClickListener {

    /** This variable is used to debug MapManager class */
    private static final String TAG = "MapManager";

    /** This variable is used to test if we are far away from circles */
    private static final int FAR_AWAY_ZOOM = 15;

    /** This variable defines yellow circle zoom */
    private static final int BLUE_ZOOM = 18;

    /** This variable defines blue circle zoom */
    private static final int YELLOW_ZOOM = 20;

    /** This variable is used to scale real values to the map values */
    private static final double SCALE_FACTOR = 5.5;

    /** This variable is used to define bluetooth sensing range */
    private static final double BLUETOOTH_DISTANCE = 10.0;

    /** This variable is used to define line width of the circles */
    private static final int LINE_WIDTH = 8;

    /** This variable is used to define circles radius */
    private static final double RANGES[] = {20, 7.5, 3.6, 1.2, 0.45};

    private static final int WIFI_RANGE_LEVELS[] = {10, 15, 20, 30, 50, 70, 100};

    private static final double WIFI_RANGE_SCALE = 5.8;

    /** This variable is used to store my location */
    private static Location sLocation;

    /** This variable is used to store social information */
    private static ArrayList<SocialDetail> sSocialInformation;

    /** This variable is used to store marker locations */
    private ArrayList<Location> mMarkerLocations = new ArrayList<>();

    /** This variable is used to store the application context */
    private Context mContext;

    /** This variable is used to manage the map */
    private GoogleMap mGoogleMap;


    /**
     * Constructor of MapManager class
     * @param context application context
     * @param googleMap map reference
     */
    public MapManager(Context context, GoogleMap googleMap) {
        mContext = context;
        mGoogleMap = googleMap;
        mGoogleMap.setOnInfoWindowClickListener(this);
    }

    /**
     * This method is used to refresh map social information
     * @param socialDetails social information
     */
    public void refreshMapInformation(ArrayList<SocialDetail> socialDetails) {
        sSocialInformation = new ArrayList<>(socialDetails);
        refreshMap();
    }

    /**
     * This method is used to refresh my location on map
     * @param location my location
     */
    public void refreshLocation(Location location) {
        sLocation = location;
        refreshMap();
    }

    /**
     * This method is responsible to refresh map information
     */
    public void refreshMap() {
        if (sLocation != null) {
            mGoogleMap.clear();
            mMarkerLocations.clear();
            if (sSocialInformation != null) {
                for (SocialDetail socialDetail : sSocialInformation) {
                    putMarker(sLocation, socialDetail);
                }
            }
            drawCirclesProximityCircles();
            setYourLocation();
        }
    }

    /**
     * This method is used to draw proximity circles on map
     */
    private void drawCirclesProximityCircles() {
        int[] circleColors = mContext.getResources().getIntArray(R.array.circle_colors);
        for(int i = 0; i < RANGES.length && i < circleColors.length; i++) {
            mGoogleMap.addCircle(new CircleOptions()
                    .center(new LatLng(sLocation.getLatitude(), sLocation.getLongitude()))
                    .radius(RANGES[i] * SCALE_FACTOR)
                    .strokeWidth(LINE_WIDTH)
                    .fillColor(circleColors[i])
                    .strokeColor(circleColors[i]));
        }
    }

    /**
     * This method is used to scale map distances
     * @param distance distance to scale
     * @return scaled distance
     */
    private double scaleDistance(double distance) {
        if(distance < 0) {
            distance = wifiRangeScale(BLUETOOTH_DISTANCE);
        } else if (distance < 0.6) {
            distance = 1.0;
        } else {
            if(RANGES[3] > distance)
                distance *= SCALE_FACTOR;
            else
                distance = wifiRangeScale(distance);
        }
        return distance;
    }

    private double wifiRangeScale(double distance) {
        double scaledDistance = 0;
        for(int i = 0; i < WIFI_RANGE_LEVELS.length; i++) {
            if(WIFI_RANGE_LEVELS[i] >= distance) {
                scaledDistance = RANGES[1] * SCALE_FACTOR + (i + 1) * WIFI_RANGE_SCALE;
                break;
            }
        }
        return scaledDistance;
    }

    /**
     * This method is used to set my location on map
     */
    private void setYourLocation() {
        LatLng latLng = new LatLng(sLocation.getLatitude(), sLocation.getLongitude());

        float currentZoom = mGoogleMap.getCameraPosition().zoom;
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(mContext.getString(R.string.you_are_here))
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.marker_old_man))));

        if(currentZoom < FAR_AWAY_ZOOM) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, BLUE_ZOOM);
            mGoogleMap.animateCamera(cameraUpdate);
        } else {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, currentZoom);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }

    /**
     * This method is responsible to put markers on map
     * @param location marker location
     * @param socialDetail marker social information
     */
    private void putMarker(Location location, SocialDetail socialDetail) {
        mGoogleMap.addMarker(new MarkerOptions()
                .position(generateLocationWithinRadius(location, scaleDistance(socialDetail.getDistance())))
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.marker_device)))
                .title(socialDetail.getDeviceName()));
    }

    /**
     * This method converts a drawable to a bitmap
     * @param drawableRes drawable
     * @return bitmap
     */
    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = mContext.getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * This method is used to generate random locations with a radius from my location
     * @param myCurrentLocation my location on map
     * @param radius radius size
     * @return validated distance
     */
    private LatLng generateLocationWithinRadius(Location myCurrentLocation, double radius) {
        Location location;
        while(true) {
            location = getLocationInLatLngRad(radius, myCurrentLocation);
            double distance = myCurrentLocation.distanceTo(location);
            if(distance >= radius * 0.98 && distance < radius * 1.02 && checkMarkersAround(location, radius)) {
                mMarkerLocations.add(location);
                break;
            }
        }
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    /**
     * This method avoid markers overlapping
     * @param location new marker location
     * @param radius size of radius to avoid overlapping
     * @return true if this location not overlaps a marker
     */
    private boolean checkMarkersAround(Location location, double radius) {
        boolean result = true;
        if (!mMarkerLocations.isEmpty()) {
            for(Location cacheLocation : mMarkerLocations) {
                if(cacheLocation.distanceTo(location) < avoidOverlapping(radius)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * This method is used to show device interests on activity
     * @param marker selected marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        String deviceName = marker.getTitle();
        Log.i(TAG, "Looking for " + deviceName + " marker");
        for(SocialDetail socialInformation : sSocialInformation) {
            if(deviceName.equalsIgnoreCase(socialInformation.getDeviceName())) {
                Log.i(TAG, "Marker with " + deviceName + " found");
                mContext.startActivity(new Intent(mContext, DeviceCategoriesActivity.class)
                        .putExtra("deviceInfo", socialInformation));
            }
        }
    }

    /**
     * This method is responsible to avoid markers overlapping
     * @param radius radius size
     * @return radius size to avoid overlapping
     */
    private double avoidOverlapping(double radius) {
        return radius < RANGES[3] * SCALE_FACTOR ? 0.5 : 5.0;
    }

    /**
     * This method is used to set zoom on yellow circle
     */
    public void yellowZoom() {
        zoom(YELLOW_ZOOM);
    }

    /**
     * This method is used to set zoom on blue circle
     */
    public void blueZoom() {
        zoom(BLUE_ZOOM);
    }

    /**
     * This method is used to zoom a location
     * @param zoomLevel zoom level
     */
    private void zoom(int zoomLevel) {
        if(sLocation != null) {
            LatLng latLng = new LatLng(sLocation.getLatitude(), sLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
            mGoogleMap.animateCamera(cameraUpdate, 800, null);
        }
    }

    /**
     * This method generates random locations around a location in a radius size
     * @param radiusInMeters radius size
     * @param currentLocation location
     * @return location
     */
    private static Location getLocationInLatLngRad(double radiusInMeters, Location currentLocation) {
        double x0 = currentLocation.getLatitude();
        double y0 = currentLocation.getLongitude();

        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radiusInMeters / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        double new_x = x / Math.cos(y0);
        double new_y = y / Math.cos(x0);
        double foundLatitude;
        double foundLongitude;
        boolean shouldAddOrSubtractLat = random.nextBoolean();
        boolean shouldAddOrSubtractLon = random.nextBoolean();
        if (shouldAddOrSubtractLat) {
            foundLatitude = new_x + x0;
        } else {
            foundLatitude = x0 - new_x;
        }
        if (shouldAddOrSubtractLon) {
            foundLongitude = new_y + y0;
        } else {
            foundLongitude = y0 - new_y;
        }
        Location copy = new Location(currentLocation);
        copy.setLatitude(foundLatitude);
        copy.setLongitude(foundLongitude);
        return copy;
    }

}