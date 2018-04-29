// Generated code from Butter Knife. Do not modify!
package cs.usense.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import cs.usense.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ReportsActivity_ViewBinding implements Unbinder {
  private ReportsActivity target;

  private View view2131558608;

  @UiThread
  public ReportsActivity_ViewBinding(ReportsActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ReportsActivity_ViewBinding(final ReportsActivity target, View source) {
    this.target = target;

    View view;
    target.emailReport = Utils.findRequiredViewAsType(source, R.id.email_report, "field 'emailReport'", TextView.class);
    target.reportsList = Utils.findRequiredViewAsType(source, R.id.reports_list, "field 'reportsList'", ListView.class);
    view = Utils.findRequiredView(source, R.id.email_report_content, "method 'onClickEmail'");
    view2131558608 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClickEmail(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    ReportsActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.emailReport = null;
    target.reportsList = null;

    view2131558608.setOnClickListener(null);
    view2131558608 = null;
  }
}
