/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class is used to load data on ListView
 * @author Miguel Tavares (COPELABS/ULHT)
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
import android.widget.TextView;

import java.util.ArrayList;

import cs.usense.R;
import cs.usense.models.AlertInterestItem;


public class AlertInterestsAdapter extends ArrayAdapter<AlertInterestItem> {

    /** This variable is used to debug AlertInterestsAdapter class */
    private static final String TAG = "AlertInterestsAdapter";

    /**
     * Constructor of AlertInterestsAdapter class
     * @param context application context
     * @param resource layout on load data
     * @param data data to load
     */
    public AlertInterestsAdapter(Context context, int resource, ArrayList<AlertInterestItem> data) {
        super(context, resource, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Log.i(TAG, getItem(position).toString());
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        convertView = inflater.inflate(R.layout.item_alert_interests, parent, false);
        TextView deviceName = (TextView) convertView.findViewById(R.id.alert_device_name);
        TextView interests = (TextView) convertView.findViewById(R.id.alert_interests);
        deviceName.setText(getItem(position).getDeviceName());
        interests.setText(getItem(position).getInterests());
        return convertView;
    }

}
