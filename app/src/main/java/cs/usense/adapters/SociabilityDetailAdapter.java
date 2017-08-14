/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
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
import android.widget.TextView;

import java.util.ArrayList;

import cs.usense.R;
import cs.usense.models.SociabilityDetailItem;

import static cs.usense.models.SociabilityDetailItem.STARS;


/**
 * This class provides adapter details to the NSenseHistoryActivity
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public class SociabilityDetailAdapter extends ArrayAdapter<SociabilityDetailItem> {

	/** This variable is used to debug SociabilityDetailAdapter class */
	private static final String TAG = "SociabilityDAdapter";

	/**
	 * Constructor of AlertInterestsAdapter class
	 * @param context application context
	 * @param resource layout on load data
	 * @param data data to load
	 */
	public SociabilityDetailAdapter(Context context, int resource, ArrayList<SociabilityDetailItem> data) {
		super(context, resource, data);
	}
	
	@NonNull
	@Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		Log.i(TAG, getItem(position).toString());
		LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
		convertView = inflater.inflate(R.layout.item_stars_detail, parent, false);
		/* Set the device name */
		TextView deviceName = (TextView)convertView.findViewById(R.id.txtTitle);
		deviceName.setText(getItem(position).getDeviceName());
		getItem(position).fillStars(convertView, STARS);
        return convertView;
    }

}
