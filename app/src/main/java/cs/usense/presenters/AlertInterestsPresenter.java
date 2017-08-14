/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/06/05.
 * Class is part of the NSense application.
 */

package cs.usense.presenters;


import android.content.Context;

import java.util.ArrayList;

import cs.usense.inferenceModule.SocialDetail;
import cs.usense.inferenceModule.SocialInteraction;
import cs.usense.interfaces.AlertInterestsInterfaces;
import cs.usense.models.AlertInterestItem;
import cs.usense.preferences.InterestsPreferences;
import cs.usense.utilities.InterestsUtils;


/**
 * This class is used to implement MVP design pattern.
 * Receives requests from the view and treat them.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class AlertInterestsPresenter implements AlertInterestsInterfaces.Presenter {

    /** This object is used to establish communication with the view */
    private AlertInterestsInterfaces.View mView;

    /**
     * This method is the AlertInterestsPresenter constructor
     * @param view view interface to communicate with the view
     */
    public AlertInterestsPresenter(AlertInterestsInterfaces.View view) {
        mView = view;
    }

    /**
     * This method is used to load similar interests to mSimilarInterests
     */
    @Override
    public void loadSimilarInterests(Context context) {
        ArrayList<AlertInterestItem> alertInterestItems = new ArrayList<>();
        ArrayList<SocialDetail> socialDetails = SocialInteraction.getCurrentSocialInformation();
        ArrayList<String> myInterests = InterestsPreferences.readCategoriesFromCacheAsArrayList(context);
        for(SocialDetail socialDetail : socialDetails) {
            ArrayList<String> similarInterests = getCommonInterests(socialDetail, myInterests);
            if (!similarInterests.isEmpty()) {
                alertInterestItems.add(new AlertInterestItem(socialDetail.getDeviceName(), similarInterests));
            }
        }
        mView.onReceiveSimilarInterests(alertInterestItems);
    }

    /**
     * This method compares and retrieves an ArrayList with the common interests
     * @param socialDetail other user interests
     * @param myInterests owner's device interests
     * @return ArrayList with common interests
     */
    private ArrayList<String> getCommonInterests(SocialDetail socialDetail, ArrayList<String> myInterests) {
        ArrayList<String> similarInterests = new ArrayList<>();
        if(socialDetail.getInterests() != null) {
            for (String interest : myInterests) {
                if (socialDetail.getCategories().contains(interest)) {
                    similarInterests.add(InterestsUtils.getInterestAsString(interest));
                }
            }
        }
        return similarInterests;
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

}
