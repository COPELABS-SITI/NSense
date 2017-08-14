/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/07/12.
 * Class is part of the NSense application. It provides support for location pipeline.
 */

package cs.usense.pipelines.location;


import android.util.Log;


/**
 * This class contains math models to compute distance
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
abstract class DistanceModels {

    /** This variable is used to debug DistanceModels */
    private static final String TAG = "DistanceModels";

    /** Free space path loss variables */
    private static final int FSPL_FREQ = 189;
    private static final int FSPL_LIGHT = -148;
    private static final int FREE_SPACE_PATH_LOSS_CONSTANT_FOR_BLE = FSPL_FREQ + FSPL_LIGHT;

    /**
     * Convert RSSI to distance using the ITU model.
     * See <a https://en.wikipedia.org/wiki/ITU_model_for_indoor_attenuation</a>
     *
     * @param rssi -  Received Signal Strength Indication (RSSI) in dBm
     * @param freqInMHz - The frequency in MHz of the channel over which the client is communicating with the access point.
     * @return distance in meters.
     */
    static double ituModel(double rssi, double freqInMHz) {
        double distance = Math.pow(10, ((Math.abs(rssi) - 20 * Math.log10(freqInMHz) + 25))/22);
        Log.i(TAG, "Distance computed with ituModel " + distance + "m");
        return distance > 100 ? 100 : distance;
    }

    /**
     * Convert RSSI to distance using the Free Space Path Loss model.
     * See <a href="http://en.wikipedia.org/wiki/Free-space_path_loss">Free-space_path_loss</a>
     *
     * @param rssi Received Signal Strength Indication (RSSI) in dBm
     * @param txPowerAtSource the calibrated power of the transmitter (dBm) at 0 meter
     * @return the distance at which that rssi value would occur in meters
     */
    static double freeSpacePathLossModel(int rssi, int txPowerAtSource) {
        double pathLoss = txPowerAtSource - rssi;
        double distance = Math.pow(10, (pathLoss - FREE_SPACE_PATH_LOSS_CONSTANT_FOR_BLE) / 20.0) * 2;
        Log.i(TAG, "Distance computed with freeSpacePathLossModel " + distance + "m");
        return distance > 100 ? 100 : distance;
    }

    /**
     * Convert RSSI to distance using the Log Distance Path Loss model.
     * See <a href="https://en.wikipedia.org/wiki/Log-distance_path_loss_model">Log-Distance-Path-Loss</a>
     *
     * @param rssi Received Signal Strength Indication (RSSI) in dBm
     * @param rssi1Meter Received Signal Strength Indication (RSSI) in dBm at 1 meter
     * @param txPowerAtSource the calibrated power of the transmitter (dBm) at 0 meter
     * @return the distance at which that rssi value would occur in meters
     */
    static double logDistancePathLossModel(int rssi, int rssi1Meter, int txPowerAtSource) {
        double pathLoss = txPowerAtSource - rssi;
        double pathLoss1m = txPowerAtSource - rssi1Meter;
        double result = pathLoss - pathLoss1m;
        double distance = Math.pow(10, result / 25);
        Log.i(TAG, "Distance computed with logDistancePathLossModel " + distance + "m");
        return distance > 100 ? 100 : distance;
    }

}
