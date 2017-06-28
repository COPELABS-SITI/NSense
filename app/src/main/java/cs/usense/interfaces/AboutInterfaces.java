package cs.usense.interfaces;


import android.content.Context;

public interface AboutInterfaces {

    interface View {
        void showAboutInfo(String aboutInfo);
    }

    interface Presenter {
        void onResume(Context context);
        void onDestroy();
    }

}
