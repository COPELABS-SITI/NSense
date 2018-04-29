// Generated code from Butter Knife. Do not modify!
package cs.usense.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import cs.usense.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SensorNotFoundActivity_ViewBinding implements Unbinder {
  private SensorNotFoundActivity target;

  @UiThread
  public SensorNotFoundActivity_ViewBinding(SensorNotFoundActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SensorNotFoundActivity_ViewBinding(SensorNotFoundActivity target, View source) {
    this.target = target;

    target.warningTitle = Utils.findRequiredViewAsType(source, R.id.warning_title, "field 'warningTitle'", TextView.class);
    target.warningMessage = Utils.findRequiredViewAsType(source, R.id.warning_message, "field 'warningMessage'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SensorNotFoundActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.warningTitle = null;
    target.warningMessage = null;
  }
}
