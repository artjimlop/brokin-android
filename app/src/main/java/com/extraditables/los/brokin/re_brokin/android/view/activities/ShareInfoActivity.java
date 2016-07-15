package com.extraditables.los.brokin.re_brokin.android.view.activities;

import android.os.Bundle;
import com.extraditables.los.brokin.R;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.component.ApplicationComponent;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.component.DaggerStocksComponent;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module.ActivityModule;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module.StockModule;

public class ShareInfoActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share_info);
  }

  @Override protected void initializeInjector(ApplicationComponent applicationComponent) {
    applicationComponent.inject(this);
    DaggerStocksComponent.builder()
        .applicationComponent(applicationComponent)
        .activityModule(new ActivityModule(this))
        .stockModule(new StockModule()).build().inject(this);
  }
}
