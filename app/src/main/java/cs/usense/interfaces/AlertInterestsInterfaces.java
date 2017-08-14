/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/06/05.
 * Class is part of the NSense application.
 */

package cs.usense.interfaces;


import android.content.Context;

import java.util.ArrayList;

import cs.usense.models.AlertInterestItem;


/**
 * This interface is used to implement MVP design pattern.
 * It establishes the communication between the view and the presenter
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public interface AlertInterestsInterfaces {

    /**
     * This interface implements the view behavior
     */
    interface View {
        void onReceiveSimilarInterests(ArrayList<AlertInterestItem> similarInterests);
    }

    /**
     * This interface implements how the presenter replies to the view
     */
    interface Presenter {
        void loadSimilarInterests(Context context);
        void onDestroy();
    }
}
