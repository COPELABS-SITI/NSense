// Generated code from Butter Knife. Do not modify!
package cs.usense.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.github.mikephil.charting.charts.BarChart;
import cs.usense.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SociabilityActivity_ViewBinding implements Unbinder {
  private SociabilityActivity target;

  @UiThread
  public SociabilityActivity_ViewBinding(SociabilityActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SociabilityActivity_ViewBinding(SociabilityActivity target, View source) {
    this.target = target;

    target.barChart = Utils.findRequiredViewAsType(source, R.id.chart, "field 'barChart'", BarChart.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SociabilityActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.barChart = null;
  }
}
