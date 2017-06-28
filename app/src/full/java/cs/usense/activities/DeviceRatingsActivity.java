/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class instantiates an activity that allows user
 * set his own interests.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.activities;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cs.usense.R;
import cs.usense.utilities.InterestsUtils;

public class DeviceRatingsActivity extends ActionBarActivity {

    /** This variable is used to debug RatingsActivity class */
    private static final String TAG = "DeviceSubCatActivity";

    /** This variable represents how many categories we can see on subcategories activities */
    private static final int NUMBER_OF_CATEGORIES_PER_ACTIVITY = 4;

    /** This variable represents how many stars we can set */
    private static final int NUMBER_OF_STARS_PER_CATEGORY = 3;

    /** This ArrayList stores the user's interests */
    private ArrayList<String> mCategories = new ArrayList<>();

    /** This ArrayList stores the user's interests */
    private ArrayList<String> mRatings = new ArrayList<>();

    private int mIndex;

    private String mDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_subcategories);

        mCategories = getIntent().getStringArrayListExtra("categories");
        mRatings = getIntent().getStringArrayListExtra("ratings");
        mDeviceName = getIntent().getStringExtra("deviceName");
        mIndex = getIntent().getIntExtra("index", 0);

        loadTitles();
        loadMatrix();
        setupSubCategoriesButton();

        setup();

        /** Load the user's interests from cache to the matrix */
        loadCategories(mRatings);
    }

    /**
     * This method is called to check and place the interests
     */
    private void setup() {
        ImageView topBarImage = (ImageView) findViewById(R.id.top_bar_image);
        topBarImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        TextView interestsHeader = (TextView) findViewById(R.id.activity_title);
        interestsHeader.setText(getString(R.string.user_interests, mDeviceName));

        if(mRatings.size() > mIndex + NUMBER_OF_CATEGORIES_PER_ACTIVITY) {
            setupSubCategoriesButton();
        } else {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.view_more);
            linearLayout.setVisibility(View.GONE);
        }

    }

    private void setupSubCategoriesButton() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.view_more);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeviceRatingsActivity.this, DeviceRatingsActivity.class)
                        .putExtra("index", mIndex + NUMBER_OF_CATEGORIES_PER_ACTIVITY)
                        .putStringArrayListExtra("categories", mCategories)
                        .putStringArrayListExtra("ratings", mRatings)
                        .putExtra("deviceName", mDeviceName)
                );
                finish();
            }
        });
    }

    private void loadTitles() {
        for (int i = 0, k = mIndex; k < mCategories.size() && i < NUMBER_OF_CATEGORIES_PER_ACTIVITY; i++, k++) {
            TextView title = (TextView) findViewById(getResources().getIdentifier("main_title_" + i, "id", getPackageName()));
            title.setText(InterestsUtils.getInterestAsString(mCategories.get(k)));
        }
    }

    private void loadMatrix() {
        for(int i = 0; i + mIndex < mRatings.size() && i < NUMBER_OF_CATEGORIES_PER_ACTIVITY; i++) {
            ArrayList<String> subCategories = InterestsUtils.getRatingsAsString(this, mRatings.get(i + mIndex));
            for(int j = 0, k = i; j < NUMBER_OF_STARS_PER_CATEGORY; j++, k += NUMBER_OF_CATEGORIES_PER_ACTIVITY) {
                ((TextView) findViewById(getResources().getIdentifier("title_" + k, "id", getPackageName()))).setText(subCategories.get(j));
                ((ImageView) findViewById(getResources().getIdentifier("image_" + k, "id", getPackageName()))).setImageResource(R.drawable.ic_star_empty);
            }
        }
    }

    /**
     * This method is used to set user's interests
     * @param relativeLayout interest layout
     */
    private void enableInterest(RelativeLayout relativeLayout) {
        int count = relativeLayout.getChildCount();
        relativeLayout.setTag(R.id.interests, 1);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.black));
        for (int i = 0; i < count; i++) {
            View view = relativeLayout.getChildAt(i);
            if(view instanceof LinearLayout) {
                int countLinearL = ((LinearLayout)view).getChildCount();
                for (int j = 0; j < countLinearL; j++) {
                    View viewLinear = ((LinearLayout)view).getChildAt(j);
                    if (viewLinear instanceof ImageView) {
                        if(((ImageView) viewLinear).getDrawable() != null) {
                            ((ImageView) viewLinear).setColorFilter(Color.WHITE);
                        }
                    } else if (viewLinear instanceof TextView) {
                        ((TextView) viewLinear).setTextColor(Color.WHITE);
                    }
                }
            }
        }
        Log.i(TAG, relativeLayout.getTag().toString() + " on");
        invalidateOptionsMenu();
    }

    /**
     * This method load on matrix user's interests
     * @param interests user's interests
     */
    private void loadCategories(ArrayList<String> interests) {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.interests);
        for(String interest : interests) {
            for(int i = 0; i < NUMBER_OF_CATEGORIES_PER_ACTIVITY; i++) {
                TextView category = (TextView) findViewById(getResources().getIdentifier("main_title_" + i, "id", getPackageName()));
                if(category.getText().toString().equals(InterestsUtils.getInterestAsString((Integer.parseInt(interest) / 10) + "0"))) {
                    String rating = String.valueOf(Integer.parseInt(interest) % 10);
                    for(int j = 0, k = i; j < NUMBER_OF_STARS_PER_CATEGORY; k += NUMBER_OF_CATEGORIES_PER_ACTIVITY, j++) {
                        TextView title = (TextView) findViewById(getResources().getIdentifier("title_" + k, "id", getPackageName()));
                        if(title.getText().toString().equals(InterestsUtils.getRatingAsString(rating))) {
                            enableInterest((RelativeLayout) relativeLayout.findViewWithTag(String.valueOf(k).trim()));
                            break;
                        }
                    }
                }
            }
        }
    }

}
