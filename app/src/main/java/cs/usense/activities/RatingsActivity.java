/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class instantiates an activity that allows user
 * set his category ratings.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cs.usense.R;
import cs.usense.preferences.InterestsPreferences;
import cs.usense.utilities.InterestsUtils;

@SuppressWarnings("ConstantConditions")
public class RatingsActivity extends ActionBarActivity implements View.OnClickListener {

    /** This variable is used to debug RatingsActivity class */
    private static final String TAG = "RatingsActivity";

    /** This variable represents how many categories we can see on subcategories activities */
    private static final int NUMBER_OF_CATEGORIES_PER_ACTIVITY = 4;

    /** This variable represents how many stars we can set */
    private static final int NUMBER_OF_STARS_PER_CATEGORY = 3;

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
        setContentView(R.layout.activity_subcategories);
        setup();
    }

    /** This method initialize everything needed in this activity */
    private void setup() {
        ButterKnife.bind(this);
        setActivityTitle();
        fetchDataFromIntent();
        loadTitles();
        loadMatrix();
        loadRatings();
        setButtonNextLabel();
    }

    /** This method set the action bar layout, activity title and it's description */
    private void setActivityTitle() {
        TextView interestsHeader = (TextView) findViewById(R.id.activity_title);
        interestsHeader.setText(getString(R.string.My_Ratings));
        setActionBarTitle(getString(R.string.My_Ratings));
    }

    /** This method set the button next name */
    private void setButtonNextLabel() {
        if(mCategories.size() <= mIndex + mCategoriesPresented) {
            ((TextView) findViewById(R.id.next_btn)).setText(getString(R.string.finish));
        }
    }

    /** This method is used to fetch data from intent */
    private void fetchDataFromIntent() {
        //mCategories = getIntent().getStringArrayListExtra("categories");
        mCategories = InterestsPreferences.readCategoriesFromCacheAsArrayList(this);
        mIndex = getIntent().getIntExtra("index", 0);
        if((mRatings = getIntent().getStringArrayListExtra("ratings")) == null) {
            mRatings = InterestsPreferences.readInterestsFromCacheAsArrayList(this);
        }
    }

    /** This method is used initialize interests titles */
    private void loadTitles() {
        for (int i = 0, k = mIndex; k < mCategories.size() && i < NUMBER_OF_CATEGORIES_PER_ACTIVITY; i++, k++) {
            TextView title = (TextView) findViewById(getResources().getIdentifier("main_title_" + i, "id", getPackageName()));
            title.setText(InterestsUtils.getInterestAsString(mCategories.get(k)));
        }
    }

    /**
     * This method load on matrix user's ratings
     */
    private void loadMatrix() {
        for(mCategoriesPresented = 0; mCategoriesPresented + mIndex < mCategories.size() && mCategoriesPresented < NUMBER_OF_CATEGORIES_PER_ACTIVITY; mCategoriesPresented++) {
            ArrayList<String> subCategories = InterestsUtils.getRatingsAsString(this, mCategories.get(mCategoriesPresented + mIndex));
            for(int j = 0, k = mCategoriesPresented; j < NUMBER_OF_STARS_PER_CATEGORY; j++, k += NUMBER_OF_CATEGORIES_PER_ACTIVITY) {
                ((TextView) findViewById(getResources().getIdentifier("title_" + k, "id", getPackageName()))).setText(subCategories.get(j));
                ((ImageView) findViewById(getResources().getIdentifier("image_" + k, "id", getPackageName()))).setImageResource(R.drawable.ic_star_empty);
            }
        }
    }

    /** This method initialize the button which allow the user's to choose they subcategories */
    @OnClick(R.id.view_more)
    public void buttonNext(View view) {
        if(mStarsChosen == mCategoriesPresented) {
            if (mCategories.size() > mIndex + mCategoriesPresented) {
                startNewRatingsActivity(mIndex + NUMBER_OF_CATEGORIES_PER_ACTIVITY);
                finish();
            } else {
                InterestsPreferences.saveRatingsOnCache(this, mRatings);
                Toast.makeText(this, getString(R.string.Interests_saved), Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Set your stars to proceed", Toast.LENGTH_LONG).show();
        }
    }

    /** This method load on matrix user's interests */
    private void loadRatings() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.interests);
        for(String interest : mRatings) {
            for(int i = 0; i < NUMBER_OF_CATEGORIES_PER_ACTIVITY; i++) {
                TextView category = (TextView) findViewById(getResources().getIdentifier("main_title_" + i, "id", getPackageName()));
                if(category.getText().toString().equals(InterestsUtils.getInterestAsString((Integer.parseInt(interest) / 10) + "0"))) {
                    String rating = String.valueOf(Integer.parseInt(interest) % 10);
                    for(int j = 0, k = i; j < NUMBER_OF_STARS_PER_CATEGORY; k += NUMBER_OF_CATEGORIES_PER_ACTIVITY) {
                        TextView title = (TextView) findViewById(getResources().getIdentifier("title_" + k, "id", getPackageName()));
                        if(title.getText().toString().equals(InterestsUtils.getRatingAsString(rating))) {
                            matrixAction((RelativeLayout) relativeLayout.findViewWithTag(String.valueOf(k).trim()), 1, R.color.black, R.color.white);
                            mStarsChosen++;
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        RelativeLayout relativeLayout = (RelativeLayout) view;
        int tag = Integer.parseInt(relativeLayout.getTag().toString());
        int column = tag % NUMBER_OF_CATEGORIES_PER_ACTIVITY;
        Object state = view.getTag(R.id.interests);
        String rating = ((TextView) ((LinearLayout)relativeLayout.getChildAt(0)).getChildAt(1)).getText().toString();
        String interest = ((TextView) findViewById(getResources().getIdentifier("main_title_" + column, "id", getPackageName()))).getText().toString();
        int interestPlusRating = Integer.valueOf(InterestsUtils.getInterestValue(interest)) + Integer.valueOf(InterestsUtils.getRatingValue(rating));
        if (state != null) {
            mStarsChosen--;
            mRatings.remove(String.valueOf(interestPlusRating));
            matrixAction(relativeLayout, null, R.color.white, R.color.black);
            Log.i(TAG, relativeLayout.getTag().toString() + " off");
        } else {
            if(!(rating.isEmpty())) {
                clearColumn(interest, tag);
                mRatings.add(String.valueOf(interestPlusRating));
                matrixAction(relativeLayout, 1, R.color.black, R.color.white);
                if(mStarsChosen != NUMBER_OF_CATEGORIES_PER_ACTIVITY)
                    mStarsChosen++;
                Log.i(TAG, relativeLayout.getTag().toString() + " on");
            }
        }
    }

    private void matrixAction(RelativeLayout relativeLayout, Object tag, int primaryColor, int secondaryColor) {
        int count = relativeLayout.getChildCount();
        relativeLayout.setTag(R.id.interests, tag);
        relativeLayout.setBackgroundColor(getResources().getColor(primaryColor));
        for (int i = 0; i < count; i++) {
            View view = relativeLayout.getChildAt(i);
            if(view instanceof LinearLayout) {
                int countLinearL = ((LinearLayout) view).getChildCount();
                for (int j = 0; j < countLinearL; j++) {
                    View viewLinear = ((LinearLayout) view).getChildAt(j);
                    if (viewLinear instanceof ImageView) {
                        if(((ImageView) viewLinear).getDrawable() != null) {
                            ((ImageView) viewLinear).setColorFilter(getResources().getColor(secondaryColor));
                        }
                    } else if (viewLinear instanceof TextView) {
                        ((TextView) viewLinear).setTextColor(getResources().getColor(secondaryColor));
                    }
                }
            }
        }
    }

    private void clearColumn(String interest, int tag) {
        boolean ex1 = false, ex2 = false;
        for(int i = 0;!ex1 || !ex2; i++) {
            try {
                int positionToClean = tag;
                if(!ex1) positionToClean -= NUMBER_OF_CATEGORIES_PER_ACTIVITY * i;
                else positionToClean += NUMBER_OF_CATEGORIES_PER_ACTIVITY * i;
                RelativeLayout relativeLayout = ((RelativeLayout) findViewById(getResources().getIdentifier("matrix_position_" + positionToClean, "id", getPackageName())));
                if(relativeLayout.getTag(R.id.interests) != null)
                    mStarsChosen--;
                String rating = ((TextView) ((LinearLayout)relativeLayout.getChildAt(0)).getChildAt(1)).getText().toString();
                int interestPlusRating = Integer.valueOf(InterestsUtils.getInterestValue(interest)) + Integer.valueOf(InterestsUtils.getRatingValue(rating));
                matrixAction(relativeLayout, null, R.color.white, R.color.black);
                mRatings.remove(String.valueOf(interestPlusRating));
            } catch (Exception e) {
                i = 0;
                if (!ex1) ex1 = true;
                else ex2 = true;
            }
        }
    }

    private void startNewRatingsActivity(int index) {
        startActivity(new Intent(RatingsActivity.this, RatingsActivity.class)
                .putStringArrayListExtra("categories", mCategories)
                .putStringArrayListExtra("ratings", mRatings)
                .putExtra("index", index)
        );
    }

    @Override
    public void onBackPressed() {
        if(mIndex > 0) {
            startNewRatingsActivity(mIndex - NUMBER_OF_CATEGORIES_PER_ACTIVITY);
        } else {
            startActivity(new Intent(this, CategoriesActivity.class));
        }
        finish();
    }

}