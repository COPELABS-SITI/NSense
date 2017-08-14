/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/06/05.
 * Class is part of the NSense application.
 */

package cs.usense.interfaces;


import android.content.Context;

/**
 * This interface is used to implement MVP design pattern.
 * It establishes the communication between the view and the presenter
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public interface ReportsInterfaces {

    /**
     * This interface implements the view behavior
     */
    interface View {
        void onSetEmailReport(String emailAddress);
        void onReportsListReady(String[] reportNames);
    }

    /**
     * This interface implements how the presenter replies to the view
     */
    interface Presenter {
        void onResume(Context context);
        void onClickReport(Context context, int option);
        void onClickEmail(Context context);
        void onDestroy();
    }

}
