package cs.usense.presenters;


import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import cs.usense.R;
import cs.usense.interfaces.CategoriesInterfaces;
import cs.usense.preferences.InterestsPreferences;

public class CategoriesPresenter implements CategoriesInterfaces.Presenter {

    /** This variable is used to set how many interests users need to choose */
    private static final int MIN_CATEGORIES = 5;

    /** This ArrayList contains user's ratings */
    private ArrayList<String> mCategories = new ArrayList<>();

    private CategoriesInterfaces.View mView;

    public CategoriesPresenter(CategoriesInterfaces.View view) {
        mView = view;
    }

    @Override
    public void onClickCategory(Context context, View view) {
        int tag = Integer.parseInt(view.getTag().toString());
        RelativeLayout relativeLayout = (RelativeLayout) view;
        Object state = relativeLayout.getTag(R.id.interests);
        if (state != null) {
            /** disable category */
            mView.onUpdateCategory(view, tag, R.color.white, R.color.black, null);
            mCategories.remove(String.valueOf(tag * 10));
        } else {
            /** enable category */
            mView.onUpdateCategory(view, tag, R.color.black, R.color.white, 1);
            mCategories.add(String.valueOf(tag * 10));
        }
        mView.onUpdateProgressBar(mCategories.size());
    }

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

    @Override
    public void onLoadCategories(Context context, RelativeLayout relativeLayout) {
        ArrayList<String> categories = InterestsPreferences.readCategoriesFromCacheAsArrayList(context);
        for(String category : categories) {
            if(Integer.parseInt(category) % 10 == 0) {
                String categoryValue = String.valueOf(Integer.parseInt(category) / 10);
                RelativeLayout categoryLayout = (RelativeLayout) relativeLayout.findViewWithTag(categoryValue.trim());
                if (categoryLayout != null) {
                    onClickCategory(context, categoryLayout);
                }
            }
        }
    }

}
