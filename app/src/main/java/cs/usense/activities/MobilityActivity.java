package cs.usense.activities;

import android.content.Intent;
import android.os.Bundle;

import cs.usense.R;

public class MobilityActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobility);
        setActionBarTitle(getString(R.string.Mobility));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
