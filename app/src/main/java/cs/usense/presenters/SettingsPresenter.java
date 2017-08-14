/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/7/28.
 * Class is part of the NSense application.
 */

package cs.usense.presenters;

import android.content.Context;
import android.content.res.TypedArray;

import java.util.ArrayList;

import cs.usense.R;
import cs.usense.activities.AboutActivity;
import cs.usense.activities.AlertsActivity;
import cs.usense.activities.CategoriesActivity;
import cs.usense.activities.ReportsActivity;
import cs.usense.interfaces.SettingsInterfaces;
import cs.usense.models.SettingsItem;


/**
 * This class is used to implement MVP design pattern.
 * Receives requests from the view and treat them.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class SettingsPresenter implements SettingsInterfaces.Presenter {

    /** This object is used to establish communication with the view */
    private SettingsInterfaces.View mView;

    /**
     * This method is the SettingsPresenter constructor
     * @param view view interface to communicate with the view
     */
    public SettingsPresenter(SettingsInterfaces.View view) {
        mView = view;
    }

    @Override
    public void onItemClick(int option) {
        if(option == 0) {
            mView.startSelectedActivity(CategoriesActivity.class, false);
        } else if(option == 1) {
            mView.startSelectedActivity(AlertsActivity.class, true);
        } else if(option == 2) {
            mView.startSelectedActivity(ReportsActivity.class, true);
        } else if(option == 3) {
            mView.startSelectedActivity(AboutActivity.class, true);
        }
    }

    @Override
    public void onResume(Context context) {
        ArrayList<SettingsItem> items = new ArrayList<>();
        String[] optionsTitles = context.getResources().getStringArray(R.array.settings_titles);
        TypedArray optionImages = context.getResources().obtainTypedArray(R.array.settings_icons);
        for(int i = 0; i < optionsTitles.length; i++)
            items.add(new SettingsItem(optionsTitles[i], optionImages.getResourceId(i, 0)));
        mView.onReceiveSettingsData(items);
    }

    @Override
    public void onDestroy() {
        mView = null;
    }
}
