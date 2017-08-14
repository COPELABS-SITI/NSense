/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/06/05.
 * Class is part of the NSense application.
 */

package cs.usense.presenters;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import cs.usense.R;
import cs.usense.interfaces.AboutInterfaces;


/**
 * This class is used to implement MVP design pattern.
 * Receives requests from the view and treat them.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class AboutPresenter implements AboutInterfaces.Presenter {

    /** This object is used to establish communication with the view */
    private AboutInterfaces.View mView;

    /**
     * This method is the AboutPresenter constructor
     * @param view view interface to communicate with the view
     */
    public AboutPresenter(AboutInterfaces.View view) {
        mView = view;
    }

    @Override
    public void onResume(Context context) {
        try {
            PackageInfo pInfo  = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            mView.showAboutInfo(context.getString(R.string.version, pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

}
