package cs.usense.interfaces;


import android.content.Context;

import com.github.mikephil.charting.data.BarData;

import java.util.ArrayList;

import cs.usense.models.SociabilityDetailItem;

public interface SociabilityInterfaces {

    interface View {
        void onReceiveBarData(BarData barData);
        void onReceiveDayData(ArrayList<SociabilityDetailItem> items, String date, String dataType);
    }

    interface Presenter {
        void onLoadBarDataSet(Context context);
        void onBarClick(Context context, int xIndex, int dataSetIndex);
    }
}
