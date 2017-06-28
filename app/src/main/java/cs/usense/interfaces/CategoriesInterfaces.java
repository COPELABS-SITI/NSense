package cs.usense.interfaces;


import android.content.Context;
import android.widget.RelativeLayout;

public interface CategoriesInterfaces {

    interface View {
        void onUpdateProgressBar(int progress);
        void onValidationError(String errorMessage);
        void onUpdateCategory(android.view.View view, int tag, int primaryColor, int secondaryColor, Object state);
    }

    interface Presenter {
        boolean onValidation(Context context);
        void onLoadCategories(Context context, RelativeLayout relativeLayout);
        void onClickCategory(Context context, android.view.View view);
    }

}
