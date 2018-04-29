/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/04/04.
 * Class is part of the NSense application.
 */

package cs.usense.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cs.usense.R;
import cs.usense.preferences.InterestsPreferences;
import cs.usense.utilities.InterestsUtils;
import cs.usense.views.InformationView;


/**
 * This class instantiates an activity that allows the user to set his own category ratings.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2017
 */
@SuppressWarnings("ConstantConditions")
public class RatingsActivity extends ActionBarActivity {

    /** This variable is used to debug RatingsActivity class */
    private static final String TAG = "RatingsActivity";

    /** This variable represents how many categories we can see on subcategories activities */
    public static final int CATEGORIES_PER_ACTIVITY = 4;

    /** This variable represents how many stars we can set */
    public static final int STARS_PER_CATEGORY = 3;

    /** This ArrayList contains categories */
    private ArrayList<String> mCategories = new ArrayList<>();

    /** This ArrayList contains categories ratings */
    private ArrayList<String> mRatings = new ArrayList<>();

    /** This variable stores the subcategories presented on current activity */
    private int mCategoriesPresented;

    /** This variable stores the number of stars chosen */
    private int mStarsChosen;

    /** This variable stores the list index */
    private int mIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);
        setup();
    }

    /**
     *  This method initialize everything needed in this activity
     */
    private void setup() {
        ButterKnife.bind(this);
        setActivityTitle();
        fetchDataFromIntent();
        loadTitles();
        loadMatrix();
        loadRatings();
        setButtonNextLabel();
    }

    /**
     * This method set the action bar layout, activity title and it's description
     */
    private void setActivityTitle() {
        TextView interestsHeader = (TextView) findViewById(R.id.activity_title);
        interestsHeader.setText(getString(R.string.My_Ratings));
        setActionBarTitle(getString(R.string.My_Ratings));
    }

    /**
     * This method set the button next name
     */
    private void setButtonNextLabel() {
        if(mCategories.size() <= mIndex + mCategoriesPresented) {
            ((TextView) findViewById(R.id.next_btn)).setText(getString(R.string.finish));
        }
    }

    /**
     * This method is used to fetch data from intent
     */
    private void fetchDataFromIntent() {
        mCategories = InterestsPreferences.readCategoriesFromCacheAsArrayList(this);
        mIndex = getIntent().getIntExtra("index", 0);
        if((mRatings = getIntent().getStringArrayListExtra("ratings")) == null) {
            mRatings = InterestsPreferences.readInterestsFromCacheAsArrayList(this);
        }
    }

    /**
     * This method is used initialize interests titles
     */
    private void loadTitles() {
        for (int i = 0, k = mIndex; k < mCategories.size() && i < CATEGORIES_PER_ACTIVITY; i++, k++) {
            TextView title = (TextView) findViewById(getResources().getIdentifier("main_title_" + i, "id", getPackageName()));
            title.setText(InterestsUtils.getInterestAsString(mCategories.get(k)));
        }
    }

    /**
     * This method load on matrix user's ratings
     */
    private void loadMatrix() {
        for(mCategoriesPresented = 0; mCategoriesPresented + mIndex < mCategories.size() && mCategoriesPresented < CATEGORIES_PER_ACTIVITY; mCategoriesPresented++) {
            ArrayList<String> subCategories = InterestsUtils.getRatingsAsString(this, mCategories.get(mCategoriesPresented + mIndex));
            for(int j = 0, k = mCategoriesPresented; j < STARS_PER_CATEGORY; j++, k += CATEGORIES_PER_ACTIVITY) {
                InformationView infoView = (InformationView) findViewById(getResources().getIdentifier("info_" + k, "id", getPackageName()));
                infoView.setImageAndTitle(R.drawable.ic_star_empty, R.color.black, subCategories.get(j));
            }
        }
    }

    /**
     * This method initialize the button which allow the user's to choose they subcategories
     */
    @OnClick(R.id.view_more)
    public void buttonNext(View view) {
        if(mStarsChosen == mCategoriesPresented) {
            if (mCategories.size() > mIndex + mCategoriesPresented) {
                startNewRatingsActivity(mIndex + CATEGORIES_PER_ACTIVITY);
            } else {
                InterestsPreferences.saveRatingsOnCache(this, mRatings);
                Toast.makeText(this, getString(R.string.Interests_saved), Toast.LENGTH_LONG).show();
            }
            finish();
        } else {
            Toast.makeText(this, "Set your stars to proceed", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method load on matrix user's interests
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
                            infoView.switchStatus();
                            mStarsChosen++;
                            break;
                        }
                    }
                }
            }
        }
    }

    public void onClickRating(View view) {
        InformationView infoView = (InformationView) view;
        int column = Integer.parseInt((String) view.getTag());
        String category = ((TextView) findViewById(getResources().getIdentifier("main_title_" + column, "id", getPackageName()))).getText().toString();
        String interestWithRating = InterestsUtils.buildInterest(category, infoView.getTitle());
        clearColumn(infoView, category, column);
        if(!infoView.isEnabled()) {
            mRatings.add(interestWithRating);
            mStarsChosen++;
        }
    }

    private void clearColumn(InformationView clickedView, String category, int column) {
        for (int i = 0; i < STARS_PER_CATEGORY; i++) {
            InformationView infoView = (InformationView) findViewById(getResources().getIdentifier("info_" + (column + i * CATEGORIES_PER_ACTIVITY), "id", getPackageName()));
            if(!infoView.getTitle().equals(clickedView.getTitle()) && infoView.isEnabled())
                infoView.switchStatus();
            if(mRatings.remove(InterestsUtils.buildInterest(category, infoView.getTitle()))) {
                mStarsChosen--;
            }
        }
    }

    private void startNewRatingsActivity(int index) {
        startActivity(new Intent(this, RatingsActivity.class)
                .putStringArrayListExtra("categories", mCategories)
                .putStringArrayListExtra("ratings", mRatings)
                .putExtra("index", index)
        );
    }

    @Override
    public void onBackPressed() {
        if(mIndex > 0) {
            startNewRatingsActivity(mIndex - CATEGORIES_PER_ACTIVITY);
        } else {
            startActivity(new Intent(this, CategoriesActivity.class));
        }
        finish();
    }

}