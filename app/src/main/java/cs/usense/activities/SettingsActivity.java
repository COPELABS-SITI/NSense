/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cs.usense.R;
import cs.usense.adapters.SettingsAdapter;
import cs.usense.interfaces.SettingsInterfaces;
import cs.usense.models.SettingsItem;
import cs.usense.presenters.SettingsPresenter;


/**
 * This class instantiates an activity to show the application settings menu.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public class SettingsActivity extends ActionBarActivity implements SettingsInterfaces.View, OnItemClickListener {

    /** This ListView stores the options that are available on settings menu */
    @BindView(R.id.settings_list) ListView settingsList;

    /** This object is used to establish communication with the presenter */
    private SettingsInterfaces.Presenter mPresenter;

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
        ButterKnife.bind(this);
        mPresenter = new SettingsPresenter(this);
        settingsList.setOnItemClickListener(this);
        setActionBarTitle(getString(R.string.Settings));
    }

    @Override
    public void onResume() {
        mPresenter.onResume(this);
        super.onResume();
    }

    /**
     * This method receives the data which will populate the ListView
     * @param data data to populate the ListView
     */
    @Override
    public void onReceiveSettingsData(ArrayList<SettingsItem> data) {
        settingsList.setAdapter(new SettingsAdapter(this, R.layout.item_image_and_title, data));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.onItemClick(position);
    }

    /**
     * This method is called when the user selects one option of the settings
     * @param activity activity to instantiate
     * @param finishThisActivity boolean that decides if the current activity will be destroyed or not
     */
    @Override
    public void startSelectedActivity(Class activity, boolean finishThisActivity) {
        startActivity(new Intent(this, activity));
        if(finishThisActivity) finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

}
