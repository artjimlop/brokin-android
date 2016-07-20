/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 * @author Fernando Cejas (the android10 coder)
 */
package com.extraditables.los.brokin.re_brokin.core.actions;

public interface Action extends Runnable {

  void run();

  interface Callback<Result> {
    void onLoaded(Result result);
  }

}
