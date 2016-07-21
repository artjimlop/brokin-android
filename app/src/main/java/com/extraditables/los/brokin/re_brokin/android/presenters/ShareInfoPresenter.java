package com.extraditables.los.brokin.re_brokin.android.presenters;

import com.extraditables.los.brokin.re_brokin.android.view.ShareInfoView;
import com.extraditables.los.brokin.re_brokin.core.actions.Action;
import com.extraditables.los.brokin.re_brokin.core.actions.GetShareHistoryAction;
import java.io.IOException;
import java.util.ArrayList;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

public class ShareInfoPresenter implements Presenter {

  private final GetShareHistoryAction getShareHistoryAction;
  private ShareInfoView shareInfoView;
  private String symbol;

  @Inject public ShareInfoPresenter(GetShareHistoryAction getShareHistoryAction) {
    this.getShareHistoryAction = getShareHistoryAction;
  }

  public void initialize(final ShareInfoView shareInfoView, String symbol) {
    this.shareInfoView = shareInfoView;
    this.symbol = symbol;
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
            });
      }
    });
  }

  @Override public void resume() {

  }

  @Override public void pause() {

  }

  @Override public void destroy() {

  }
}
