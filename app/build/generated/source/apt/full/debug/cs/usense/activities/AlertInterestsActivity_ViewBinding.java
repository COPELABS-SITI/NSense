// Generated code from Butter Knife. Do not modify!
package cs.usense.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import cs.usense.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AlertInterestsActivity_ViewBinding implements Unbinder {
  private AlertInterestsActivity target;

  @UiThread
  public AlertInterestsActivity_ViewBinding(AlertInterestsActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AlertInterestsActivity_ViewBinding(AlertInterestsActivity target, View source) {
    this.target = target;

    target.topIcon = Utils.findRequiredViewAsType(source, R.id.top_bar_image, "field 'topIcon'", ImageView.class);
    target.listView = Utils.findRequiredViewAsType(source, R.id.list, "field 'listView'", ListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AlertInterestsActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.topIcon = null;
    target.listView = null;
  }
}
