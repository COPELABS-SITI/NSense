// Generated code from Butter Knife. Do not modify!
package cs.usense.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import cs.usense.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CategoriesActivity_ViewBinding implements Unbinder {
  private CategoriesActivity target;

  private View view2131558545;

  @UiThread
  public CategoriesActivity_ViewBinding(CategoriesActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public CategoriesActivity_ViewBinding(final CategoriesActivity target, View source) {
    this.target = target;

    View view;
    target.progressBar = Utils.findRequiredViewAsType(source, R.id.progressBarInterests, "field 'progressBar'", ProgressBar.class);
    view = Utils.findRequiredView(source, R.id.view_more, "method 'onClickViewMore'");
    view2131558545 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClickViewMore(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    CategoriesActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.progressBar = null;

    view2131558545.setOnClickListener(null);
    view2131558545 = null;
  }
}
