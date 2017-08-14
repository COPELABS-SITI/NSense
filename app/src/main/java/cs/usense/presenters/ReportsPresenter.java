/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/06/05.
 * Class is part of the NSense application.
 */

package cs.usense.presenters;


import android.content.Context;

import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import cs.usense.R;
import cs.usense.interfaces.ReportsInterfaces;
import cs.usense.preferences.GeneralPreferences;
import cs.usense.reports.InterestsReport;
import cs.usense.reports.MergedReport;
import cs.usense.reports.SocialReport;
import cs.usense.utilities.Utils;


/**
 * This class is used to implement MVP design pattern.
 * Receives requests from the view and treat them.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class ReportsPresenter implements ReportsInterfaces.Presenter, LovelyTextInputDialog.TextFilter,
        LovelyTextInputDialog.OnTextInputConfirmListener {

    /** This object is used to establish communication with the view */
    private ReportsInterfaces.View mView;

    /**
     * This method is the ReportsPresenter constructor
     * @param view view interface to communicate with the view
     */
    public ReportsPresenter(ReportsInterfaces.View view) {
        mView = view;
    }

    @Override
    public void onClickReport(Context context, int option) {
        if(option == 0) {
            SocialReport.sendReport(context);
        } else if (option == 1) {
            InterestsReport.sendReport(context);
        }  else if (option == 2) {
            MergedReport.sendReport(context);
        }
    }

    @Override
    public void onClickEmail(final Context context) {
        new LovelyTextInputDialog(context)
            .setTopColorRes(R.color.lollipop_green)
            .setMessage(R.string.set_email_report_message)
            .setIcon(R.drawable.ic_arroba)
            .setInitialInput(GeneralPreferences.getReportEmail(context))
            .setInputFilter(R.string.email_not_valid, this)
            .setConfirmButton(android.R.string.ok, this)
            .show();
    }

    @Override
    public boolean check(String email) {
        return Utils.isEmailValid(email.trim());
    }

    @Override
    public void onTextInputConfirmed(String email) {
        mView.onSetEmailReport(email.trim());
    }

    @Override
    public void onResume(Context context) {
        mView.onReportsListReady(context.getResources().getStringArray(R.array.reports_titles));
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

}
