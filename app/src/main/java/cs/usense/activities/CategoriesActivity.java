/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class instantiates an activity that allows user
 * set his own interests.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.activities;


import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cs.usense.R;
import cs.usense.interfaces.CategoriesInterfaces;
import cs.usense.presenters.CategoriesPresenter;


@SuppressWarnings("ConstantConditions")
public class CategoriesActivity extends ActionBarActivity implements CategoriesInterfaces.View {

    /** This variable is used to debug CategoriesActivity class */
    private static final String TAG = "CategoriesActivity";

    private CategoriesInterfaces.Presenter mPresenter;

    /** This ProgressBar increases and decreases with quantity of user's interests */
    @BindView(R.id.progressBarInterests) ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        setup();
    }

    /** This method initialize everything needed in this activity */
    private void setup() {
        ButterKnife.bind(this);
        mPresenter = new CategoriesPresenter(this);
        setActivityTitle();
        loadMatrix();
        loadCategories();
    }

    /** This method set the action bar layout, activity title and it's description */
    private void setActivityTitle() {
        ((TextView) findViewById(R.id.activity_title)).setText(getString(R.string.My_Categories));
        ((TextView) findViewById(R.id.header_description)).setText(getString(R.string.choose_interests));
        setActionBarTitle(getString(R.string.My_Categories));
    }

    /** This method initialize the matrix with categories and it's images */
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

    /** This method load on matrix user's interests */
    private void loadCategories() {
        mPresenter.onLoadCategories(this, (RelativeLayout) findViewById(R.id.interests));
    }

    public void onClickCategory(View view) {
        mPresenter.onClickCategory(this, view);
    }

    /** This method initialize the button which allow the user's to choose they subcategories */
    @OnClick(R.id.view_more)
    public void onClickViewMore(View view) {
        if(mPresenter.onValidation(this)) {
            startActivity(new Intent(CategoriesActivity.this, RatingsActivity.class));
            finish();
        }
    }

    @Override
    public void onUpdateCategory(View view, int tag, int primaryColor, int secondaryColor, Object state) {
        Log.i(TAG, "onUpdateCategory-> tag:" + tag);
        view.setTag(R.id.interests, state);
        view.setBackgroundColor(getResources().getColor(primaryColor));
        TextView title = (TextView) findViewById(getResources().getIdentifier("title_" + tag, "id", getPackageName()));
        ImageView icon = (ImageView) findViewById(getResources().getIdentifier("image_" + tag, "id", getPackageName()));
        title.setTextColor(getResources().getColor(secondaryColor));
        icon.setColorFilter(getResources().getColor(secondaryColor));
    }

    @Override
    public void onUpdateProgressBar(int progress) {
        Log.i(TAG, "onUpdateProgressBar-> progress:" + progress);
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onValidationError(String errorMessage) {
        Log.e(TAG, errorMessage);
        Toast.makeText(CategoriesActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if(mPresenter.onValidation(this)) {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        }
    }

}
