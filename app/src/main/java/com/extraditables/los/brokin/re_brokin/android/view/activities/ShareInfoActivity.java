package com.extraditables.los.brokin.re_brokin.android.view.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.extraditables.los.brokin.R;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.NestedListView;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.component.ApplicationComponent;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.component.DaggerStocksComponent;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module.ActivityModule;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module.StockModule;
import com.extraditables.los.brokin.re_brokin.android.presenters.ShareInfoPresenter;
import com.extraditables.los.brokin.re_brokin.android.view.ShareInfoView;
import com.extraditables.los.brokin.re_brokin.android.view.adapters.GraphicAdapter;
import com.robinhood.spark.SparkView;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import java.math.BigDecimal;
import javax.inject.Inject;

public class ShareInfoActivity extends BaseActivity implements ShareInfoView {

  @Inject ShareInfoPresenter presenter;

  @Bind(R.id.button_buy_shares) TextView buyButton;
  @Bind(R.id.button_sell_shares) TextView sellButton;
  @Bind(R.id.share_quote) TextView shareQuote;
  @Bind(R.id.share_change) TextView shareChange;
  @Bind(R.id.tweets_feed) NestedListView tweets;
  @Bind(R.id.share_graphics_spark) SparkView graphic;
  @Bind(R.id.share_info_week) TextView oneDayTextView;
  @Bind(R.id.share_info_month) TextView oneMonthTextView;
  @Bind(R.id.share_info_three_month) TextView threeMonthTextView;
  @Bind(R.id.share_info_six_month) TextView sixMonthTextView;
  @Bind(R.id.share_info_year) TextView aYearTextView;

  private static final String EXTRA_SYMBOL = "symbol";
  private static final String EXTRA_SELL = "sell";
  private static final String EXTRA_USER_STOCK_ID = "user_stock_id";
  private String quoteValue;

  public static Intent getCallingIntent(Context context, String symbol, Boolean sell) {
    Intent intent = new Intent(context, ShareInfoActivity.class);
    intent.putExtra(EXTRA_SYMBOL, symbol);
    intent.putExtra(EXTRA_SELL, sell);
    return intent;
  }

  public static Intent getCallingIntent(Context context, String symbol, Integer userStockId,
      Boolean sell) {
    Intent intent = new Intent(context, ShareInfoActivity.class);
    intent.putExtra(EXTRA_SYMBOL, symbol);
    intent.putExtra(EXTRA_SELL, sell);
    intent.putExtra(EXTRA_USER_STOCK_ID, userStockId);
    return intent;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share_info_content);
    ButterKnife.bind(this);
    String symbol = getIntent().getStringExtra(EXTRA_SYMBOL);
    Boolean sell = getIntent().getBooleanExtra(EXTRA_SELL, false);
    Integer userStockId = getIntent().getIntExtra(EXTRA_USER_STOCK_ID, 0);
    initializeViews(symbol);
    initializePresenter(symbol, userStockId, sell);

    final SearchTimeline timeline = new SearchTimeline.Builder()
        .query("$"+symbol)
        .build();
    final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
        .setTimeline(timeline)
        .build();

    tweets.setAdapter(adapter);

    oneMonthTextView.setTextColor(getResources().getColor(R.color.black));
  }

  private void initializeViews(String symbol) {
    setUpToolbar(true, symbol);
  }

  private void initializePresenter(String symbol, Integer userStockId, Boolean sell) {
    presenter.initialize(this, symbol, userStockId, sell);
  }

  @Override protected void initializeInjector(ApplicationComponent applicationComponent) {
    applicationComponent.inject(this);
    DaggerStocksComponent.builder()
        .applicationComponent(applicationComponent)
        .activityModule(new ActivityModule(this))
        .stockModule(new StockModule()).build().inject(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void showGraphic(float[] data) {
    graphic.setAdapter(new GraphicAdapter(data));
    graphic.setScrubListener(new SparkView.OnScrubListener() {
      @Override
      public void onScrubbed(Object value) {
        if (value == null) {
          shareQuote.setText(quoteValue);
        } else {
          shareQuote.setText(getString(R.string.share_info_quote, String.valueOf(value)));
        }
      }
    });
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
    quoteValue = getString(R.string.share_info_quote, String.valueOf(quote));
    shareQuote.setText(quoteValue);
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

  @OnClick(R.id.button_sell_shares) public void onSellClick() {
    sellStocksAdapter();
  }

  private void sellStocksAdapter() {
    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
    builder.setMessage(getString(R.string.share_info_sell_description))
        .setPositiveButton(getString(R.string.share_info_sell_ok), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            presenter.sell();
          }
        })
        .setNegativeButton(getString(R.string.share_info_sell_cancel), null).create().show();
  }

  @OnClick(R.id.share_info_week) public void dayHistoryClicked() {
    presenter.onWeekHistoryClicked();
    oneDayTextView.setTextColor(getResources().getColor(R.color.black));
    oneMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    threeMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    sixMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    aYearTextView.setTextColor(getResources().getColor(R.color.primary));
  }

  @OnClick(R.id.share_info_month) public void monthHistoryClicked() {
    presenter.onMonthHistoryClicked();
    oneDayTextView.setTextColor(getResources().getColor(R.color.primary));
    oneMonthTextView.setTextColor(getResources().getColor(R.color.black));
    threeMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    sixMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    aYearTextView.setTextColor(getResources().getColor(R.color.primary));
  }

  @OnClick(R.id.share_info_three_month) public void threeMonthHistoryClicked() {
    presenter.onThreeHistoryClicked();
    oneDayTextView.setTextColor(getResources().getColor(R.color.primary));
    oneMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    threeMonthTextView.setTextColor(getResources().getColor(R.color.black));
    sixMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    aYearTextView.setTextColor(getResources().getColor(R.color.primary));
  }

  @OnClick(R.id.share_info_six_month) public void sixMonthHistoryClicked() {
    presenter.onSixHistoryClicked();
    oneDayTextView.setTextColor(getResources().getColor(R.color.primary));
    oneMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    threeMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    sixMonthTextView.setTextColor(getResources().getColor(R.color.black));
    aYearTextView.setTextColor(getResources().getColor(R.color.primary));
  }

  @OnClick(R.id.share_info_year) public void yearHistoryClicked() {
    presenter.onYearHistoryClicked();
    oneDayTextView.setTextColor(getResources().getColor(R.color.primary));
    oneMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    threeMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    sixMonthTextView.setTextColor(getResources().getColor(R.color.primary));
    aYearTextView.setTextColor(getResources().getColor(R.color.black));
  }
}
