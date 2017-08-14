/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cs.usense.R;
import cs.usense.interfaces.SociabilityInterfaces;
import cs.usense.models.SociabilityDetailItem;
import cs.usense.presenters.SociabilityPresenter;
import cs.usense.utilities.Utils;

import static cs.usense.activities.SociabilityDetailActivity.EXTRA_DATA;
import static cs.usense.activities.SociabilityDetailActivity.EXTRA_DATE;
import static cs.usense.activities.SociabilityDetailActivity.EXTRA_SOCIAL_DATA_TYPE;


/**
 * This class instantiates an activity to show an history
 * of social interaction and propinquity in a scale that starts on 0 to 5.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public class SociabilityActivity extends ActionBarActivity implements OnChartValueSelectedListener, SociabilityInterfaces.View,
        YAxisValueFormatter, ValueFormatter {

    /** This variable is used to debug SociabilityActivity class */
    private static final String TAG = "SociabilityActivity";

    /** This variable is used to show the bar graph */
    @BindView(R.id.chart) BarChart barChart;

    /** This object is the presenter of this activity */
    private SociabilityInterfaces.Presenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sociability);
        setup();
    }

    /**
     * This method stars this class components
     */
    private void setup() {
        ButterKnife.bind(this);
        setActionBarTitle(getString(R.string.Sociability));
        mPresenter = new SociabilityPresenter(this);
        barChart.setOnChartValueSelectedListener(this);
        formatBarChart();
    }

    /**
     * This method is responsible to format the bar chart
     */
    private void formatBarChart() {
        Log.i(TAG, "formatGraph was invoked");
        barChart.animateY(2000);
        barChart.setDescription(Utils.EMPTY_STRING);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.setScaleYEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelsToSkip(0);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(this);

        leftAxis.setAxisMaxValue(6f);
        leftAxis.setAxisMinValue(0f);
    }

    @Override
    public void onResume() {
        mPresenter.onLoadBarDataSet(this);
        super.onResume();
    }

    @Override
    public void onReceiveBarData(BarData barData) {
        formatAndDataSet(barData);
        barChart.setData(barData);
    }

    /**
     * This method is responsible to format data set
     * @param dataSet data set to be formatted
     */
    private void formatAndDataSet(BarData dataSet) {
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter(this);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight highlight) {
        mPresenter.onBarClick(this, e.getXIndex(), dataSetIndex);
    }

    @Override
    public void onReceiveDayData(ArrayList<SociabilityDetailItem> items, String date, String dataType) {
        startActivity(new Intent(this, SociabilityDetailActivity.class)
                .putExtra(EXTRA_DATE, date)
                .putExtra(EXTRA_DATA, items)
                .putExtra(EXTRA_SOCIAL_DATA_TYPE, dataType)
        );
    }

    @Override
    public void onNothingSelected() {}

    @Override
    public String getFormattedValue(float v, YAxis yAxis) {
        return new DecimalFormat("0").format(v);
    }

    @Override
    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
        return new DecimalFormat("#.0").format(v);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onBackPressed");
        mPresenter.onDestroy();
        super.onDestroy();
    }

}
