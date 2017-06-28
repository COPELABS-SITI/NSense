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

import cs.usense.R;


/**
 * This class provides adapter details to the NSenseHistoryActivity
 */
public class ReportAdapter extends ArrayAdapter<String> {

    /** This variable is used to debug SociabilityDetailAdapter class */
    private static final String TAG = "SociabilityDAdapter";


    public ReportAdapter(Context context, int resource, String[] data) {
        super(context, resource, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Log.i(TAG, getItem(position));
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        convertView = inflater.inflate(R.layout.item_image_and_title, parent, false);
        /** Set report name and it's icon */
        TextView reportName = (TextView) convertView.findViewById(R.id.title_row);
        ImageView reportIcon = (ImageView) convertView.findViewById(R.id.icon_row);
        reportName.setText(getItem(position));
        reportIcon.setImageResource(R.drawable.ic_report);
        return convertView;
    }

}