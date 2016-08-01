package com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.stock;

import rx.Observable;
import yahoofinance.Stock;

public interface RemoteStockRepository {

  Observable<Stock> getMonthHistory(String symbol, Integer quantity);

  Observable<Stock> getWeekHistory(String symbol);
}
