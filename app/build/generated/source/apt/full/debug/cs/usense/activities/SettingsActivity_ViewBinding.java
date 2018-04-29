// Generated code from Butter Knife. Do not modify!
package cs.usense.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import cs.usense.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SettingsActivity_ViewBinding implements Unbinder {
  private SettingsActivity target;

  @UiThread
  public SettingsActivity_ViewBinding(SettingsActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SettingsActivity_ViewBinding(SettingsActivity target, View source) {
    this.target = target;

    target.settingsList = Utils.findRequiredViewAsType(source, R.id.settings_list, "field 'settingsList'", ListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SettingsActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.settingsList = null;
  }
}
