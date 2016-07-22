package com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock;

import java.io.IOException;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class ServiceRemoteStockRepository implements RemoteStockRepository {

  @Inject public ServiceRemoteStockRepository() {
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
