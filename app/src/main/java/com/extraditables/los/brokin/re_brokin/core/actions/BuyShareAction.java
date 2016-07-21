package com.extraditables.los.brokin.re_brokin.core.actions;

import com.extraditables.los.brokin.re_brokin.core.infrastructure.executor.PostExecutionThread;
import com.extraditables.los.brokin.re_brokin.core.infrastructure.executor.ThreadExecutor;
import javax.inject.Inject;

public class BuyShareAction implements Action {

  private final ThreadExecutor threadExecutor;
  private final PostExecutionThread postExecutionThread;

  @Inject public BuyShareAction(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
    this.threadExecutor = threadExecutor;
    this.postExecutionThread = postExecutionThread;
  }

  @Override public void run() {

  }
}
