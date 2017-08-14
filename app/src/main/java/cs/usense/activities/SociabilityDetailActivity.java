/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cs.usense.R;
import cs.usense.adapters.SociabilityDetailAdapter;
import cs.usense.models.SociabilityDetailItem;

/**
 * It provides support to the NSense History Activity, and provides the
 * list of devices, SocialInteraction, and Propinquity information with the layout.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
@SuppressWarnings("unchecked")
public class SociabilityDetailActivity extends AppCompatActivity {

    /** This variable is a key to fetch the date from the Intent */
    public static final String EXTRA_DATE = "date";

    /** This variable is a key to fetch the data type from the Intent */
    public static final String EXTRA_DATA = "data";

    /** This variable is a key to fetch the social data from the Intent */
    public static final String EXTRA_SOCIAL_DATA_TYPE = "social";

    /** this ImageView holds the image on the top bar of the activity */
    @BindView(R.id.top_bar_image) ImageView topBarImage;

    /** this TextView holds the title of the activity */
    @BindView(R.id.sociability_detail_title) TextView title;

    /** this ListView holds the stars to be presented */
    @BindView(R.id.stars_list) ListView starsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sociability_detail);
        setup();
    }

    /**
     * This method initialize everything needed in this activity
     */
    private void setup() {
        ButterKnife.bind(this);
        setActivityTitle();
        loadData();
    }

    /**
     * This method initialize the activity title
     */
    private void setActivityTitle() {
        String socialInformationType = (String) getIntent().getExtras().get(EXTRA_SOCIAL_DATA_TYPE);
        String date = (String) getIntent().getExtras().get(EXTRA_DATE);
        topBarImage.setColorFilter(getResources().getColor(R.color.white));
        title.setText(socialInformationType + " - " + date);
    }

    /**
     * This method load the data, users and stars
     */
    private void loadData() {
        ArrayList<SociabilityDetailItem> data = (ArrayList) getIntent().getExtras().get(EXTRA_DATA);
        starsList.setAdapter(new SociabilityDetailAdapter(this, R.layout.item_stars_detail, data));
    }

}
