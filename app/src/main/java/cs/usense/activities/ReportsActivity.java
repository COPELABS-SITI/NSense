/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cs.usense.R;
import cs.usense.adapters.ReportAdapter;
import cs.usense.interfaces.ReportsInterfaces;
import cs.usense.preferences.GeneralPreferences;
import cs.usense.presenters.ReportsPresenter;


/**
 * This class instantiates an activity with a menu that allows the users to generate some reports.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public class ReportsActivity extends ActionBarActivity implements OnItemClickListener, ReportsInterfaces.View {

    /** This textview shows the email that is configured to send the reports */
    @BindView(R.id.email_report) TextView emailReport;

    /** This listview shows the list of reports that can be generated */
    @BindView(R.id.reports_list) ListView reportsList;

    /** This object is the presenter of this activity */
    private ReportsInterfaces.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        setup();
    }

    /**
     * This method initialize everything that this class needs
     */
    private void setup() {
        ButterKnife.bind(this);
        mPresenter = new ReportsPresenter(this);
        reportsList.setOnItemClickListener(this);
        setActionBarTitle(getString(R.string.Reports));
        onSetEmailReport(GeneralPreferences.getReportEmail(this));
    }

    /**
     * This method load the email stored to a small TextView
     */
    @Override
    public void onSetEmailReport(String emailAddress) {
        emailReport.setText(emailAddress);
        GeneralPreferences.setReportEmail(this, emailAddress);
    }

    /**
     * This method load the list of reports that can be generated
     */
    @Override
    public void onReportsListReady(String[] reportNames) {
        reportsList.setAdapter(new ReportAdapter(this, R.layout.item_image_and_title, reportNames));
    }

    @Override
    public void onResume() {
        mPresenter.onResume(this);
        super.onResume();
    }

    /**
     * This method instantiates an OnClickListener to change the email address where reports are sent.
     */
    @OnClick(R.id.email_report_content)
    public void onClickEmail(View view) {
        mPresenter.onClickEmail(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.onClickReport(this, position);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SettingsActivity.class));
        finish();
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

}
