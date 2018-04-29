/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.activities;


import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import cs.usense.views.InformationView;


/**
 * This class instantiates an activity that allows the user to set his own interests.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
@SuppressWarnings("ConstantConditions")
public class CategoriesActivity extends ActionBarActivity implements CategoriesInterfaces.View {

    /** This variable is used to debug CategoriesActivity class */
    private static final String TAG = "CategoriesActivity";

    /** This ProgressBar increases and decreases with quantity of user's interests */
    @BindView(R.id.progressBarInterests) ProgressBar progressBar;

    /** This object is the presenter of this activity */
    private CategoriesInterfaces.Presenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        setup();
    }

    /**
     *  This method initialize everything needed in this activity
     */
    private void setup() {
        ButterKnife.bind(this);
        mPresenter = new CategoriesPresenter(this);
        setActivityTitle();
        loadMatrix();
        loadCategories();
    }

    /**
     * This method set the action bar layout, activity title and it's description
     */
    private void setActivityTitle() {
        ((TextView) findViewById(R.id.activity_title)).setText(getString(R.string.My_Categories));
        ((TextView) findViewById(R.id.header_description)).setText(getString(R.string.choose_interests));
        setActionBarTitle(getString(R.string.My_Categories));
    }

    /**
     * This method initialize the matrix with categories and it's images
     */
    private void loadMatrix() {
        String[] categories = getResources().getStringArray(R.array.categories);
        TypedArray categories_icons = getResources().obtainTypedArray(R.array.categories_icons);
        for(int i = 0; i < categories.length; i++) {
            InformationView infoView = (InformationView) findViewById(getResources().getIdentifier("info_" + i, "id", getPackageName()));
            infoView.setImageAndTitle(categories_icons.getResourceId(i, -1), categories[i]);
        }
        categories_icons.recycle();
    }

    /**
     * This method load on matrix user's interests
     */
    private void loadCategories() {
        mPresenter.onLoadCategories(this, (RelativeLayout) findViewById(R.id.interests));
    }

    public void onClickCategory(View view) {
        mPresenter.onClickCategory(this, view);
    }

    /**
     * This method initialize the button which allow the user's to choose they subcategories
     */
    @OnClick(R.id.view_more)
    public void onClickViewMore(View view) {
        if(mPresenter.onValidation(this)) {
            startActivity(new Intent(CategoriesActivity.this, RatingsActivity.class));
            finish();
        }
    }

    @Override
    public void onUpdateProgressBar(int progress) {
        Log.i(TAG, "onUpdateProgressBar-> progress:" + progress);
        progressBar.setProgress(progress);
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

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

}
