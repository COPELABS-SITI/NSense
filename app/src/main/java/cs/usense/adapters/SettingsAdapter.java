/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/7/28.
 * Class is part of the NSense application.
 */

package cs.usense.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cs.usense.R;
import cs.usense.models.SettingsItem;


/**
 * This class is used to load data on ListView
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public class SettingsAdapter extends ArrayAdapter<SettingsItem> {

    /** This variable is used to debug SettingsAdapter class */
    private static final String TAG = "SettingsAdapter";

    /**
     * Constructor of SettingsAdapter class
     * @param context application context
     * @param resource layout on load data
     * @param data data to load
     */
    public SettingsAdapter(Context context, int resource, ArrayList<SettingsItem> data) {
        super(context, resource, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Log.i(TAG, getItem(position).toString());
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        convertView = inflater.inflate(R.layout.item_image_and_title, parent, false);
        TextView settingsTitle = (TextView) convertView.findViewById(R.id.title_row);
        ImageView settingsIcon = (ImageView) convertView.findViewById(R.id.icon_row);
        settingsTitle.setText(getItem(position).getSettingsTitle());
        settingsIcon.setImageResource(getItem(position).getSettingsIcon());
        return convertView;
    }

}
