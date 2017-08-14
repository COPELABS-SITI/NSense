/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */


package cs.usense.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import cs.usense.R;
import cs.usense.utilities.InterestsUtils;

/**
 * This class instantiates an activity to show a splash screen
 * when application starts.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public class SplashActivity extends Activity implements Runnable {

    /** This variable is used to debug SplashActivity class */
    private static final String TAG = "SplashActivity";

    /** This variable is used to define how many time the splash screen appears */
    private static final int SPLASH_TIMER = 2 * 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.i(TAG, "onCreate");
        setup();
    }

    /**
     * This method initialize some features on application that needs a context
     */
    private void setup() {
        InterestsUtils.setup(this);
        new Handler().postDelayed(this, SPLASH_TIMER);
    }

    @Override
    public void run() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}