/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/07/18.
 * Class is part of the NSense application.
 */

package cs.usense.activities;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cs.usense.R;


/**
 * This activity is triggered when there is any feature missing on the device,
 * like accelerometer or bluetooth.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class SensorNotFoundActivity extends ActionBarActivity {

    /** This variable is used to fetch the activity title from the Intent */
    public static final String EXTRA_TITLE = "title";

    /** This variable is used to fetch the activity message from the Intent */
    public static final String EXTRA_MESSAGE = "message";

    /** This TextView holds the title of the dialog when the exception is triggered */
    @BindView(R.id.warning_title) TextView warningTitle;

    /** This TextView holds the error message of the dialog when the exception is triggered */
    @BindView(R.id.warning_message) TextView warningMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_not_found);
        ButterKnife.bind(this);
        warningTitle.setText(getIntent().getExtras().getString(EXTRA_TITLE));
        warningMessage.setText(getIntent().getExtras().getString(EXTRA_MESSAGE));
    }

    @Override
    public void onBackPressed() {
        turnOff();
    }
}
