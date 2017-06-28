/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class instantiates an activity to show the interests
 * of a selected device.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import cs.usense.R;
import cs.usense.inferenceModule.SocialDetail;
import cs.usense.utilities.InterestsUtils;

public class DeviceCategoriesActivity extends AppCompatActivity {

    /** This variable is used to debug RatingsActivity class */
    private static final String TAG = "DeviceCatActivity";

    /** This ArrayList stores the user's interests */
    private ArrayList<String> mCategories = new ArrayList<>();

    /** This ArrayList stores the user's interests */
    private ArrayList<String> mRatings = new ArrayList<>();

    private SocialDetail mSocialInformation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_categories);
        setup();
    }


    /**
     * This method is called to check and place the interests
     */
    private void setup() {
        mSocialInformation = getIntent().getExtras().getParcelable("deviceInfo");
        Log.i(TAG, "Social Information received: \n" + mSocialInformation.toString());
        ImageView topBarImage = (ImageView) findViewById(R.id.top_bar_image);
        topBarImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        TextView interestsHeader = (TextView) findViewById(R.id.activity_title);


        /** Fetch the user's interests */
        if (mSocialInformation.getInterests() != null && !mSocialInformation.getInterests().isEmpty()) {
            interestsHeader.setText(getString(R.string.user_interests, mSocialInformation.getDeviceName()));
            parseUserInterestsToArrayList(mSocialInformation.getInterests());
        }

        /** Check how many interests are received */
        if(mCategories.size() > 0) {
            /** Places the user's interests on the matrix */
            loadMatrix();
            loadCategories();
        } else {
            /** There's no interests received, will show a message with interests not available */
            interestsHeader.setText(getString(R.string.Interests_not_available));
        }

        if(mRatings.size() > 0) {
            setupSubCategoriesButton();
        } else {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.view_more);
            linearLayout.setVisibility(View.GONE);
        }

    }

    /**
     * Parse the interests from String to ArrayList of Strings
     */
    private void parseUserInterestsToArrayList(String rawInterests) {
        ArrayList<String> temp = new ArrayList<>(Arrays.asList(rawInterests.split(",")));
        for(String interest : temp) {
            mCategories.add(InterestsUtils.getCategoryOfRating(interest));
            mRatings.add(interest);
        }
    }

    private void setupSubCategoriesButton() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.view_more);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeviceCategoriesActivity.this, DeviceRatingsActivity.class)
                        .putExtra("deviceName", mSocialInformation.getDeviceName())
                        .putStringArrayListExtra("categories", mCategories)
                        .putStringArrayListExtra("ratings", mRatings)
                );
            }
        });
    }

    /**
     * This method is used to set user's interests
     * @param mRelativeLayout interest layout
     */
    private void enableInterest(RelativeLayout mRelativeLayout) {
        int count = mRelativeLayout.getChildCount();
        mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.black));
        mRelativeLayout.setTag(R.id.interests, 1);
        for (int i = 0; i < count; i++) {
            View view = mRelativeLayout.getChildAt(i);
            if(view instanceof LinearLayout) {
                int countLinearL = ((LinearLayout) view).getChildCount();
                for (int j = 0; j < countLinearL; j++) {
                    View viewLinear = ((LinearLayout) view).getChildAt(j);
                    if (viewLinear instanceof ImageView) {
                        ((ImageView) viewLinear).setColorFilter(Color.WHITE);
                    } else if (viewLinear instanceof TextView) {
                        ((TextView) viewLinear).setTextColor(Color.WHITE);
                    }
                }
            }
        }
    }

    /**
     * This method load on matrix user's interests
     */
    private void loadCategories() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.interests);
        for(String interest : mCategories) {
            if(Integer.parseInt(interest) % 10 == 0) {
                String category = String.valueOf(Integer.parseInt(interest) / 10);
                RelativeLayout mRelativeLayout = (RelativeLayout) relativeLayout.findViewWithTag(category.trim());
                if (mRelativeLayout != null) {
                    enableInterest(mRelativeLayout);
                }
            }
        }
    }

    private void loadMatrix() {
        String[] categories = getResources().getStringArray(R.array.categories);
        TypedArray categories_icons = getResources().obtainTypedArray(R.array.categories_icons);
        for(int i = 0; i < categories.length; i++) {
            TextView title = (TextView) findViewById(getResources().getIdentifier("title_" + i, "id", getPackageName()));
            ImageView icon = (ImageView) findViewById(getResources().getIdentifier("image_" + i, "id", getPackageName()));
            title.setText(categories[i]);
            icon.setImageResource(categories_icons.getResourceId(i, -1));
        }
    }

}