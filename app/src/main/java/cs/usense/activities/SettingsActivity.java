/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class instantiates an activity to show
 * application settings menu.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cs.usense.R;

public class SettingsActivity extends ActionBarActivity implements OnItemClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setup();
    }

    /**
     * This method initialize everything that this class needs
     */
    private void setup() {
        setActionBarTitle(getString(R.string.Settings));
        ListView mSettingsOptions = (ListView) findViewById(R.id.settings_list);
        String[] optionsTitles = getResources().getStringArray(R.array.settings_titles);
        TypedArray optionImages = getResources().obtainTypedArray(R.array.settings_icons);

        List<HashMap<String, String>> listOfHashMaps = new ArrayList<>();

        for(int i = 0; i < optionsTitles.length; i++) {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("settings_icon", Integer.toString(optionImages.getResourceId(i, 0)));
            hm.put("settings_title", optionsTitles[i]);
            listOfHashMaps.add(hm);
        }

        optionImages.recycle();
        String[] from = {"settings_icon", "settings_title"};
        int[] to = {R.id.icon_row, R.id.title_row};

        mSettingsOptions.setAdapter(new SimpleAdapter(this, listOfHashMaps, R.layout.item_image_and_title, from, to));
        mSettingsOptions.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0) {
            startActivity(new Intent(this, CategoriesActivity.class));
        } else if (position == 1) {
            startActivity(new Intent(this, AlertsActivity.class));
            finish();
        } else if (position == 2) {
            startActivity(new Intent(this, ReportsActivity.class));
            finish();
        } else if (position == 3) {
            startActivity(new Intent(this, AboutActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
