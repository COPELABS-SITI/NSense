/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/06/05.
 * Class is part of the NSense application.
 */

package cs.usense.presenters;


import android.content.Context;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import cs.usense.R;
import cs.usense.db.NSenseDataSource;
import cs.usense.interfaces.SociabilityInterfaces;
import cs.usense.models.SociabilityDetailItem;
import cs.usense.models.SociabilityGraphItem;


/**
 * This class is used to implement MVP design pattern.
 * Receives requests from the view and treat them.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class SociabilityPresenter implements SociabilityInterfaces.Presenter {

    /** This list contains the data to be loaded on the chart */
    private ArrayList<SociabilityGraphItem> mChartData;

    /** This object is used to establish communication with the view */
    private SociabilityInterfaces.View mView;


    /**
     * This method is the SociabilityPresenter constructor
     * @param view view interface to communicate with the view
     */
    public SociabilityPresenter(SociabilityInterfaces.View view) {
        mView = view;
    }

    /**
     * This method returns the data to load on the chart
     * @param context application context
     * @return data to be loaded on the chart
     */
    private ArrayList<SociabilityGraphItem> getChartData(Context context) {
        if(mChartData == null) {
            mChartData = (NSenseDataSource.getInstance(context)).getStarsAvgValues();
        }
        return mChartData;
    }

    /**
     * This method is responsible to manage the user's interactions with the chart
     * @param context application context
     * @param xIndex selected column index
     * @param dataSetIndex data index, 2 columns per day
     */
    @Override
    public void onBarClick(Context context, int xIndex, int dataSetIndex) {
        String selectedDate = getChartData(context).get(xIndex).getDate();
        ArrayList<SociabilityDetailItem> items = (NSenseDataSource.getInstance(context)).getStarsOfADay(selectedDate, dataSetIndex);
        mView.onReceiveDayData(items, selectedDate, socialInformationType(context, dataSetIndex));

    }

    /**
     * This method checks which option the user chosen.
     * If dataSetIndex == 0 user chosen social interaction
     * If not, the user chosen propinquity
     * @param context application context
     * @param dataSetIndex data index
     * @return string with what the user chosen
     */
    private String socialInformationType(Context context, int dataSetIndex) {
        return dataSetIndex == 0 ? context.getString(R.string.Social_Interaction) : context.getString(R.string.Propinquity);
    }

    /**
     * This method is responsible to load data on bar chart format
     * @param context application context
     */
    @Override
    public void onLoadBarDataSet(Context context) {
        ArrayList<BarEntry> siVData = new ArrayList<>();
        ArrayList<BarEntry> propData = new ArrayList<>();
        ArrayList<String> daysOfMonthData = new ArrayList<>();
        ArrayList<SociabilityGraphItem> chartData = getChartData(context);

        for(int i = 0; i < chartData.size(); i++) {
            daysOfMonthData.add(chartData.get(i).getDate());
            siVData.add(new BarEntry(chartData.get(i).getSociabilityStars(), i));
            propData.add(new BarEntry(chartData.get(i).getPropinquityStars(), i));
        }

        List<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(createBarDataSet(context, siVData, R.string.Social_Interaction, R.color.light_green));
        dataSets.add(createBarDataSet(context, propData, R.string.Propinquity, R.color.application_blue));
        mView.onReceiveBarData(new BarData(daysOfMonthData , dataSets));
    }

    /**
     * This method is responsible to create a bar chart data set
     * @param data data set data
     * @param label data set label
     * @param color data set color
     * @return bar chart data set
     */
    private BarDataSet createBarDataSet(Context context, ArrayList<BarEntry> data, int label, int color) {
        BarDataSet dataSet = new BarDataSet(data, context.getString(label));
        dataSet.setColor(context.getResources().getColor(color));
        return dataSet;
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

}
