/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/06/05.
 * Class is part of the NSense application.
 */

package cs.usense.presenters;


import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import cs.usense.views.InformationView;
import cs.usense.R;
import cs.usense.interfaces.CategoriesInterfaces;
import cs.usense.preferences.InterestsPreferences;


/**
 * This class is used to implement MVP design pattern.
 * Receives requests from the view and treat them.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class CategoriesPresenter implements CategoriesInterfaces.Presenter {

    /** This variable is used to set how many interests users need to choose */
    private static final int MIN_CATEGORIES = 5;

    /** This ArrayList contains user's ratings */
    private ArrayList<String> mCategories = new ArrayList<>();

    /** This object is used to establish communication with the view */
    private CategoriesInterfaces.View mView;

    /**
     * This method is the CategoriesPresenter constructor
     * @param view view interface to communicate with the view
     */
    public CategoriesPresenter(CategoriesInterfaces.View view) {
        mView = view;
    }

    @Override
    public void onClickCategory(Context context, View view) {
        InformationView infoView = ((InformationView) view);
        String categoryValue = (String) infoView.getTag();
        if(mCategories.contains(categoryValue)) {
            mCategories.remove(categoryValue);
        } else {
            mCategories.add(categoryValue);
        }
        mView.onUpdateProgressBar(mCategories.size());
    }

    /**
     * This method checks if the user selected a minimum of MIN_CATEGORIES
     * categories of interests
     * @param context application context
     * @return true if a minimum of categories of interests was selected, false if not
     */
    @Override
    public boolean onValidation(Context context) {
        boolean isValid = mCategories.size() >= MIN_CATEGORIES;
        if(isValid) {
            InterestsPreferences.saveCategoriesOnCache(context, mCategories);
            InterestsPreferences.refreshRatings(context, mCategories);
        } else {
            mView.onValidationError(context.getString(R.string.set_your_interests_to_proceed));
        }
        return isValid;
    }

    /**
     * This method loads the stored categories to the matrix of categories
     * @param context application context
     * @param relativeLayout layout where the categories will be loaded
     */
    @Override
    public void onLoadCategories(Context context, RelativeLayout relativeLayout) {
        mCategories = InterestsPreferences.readCategoriesFromCacheAsArrayList(context);
        mView.onUpdateProgressBar(mCategories.size());
        for(String category : mCategories) {
            InformationView infoView = (InformationView) relativeLayout.findViewWithTag(category);
            infoView.switchStatus();
        }
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

}
