/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/04/04.
 * Class is part of the NSense application.
 */

package cs.usense.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cs.usense.R;
import cs.usense.utilities.InterestsUtils;
import cs.usense.views.InformationView;

import static cs.usense.activities.RatingsActivity.CATEGORIES_PER_ACTIVITY;
import static cs.usense.activities.RatingsActivity.STARS_PER_CATEGORY;

/**
 * This class instantiates an activity that allows the user to watch the category
 * ratings of others users.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2017
 */
public class DeviceRatingsActivity extends ActionBarActivity {

    /** This variable is used to debug RatingsActivity class */
    private static final String TAG = "DeviceSubCatActivity";

    /** This variable represents how many categories we can see on subcategories activities */
    private static final int NUMBER_OF_CATEGORIES_PER_ACTIVITY = 4;

    /** This variable represents how many stars we can set */
    private static final int NUMBER_OF_STARS_PER_CATEGORY = 3;

    /** This variable represents the view more button */
    @BindView(R.id.view_more) LinearLayout viewMore;

    /** This ArrayList stores the user's interests */
    private ArrayList<String> mCategories = new ArrayList<>();

    /** This ArrayList stores the user's interests */
    private ArrayList<String> mRatings = new ArrayList<>();

    /** This variable stores the device name to show on the activity */
    private String mDeviceName;

    /** This variable stores the list index */
    private int mIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_ratings);
        setup();
    }

    /**
     * This method initialize everything that this activity needs
     */
    private void setup() {
        ButterKnife.bind(this);
        loadDataFromIntent();
        loadTopBarAndViewMoreButton();
        loadTitles();
        loadMatrix();
        loadRatings();
    }

    /**
     * This method is triggered when the user click on view more button.
     * Sends the user to the next ratings activity to continue watching
     * the user's ratings
     */
    @OnClick(R.id.view_more)
    public void onClickViewMore() {
        startActivity(new Intent(this, DeviceRatingsActivity.class)
                .putExtra("index", mIndex + NUMBER_OF_CATEGORIES_PER_ACTIVITY)
                .putStringArrayListExtra("categories", mCategories)
                .putStringArrayListExtra("ratings", mRatings)
                .putExtra("deviceName", mDeviceName)
        );
        finish();
    }

    /**
     * This method fetch the data to be loaded from the intent
     */
    private void loadDataFromIntent() {
        mCategories = getIntent().getStringArrayListExtra("categories");
        mRatings = getIntent().getStringArrayListExtra("ratings");
        mDeviceName = getIntent().getStringExtra("deviceName");
        mIndex = getIntent().getIntExtra("index", 0);
    }

    /**
     * This method initialize the top bar and also the view more button
     */
    private void loadTopBarAndViewMoreButton() {
        ImageView topBarImage = (ImageView) findViewById(R.id.top_bar_image);
        topBarImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        TextView interestsHeader = (TextView) findViewById(R.id.activity_title);
        interestsHeader.setText(getString(R.string.user_interests, mDeviceName));
        if(mIndex + NUMBER_OF_CATEGORIES_PER_ACTIVITY > mRatings.size())
            viewMore.setVisibility(View.GONE);
    }

    /**
     * This method loads the ratings titles that is the categories.
     */
    private void loadTitles() {
        for (int i = 0, k = mIndex; k < mCategories.size() && i < NUMBER_OF_CATEGORIES_PER_ACTIVITY; i++, k++) {
            TextView title = (TextView) findViewById(getResources().getIdentifier("main_title_" + i, "id", getPackageName()));
            title.setText(InterestsUtils.getInterestAsString(mCategories.get(k)));
            Log.i(TAG, "Loading title " + mCategories.get(k));
        }
    }

    /**
     * This method loads the ratings on the matrix
     */
    private void loadMatrix() {
        for(int i = 0; i + mIndex < mRatings.size() && i < NUMBER_OF_CATEGORIES_PER_ACTIVITY; i++) {
            ArrayList<String> ratings = InterestsUtils.getRatingsAsString(this, mRatings.get(i + mIndex));
            for(int j = 0, k = i; j < NUMBER_OF_STARS_PER_CATEGORY; j++, k += NUMBER_OF_CATEGORIES_PER_ACTIVITY) {
                InformationView infoView = (InformationView)  findViewById(getResources().getIdentifier("info_" + k, "id", getPackageName()));
                infoView.setImageAndTitle(R.drawable.ic_star_empty, R.color.black, ratings.get(j));
                Log.i(TAG, "Loading rating " + ratings.get(j));
            }
        }
    }

    /**
     * This method loads on the matrix the user's ratings
     */
    private void loadRatings() {
        for(String interest : mRatings) {
            for(int i = 0; i < CATEGORIES_PER_ACTIVITY; i++) {
                TextView category = (TextView) findViewById(getResources().getIdentifier("main_title_" + i, "id", getPackageName()));
                if(category.getText().toString().equals(InterestsUtils.getCategoryOfInterestAsString(interest))) {
                   String rating = InterestsUtils.getRatingOfInterestValue(interest);
                   for(int j = 0, k = i; j < STARS_PER_CATEGORY; k += CATEGORIES_PER_ACTIVITY, j++) {
                        InformationView infoView = (InformationView) findViewById(getResources().getIdentifier("info_" + k, "id", getPackageName()));
                        if(infoView.getTitle().equals(InterestsUtils.getRatingAsString(rating))) {
                            Log.i(TAG, "Switch information view " + category.getText() + " rating " + infoView.getTitle());
                            infoView.switchStatus();
                            break;
                        }
                    }
                }
            }
        }
    }

}
