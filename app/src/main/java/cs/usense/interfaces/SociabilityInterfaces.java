/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/06/05.
 * Class is part of the NSense application.
 */

package cs.usense.interfaces;


import android.content.Context;

import com.github.mikephil.charting.data.BarData;

import java.util.ArrayList;

import cs.usense.models.SociabilityDetailItem;


/**
 * This interface is used to implement MVP design pattern.
 * It establishes the communication between the view and the presenter
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public interface SociabilityInterfaces {

    /**
     * This interface implements the view behavior
     */
    interface View {
        void onReceiveBarData(BarData barData);
        void onReceiveDayData(ArrayList<SociabilityDetailItem> items, String date, String dataType);
    }

    /**
     * This interface implements how the presenter replies to the view
     */
    interface Presenter {
        void onLoadBarDataSet(Context context);
        void onBarClick(Context context, int xIndex, int dataSetIndex);
        void onDestroy();
    }
}
