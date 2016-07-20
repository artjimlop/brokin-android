package com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories;

import java.io.IOException;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class ServiceStockRepository implements StockRepository {

  @Inject public ServiceStockRepository() {
  }

  @Override public Observable<Stock> getHistory(final String symbol) {
    return Observable.create(new Observable.OnSubscribe<Stock>() {
      @Override public void call(Subscriber<? super Stock> subscriber) {
        try {
          subscriber.onNext(YahooFinance.get(symbol, true));
        } catch (IOException e) {
          subscriber.onError(e);
        }
      }
    });
  }
}
