// Generated code from Butter Knife. Do not modify!
package cs.usense.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import cs.usense.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class DeviceRatingsActivity_ViewBinding implements Unbinder {
  private DeviceRatingsActivity target;

  private View view2131558545;

  @UiThread
  public DeviceRatingsActivity_ViewBinding(DeviceRatingsActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public DeviceRatingsActivity_ViewBinding(final DeviceRatingsActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.view_more, "field 'viewMore' and method 'onClickViewMore'");
    target.viewMore = Utils.castView(view, R.id.view_more, "field 'viewMore'", LinearLayout.class);
    view2131558545 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClickViewMore();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    DeviceRatingsActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.viewMore = null;

    view2131558545.setOnClickListener(null);
    view2131558545 = null;
  }
}
