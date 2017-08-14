/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */


package cs.usense.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cs.usense.R;
import cs.usense.inferenceModule.SocialDetail;
import cs.usense.utilities.InterestsUtils;
import cs.usense.views.InformationView;

import static cs.usense.utilities.InterestsUtils.CATEGORIES_OFFSET;

/**
 * This class instantiates an activity to show the interests from a selected device.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2017
 */
public class DeviceCategoriesActivity extends AppCompatActivity {

    /** This variable is used to debug RatingsActivity class */
    private static final String TAG = "DeviceCatActivity";

    /** This LinearLayout allows the user to watch interests ratings from the selected user */
    @BindView(R.id.view_more) LinearLayout viewMore;

    /** This ArrayList stores the user's interests */
    private ArrayList<String> mCategories = new ArrayList<>();

    /** This ArrayList stores the user's interests */
    private ArrayList<String> mRatings = new ArrayList<>();

    /** This variable stores the social detail data to present on this activity */
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
        ButterKnife.bind(this);
        mSocialInformation = getIntent().getExtras().getParcelable("deviceInfo");
        Log.i(TAG, "Social Information received: \n" + mSocialInformation.toString());
        ImageView topBarImage = (ImageView) findViewById(R.id.top_bar_image);
        topBarImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
        TextView interestsHeader = (TextView) findViewById(R.id.activity_title);

        /* Fetch user's interests */
        if (mSocialInformation.getInterests() != null && !mSocialInformation.getInterests().isEmpty()) {
            interestsHeader.setText(getString(R.string.user_interests, mSocialInformation.getDeviceName()));
            parseUserInterestsToArrayList(mSocialInformation.getInterests());
        }

        /* Check how many interests are received */
        if(mCategories.size() > 0) {
            /* Place user's interests on the matrix */
            loadMatrix();
            loadCategories();
        } else {
            /* There's no interests received, will show a message with interests not available */
            interestsHeader.setText(getString(R.string.Interests_not_available));
        }

        if(mRatings.size() == 0) {
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
            Log.i(TAG, "Parsing " + interest);
        }
    }

    @OnClick(R.id.view_more)
    public void onClickViewMore() {
        startActivity(new Intent(DeviceCategoriesActivity.this, DeviceRatingsActivity.class)
                .putExtra("deviceName", mSocialInformation.getDeviceName())
                .putStringArrayListExtra("categories", mCategories)
                .putStringArrayListExtra("ratings", mRatings)
        );
    }

    /**
     * This method loads the categories on the matrix
     */
    private void loadMatrix() {
        String[] categories = getResources().getStringArray(R.array.categories);
        TypedArray categories_icons = getResources().obtainTypedArray(R.array.categories_icons);
        for(int i = 0; i < categories.length; i++) {
            InformationView infoView = (InformationView) findViewById(getResources().getIdentifier("info_" + (i * CATEGORIES_OFFSET), "id", getPackageName()));
            infoView.setImageAndTitle(categories_icons.getResourceId(i, -1), categories[i]);
            Log.i(TAG, "Loading category " + categories[i]);
        }
        categories_icons.recycle();
    }

    /**
     * This method loads the users categories on the matrix
     */
    private void loadCategories() {
        for(String category : mCategories) {
            InformationView infoView = (InformationView) findViewById(getResources().getIdentifier("info_" + category, "id", getPackageName()));
            infoView.switchStatus();
            Log.i(TAG, "Switching status of information view " + infoView.getTitle());
        }
    }


}