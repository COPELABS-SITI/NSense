package cs.usense.interfaces;


import android.content.Context;

import java.util.ArrayList;

import cs.usense.models.AlertInterestItem;

public interface AlertInterestsInterfaces {

    interface View {
        void onReceiveSimilarInterests(ArrayList<AlertInterestItem> similarInterests);
    }

    interface Presenter {
        void loadSimilarInterests(Context context);
        void onDestroy();
    }
}
