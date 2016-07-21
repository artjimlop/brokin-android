package com.extraditables.los.brokin.re_brokin.android.view.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.extraditables.los.brokin.R;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.component.ApplicationComponent;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.component.DaggerStocksComponent;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module.ActivityModule;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module.StockModule;
import com.extraditables.los.brokin.re_brokin.android.presenters.ShareInfoPresenter;
import com.extraditables.los.brokin.re_brokin.android.view.ShareInfoView;
import com.extraditables.los.brokin.re_brokin.android.view.adapters.GraphicAdapter;
import com.robinhood.spark.SparkView;
import java.math.BigDecimal;
import javax.inject.Inject;

public class ShareInfoActivity extends BaseActivity implements ShareInfoView {

  @Inject ShareInfoPresenter presenter;

  @Bind(R.id.button_buy_shares) TextView buyButton;
  @Bind(R.id.button_sell_shares) TextView sellButton;
  @Bind(R.id.share_quote) TextView shareQuote;
  @Bind(R.id.share_change) TextView shareChange;

  private static final String EXTRA_SYMBOL = "symbol";
  private static final String EXTRA_SELL = "sell";

  public static Intent getCallingIntent(Context context, String symbol, Boolean sell) {
    Intent intent = new Intent(context, ShareInfoActivity.class);
    intent.putExtra(EXTRA_SYMBOL, symbol);
    intent.putExtra(EXTRA_SELL, sell);
    return intent;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share_info);
    ButterKnife.bind(this);
    String symbol = getIntent().getStringExtra(EXTRA_SYMBOL);
    Boolean sell = getIntent().getBooleanExtra(EXTRA_SELL, false);
    initializeViews(symbol);
    initializePresenter(symbol, sell);
  }

  private void initializeViews(String symbol) {
    setUpToolbar(true, symbol);
  }

  private void initializePresenter(String symbol, Boolean sell) {
    presenter.initialize(this, symbol, sell);
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
    if (graphic != null) {
      graphic.setAdapter(new GraphicAdapter(data));
    }
  }

  @Override public void showSellButton() {
    buyButton.setVisibility(View.GONE);
    sellButton.setVisibility(View.VISIBLE);
  }

  @Override public void showBuyButton() {
    buyButton.setVisibility(View.VISIBLE);
    sellButton.setVisibility(View.GONE);
  }

  @Override public void setQuote(BigDecimal quote) {
    shareQuote.setText(getString(R.string.share_info_quote, String.valueOf(quote)));
  }

  @Override public void setChange(BigDecimal change, BigDecimal chageInPercent) {
    shareChange.setText(getString(R.string.share_info_change, String.valueOf(change), String.valueOf(chageInPercent))
    );
  }

  @OnClick(R.id.button_buy_shares) public void onBuyClick() {
    buyStocksAdapter();
  }

  private void buyStocksAdapter() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    final EditText numberOfStocks = new EditText(this);
    numberOfStocks.setInputType(InputType.TYPE_CLASS_NUMBER);
    builder.setView(numberOfStocks);
    builder.setMessage(R.string.alert_buy_title)
        .setPositiveButton(R.string.alert_buy_ok, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            Long sharesToBuy = Long.valueOf(numberOfStocks.getText().toString());
            presenter.buy(sharesToBuy);
          }
        })
        .setNegativeButton(R.string.alert_buy_cancel, null)
        .create()
        .show();
  }
}
