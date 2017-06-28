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


public class SociabilityPresenter implements SociabilityInterfaces.Presenter {

    private ArrayList<SociabilityGraphItem> mChartData;

    private SociabilityInterfaces.View mView;

    public SociabilityPresenter(SociabilityInterfaces.View view) {
        mView = view;
    }

    private ArrayList<SociabilityGraphItem> getChartData(Context context) {
        if(mChartData == null) {
            mChartData = (NSenseDataSource.getInstance(context)).getStarsAvgValues();
        }
        return mChartData;
    }

    @Override
    public void onBarClick(Context context, int xIndex, int dataSetIndex) {
        String selectedDate = getChartData(context).get(xIndex).getDate();
        ArrayList<SociabilityDetailItem> items = (NSenseDataSource.getInstance(context)).getStarsOfADay(selectedDate, dataSetIndex);
        mView.onReceiveDayData(items, selectedDate, socialInformationType(context, dataSetIndex));

    }

    private String socialInformationType(Context context, int dataSetIndex) {
        if(dataSetIndex == 0) {
            return context.getString(R.string.Social_Interaction);
        } else {
            return context.getString(R.string.Propinquity);
        }
    }

    /** This method is responsible to load data on bar chart format */
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

}
