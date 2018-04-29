// Generated code from Butter Knife. Do not modify!
package cs.usense.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import cs.usense.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RatingsActivity_ViewBinding implements Unbinder {
  private RatingsActivity target;

  private View view2131558545;

  @UiThread
  public RatingsActivity_ViewBinding(RatingsActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public RatingsActivity_ViewBinding(final RatingsActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.view_more, "method 'buttonNext'");
    view2131558545 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.buttonNext(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    target = null;


    view2131558545.setOnClickListener(null);
    view2131558545 = null;
  }
}
