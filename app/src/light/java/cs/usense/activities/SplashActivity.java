/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class instantiates an activity to show a splash
 * screen when application starts.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cs.usense.R;
import cs.usense.utilities.InterestsUtils;

public class SplashActivity extends Activity {

    /** This variable is used to debug SplashActivity class */
    private static final String TAG = "SplashActivity";

    /** This variable is used to define how many time the splash screen appears */
    private static final int SPLASH_TIMER = 2 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.i(TAG, "onCreate");
        showSpashScreen();
    }

    /**
     * This method is used to show the splash screen.
     */
    private void showSpashScreen() {
        new Thread(){
            public void run(){
                try {
                    Log.i(TAG, "before sleep");
                    setup();
                    sleep(SPLASH_TIMER);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Log.i(TAG, "after sleep");
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }.start();
    }

    /**
     * This method initialize some features on application that needs a context
     */
    private void setup() {
        InterestsUtils.setup(this);
    }

}
