package com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock;

import rx.Observable;
import yahoofinance.Stock;

public interface RemoteStockRepository {

  Observable<Stock> getHistory(String symbol);
}
