package cs.usense.interfaces;


import android.content.Context;

public interface ReportsInterfaces {

    interface View {
        void onSetEmailReport(String email);
        void onReportsListReady(String[] reportNames);
    }

    interface Presenter {
        void onResume(Context context);
        void onClickReport(Context context, int option);
        void onClickEmail(Context context);
    }

}
