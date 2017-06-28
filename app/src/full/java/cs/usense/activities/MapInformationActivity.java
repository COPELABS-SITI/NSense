/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class instantiates an activity to a description of
 * the circles drawn on the map.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import cs.usense.R;

public class MapInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_information);
        ImageView topBarImage = (ImageView) findViewById(R.id.top_bar_image);
        topBarImage.setColorFilter(getResources().getColor(R.color.white));
    }
}
