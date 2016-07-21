package com.extraditables.los.brokin.re_brokin.android.presenters;

import com.extraditables.los.brokin.re_brokin.android.view.ShareInfoView;
import com.extraditables.los.brokin.re_brokin.core.actions.Action;
import com.extraditables.los.brokin.re_brokin.core.actions.GetShareHistoryAction;
import java.io.IOException;
import java.math.BigDecimal;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import yahoofinance.Stock;

public class ShareInfoPresenter implements Presenter {

  private final GetShareHistoryAction getShareHistoryAction;
  private ShareInfoView shareInfoView;
  private String symbol;

  @Inject public ShareInfoPresenter(GetShareHistoryAction getShareHistoryAction) {
    this.getShareHistoryAction = getShareHistoryAction;
  }

  public void initialize(final ShareInfoView shareInfoView, String symbol, Boolean sell) {
    this.shareInfoView = shareInfoView;
    this.symbol = symbol;
    setupSharesOption(sell);
    getShareHistoryAction.getHistory(symbol, new Action.Callback<Observable<Stock>>() {
      @Override public void onLoaded(Observable<Stock> observable) {
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Stock>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {

              }

              @Override public void onNext(Stock stock) {
                BigDecimal quote = round(stock.getQuote().getPrice(),2);
                shareInfoView.setQuote(quote);
                BigDecimal chageInPercent = round(stock.getQuote().getChangeInPercent(),2);
                BigDecimal change = stock.getQuote().getChange();
                shareInfoView.setChange(change, chageInPercent);
                setupGraphicsData(stock, shareInfoView);
              }
            });
      }
    });
  }

  private BigDecimal round(BigDecimal d, int decimalPlace) {
    return d.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
  }

  private void setupGraphicsData(Stock stock, ShareInfoView shareInfoView) {
    try {
      float[] data = new float[stock.getHistory().size()];
      for(int i = 0; i < stock.getHistory().size(); i++) {
        data[i] = stock.getHistory().get(i).getClose().floatValue();
      }
      shareInfoView.showGraphic(data);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setupSharesOption(Boolean sell) {
    if (sell) {
      shareInfoView.showSellButton();
    } else {
      shareInfoView.showBuyButton();
    }
  }

  public void buy(Long sharesToBuy) {

  }

  @Override public void resume() {

  }

  @Override public void pause() {

  }

  @Override public void destroy() {

  }
}
