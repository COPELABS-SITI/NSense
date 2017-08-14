/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/06/05.
 * Class is part of the NSense application.
 */

package cs.usense.interfaces;


import android.content.Context;
import android.widget.RelativeLayout;


/**
 * This interface is used to implement MVP design pattern.
 * It establishes the communication between the view and the presenter
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public interface CategoriesInterfaces {

    /**
     * This interface implements the view behavior
     */
    interface View {
        void onUpdateProgressBar(int progress);
        void onValidationError(String errorMessage);
    }

    /**
     * This interface implements how the presenter replies to the view
     */
    interface Presenter {
        boolean onValidation(Context context);
        void onLoadCategories(Context context, RelativeLayout relativeLayout);
        void onClickCategory(Context context, android.view.View view);
        void onDestroy();
    }

}
