package com.example.flow.appflow;

import android.os.Bundle;
import flow.Backstack;
import flow.Flow;
import flow.Parcer;
import javax.annotation.Nullable;

import static com.example.flow.util.Preconditions.checkArgument;

/**
 * Handles Bundle persistence of a Flow.
 */
public abstract class FlowBundler {
  private static final String FLOW_KEY = "flow_key";

  private final Flow.Listener listener;
  private final Parcer<Object> parcer;

  private Flow flow;

  public FlowBundler(Flow.Listener listener, Parcer<Object> parcer) {
    this.listener = listener;
    this.parcer = parcer;
  }

  public AppFlow onCreate(Bundle savedInstanceState) {
    checkArgument(flow == null, "Flow already created.");
    Backstack restoredBackstack = null;
    if (savedInstanceState != null && savedInstanceState.containsKey(FLOW_KEY)) {
      restoredBackstack = Backstack.from(savedInstanceState.getParcelable(FLOW_KEY), parcer);
    }
    flow = new Flow(getColdStartBackstack(restoredBackstack), listener);
    return new AppFlow(flow);
  }

  public void onSaveInstanceState(Bundle outState) {
    Backstack backstack = getBackstackToSave(flow.getBackstack());
    if (backstack == null) return;
    outState.putParcelable(FLOW_KEY, backstack.getParcelable(parcer));
  }

  public final Flow getFlow() {
    return flow;
  }

  /**
   * Returns the backstack that should be archived by {@link #onSaveInstanceState}. Overriding
   * allows subclasses to handle cases where the current configuration is not one that should
   * survive process death.  The default implementation returns a BackStackToSave that specifies
   * that view state should be persisted.
   *
   * @return the stack to archive, or null to archive nothing
   */
  protected Backstack getBackstackToSave(Backstack backstack) {
    return backstack;
  }

  protected abstract Backstack getColdStartBackstack(@Nullable Backstack restoredBackstack);
}
