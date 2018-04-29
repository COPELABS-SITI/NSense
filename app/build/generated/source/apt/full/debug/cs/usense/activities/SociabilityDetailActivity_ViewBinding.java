// Generated code from Butter Knife. Do not modify!
package cs.usense.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import cs.usense.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SociabilityDetailActivity_ViewBinding implements Unbinder {
  private SociabilityDetailActivity target;

  @UiThread
  public SociabilityDetailActivity_ViewBinding(SociabilityDetailActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SociabilityDetailActivity_ViewBinding(SociabilityDetailActivity target, View source) {
    this.target = target;

    target.topBarImage = Utils.findRequiredViewAsType(source, R.id.top_bar_image, "field 'topBarImage'", ImageView.class);
    target.title = Utils.findRequiredViewAsType(source, R.id.sociability_detail_title, "field 'title'", TextView.class);
    target.starsList = Utils.findRequiredViewAsType(source, R.id.stars_list, "field 'starsList'", ListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SociabilityDetailActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.topBarImage = null;
    target.title = null;
    target.starsList = null;
  }
}
