package com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock;

import java.io.IOException;
import java.util.Calendar;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

public class ServiceRemoteStockRepository implements RemoteStockRepository {

  @Inject public ServiceRemoteStockRepository() {
  }

  @Override public Observable<Stock> getMonthHistory(final String symbol, final Integer quantity) {
    return Observable.create(new Observable.OnSubscribe<Stock>() {
      @Override public void call(Subscriber<? super Stock> subscriber) {
        try {
          Calendar from = Calendar.getInstance();
          from.add(Calendar.MONTH, -quantity);
          Calendar to = Calendar.getInstance();
          subscriber.onNext(YahooFinance.get(symbol, from, to, Interval.DAILY));
        } catch (IOException e) {
          subscriber.onError(e);
        }
      }
    });
  }

  @Override public Observable<Stock> getWeekHistory(final String symbol) {
    return Observable.create(new Observable.OnSubscribe<Stock>() {
      @Override public void call(Subscriber<? super Stock> subscriber) {
        try {
          Calendar from = Calendar.getInstance();
          from.add(Calendar.HOUR, -(24*7));
          Calendar to = Calendar.getInstance();
          subscriber.onNext(YahooFinance.get(symbol, from, to, Interval.DAILY));
        } catch (IOException e) {
          subscriber.onError(e);
        }
      }
    });
  }
}
