package cs.usense.presenters;


import android.content.Context;

import java.util.ArrayList;

import cs.usense.inferenceModule.SocialDetail;
import cs.usense.inferenceModule.SocialInteraction;
import cs.usense.interfaces.AlertInterestsInterfaces;
import cs.usense.models.AlertInterestItem;
import cs.usense.preferences.InterestsPreferences;
import cs.usense.utilities.InterestsUtils;

public class AlertInterestsPresenter implements AlertInterestsInterfaces.Presenter {

    private AlertInterestsInterfaces.View mView;


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
