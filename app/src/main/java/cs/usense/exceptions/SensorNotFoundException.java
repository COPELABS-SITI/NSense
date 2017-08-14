/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/07/18.
 * Class is part of the NSense application.
 */

package cs.usense.exceptions;

import android.content.Context;
import android.content.Intent;

import cs.usense.R;
import cs.usense.activities.SensorNotFoundActivity;

/**
 * This exception is triggered when the device is missing any feature needed
 * by the application like the accelerometer or bluetooth.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class SensorNotFoundException extends Exception {

    /**
     * This method is the constructor of SensorNotFoundException class
     * @param message error message
     */
    public SensorNotFoundException(String message) {
        super(message);
    }

    public void showDialogError(Context context) {
        context.startActivity(new Intent(context, SensorNotFoundActivity.class)
                .putExtra(SensorNotFoundActivity.EXTRA_TITLE, context.getString(R.string.sensor_not_found_title))
                .putExtra(SensorNotFoundActivity.EXTRA_TITLE, getMessage())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );

    }

}
