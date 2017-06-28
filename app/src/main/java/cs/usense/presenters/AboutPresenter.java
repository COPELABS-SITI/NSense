package cs.usense.presenters;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import cs.usense.R;
import cs.usense.interfaces.AboutInterfaces;

public class AboutPresenter implements AboutInterfaces.Presenter {

    private AboutInterfaces.View mView;

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
