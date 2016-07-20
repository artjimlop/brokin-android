package com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories;

import rx.Observable;
import yahoofinance.Stock;

public interface StockRepository {

  Observable<Stock> getHistory(String symbol);
}
