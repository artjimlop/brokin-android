package com.extraditables.los.brokin.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.extraditables.los.brokin.R;
import com.extraditables.los.brokin.injector.component.ApplicationComponent;
import com.extraditables.los.brokin.injector.component.DaggerStocksComponent;
import com.extraditables.los.brokin.injector.component.StocksComponent;
import com.extraditables.los.brokin.injector.module.ActivityModule;
import com.extraditables.los.brokin.injector.module.StocksModule;

public class StockInfoActivity extends BaseActivity {

  private StocksComponent stocksComponent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_stock_info);
  }

  @Override protected void initializeInjector(ApplicationComponent applicationComponent) {
    applicationComponent.inject(this);

    stocksComponent = DaggerStocksComponent.builder().applicationComponent(applicationComponent)
        .activityModule(new ActivityModule(this)).stocksModule(new StocksModule()).build();
  }

  public static Intent getCallingIntent(Activity activity, int comicId) {
    return null;
  }
}
