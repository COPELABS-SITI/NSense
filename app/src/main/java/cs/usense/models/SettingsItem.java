/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/7/28.
 * Class is part of the NSense application.
 */

package cs.usense.models;

/**
 * This class is a model and is used to store device name and interests in common
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class SettingsItem {

    /** This variable stores the title of the settings */
    private String mSettingsTitle;

    /** This variable stores the resource id of the settings icons */
    private int mSettingsIcon;

    public SettingsItem(String settingsTitle, int settingsIcon) {
        mSettingsTitle = settingsTitle;
        mSettingsIcon = settingsIcon;
    }

    public String getSettingsTitle() {
        return mSettingsTitle;
    }

    public int getSettingsIcon() {
        return mSettingsIcon;
    }

}
