package com.extraditables.los.brokin.re_brokin.android.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import butterknife.ButterKnife;
import com.extraditables.los.brokin.R;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.component.ApplicationComponent;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.component.DaggerStocksComponent;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module.ActivityModule;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module.StockModule;
import com.extraditables.los.brokin.re_brokin.android.presenters.ShareInfoPresenter;
import com.extraditables.los.brokin.re_brokin.android.view.ShareInfoView;
import com.extraditables.los.brokin.re_brokin.android.view.adapters.GraphicAdapter;
import com.robinhood.spark.SparkView;
import javax.inject.Inject;

public class ShareInfoActivity extends BaseActivity implements ShareInfoView {

  @Inject ShareInfoPresenter presenter;

  private static final String EXTRA_SYMBOL = "symbol";

  public static Intent getCallingIntent(Context context, String symbol) {
    Intent intent = new Intent(context, ShareInfoActivity.class);
    intent.putExtra(EXTRA_SYMBOL, symbol);
    return intent;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share_info);
    ButterKnife.bind(this);
    String symbol = getIntent().getStringExtra(EXTRA_SYMBOL);
    initializeViews(symbol);
    initializePresenter(symbol);
  }

  private void initializeViews(String symbol) {
    setUpToolbar(true, symbol);
  }

  private void initializePresenter(String symbol) {
    presenter.initialize(this, symbol);
  }

  @Override protected void initializeInjector(ApplicationComponent applicationComponent) {
    applicationComponent.inject(this);
    DaggerStocksComponent.builder()
        .applicationComponent(applicationComponent)
        .activityModule(new ActivityModule(this))
        .stockModule(new StockModule()).build().inject(this);
  }

  @Override public void showGraphic(float[] data) {
    SparkView graphic = (SparkView) findViewById(R.id.share_graphics_spark);
    graphic.setAdapter(new GraphicAdapter(data));
  }
}
