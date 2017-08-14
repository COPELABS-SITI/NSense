/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cs.usense.R;
import cs.usense.interfaces.AboutInterfaces;
import cs.usense.presenters.AboutPresenter;


/**
 * This class provides some information related with the application
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public class AboutActivity extends ActionBarActivity implements AboutInterfaces.View {

    /** This variable is used to debug AboutInterfaces class */
    private static final String TAG = "AboutActivity";

    /** This variable shows the application version */
    @BindView(R.id.version) TextView applicationVersion;

    /** This object is the presenter of this activity */
    private AboutInterfaces.Presenter mPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setup();
    }

    /**
     * This method initialize everything needed in this activity
     */
    private void setup() {
        Log.i(TAG, "setup");
        ButterKnife.bind(this);
        setActionBarTitle(getString(R.string.About));
        mPresenter = new AboutPresenter(this);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        mPresenter.onResume(this);
        super.onResume();
    }

    /**
     * This method shows the about info
     */
    @Override
    public void showAboutInfo(String aboutInfo) {
        Log.i(TAG, aboutInfo);
        applicationVersion.setText(aboutInfo);
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        startActivity(new Intent(this, SettingsActivity.class));
        finish();
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

}
