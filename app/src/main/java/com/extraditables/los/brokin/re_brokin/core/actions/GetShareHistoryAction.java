package com.extraditables.los.brokin.re_brokin.core.actions;

import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock.RemoteStockRepository;
import com.extraditables.los.brokin.re_brokin.core.infrastructure.executor.PostExecutionThread;
import com.extraditables.los.brokin.re_brokin.core.infrastructure.executor.ThreadExecutor;
import javax.inject.Inject;
import rx.Observable;
import yahoofinance.Stock;

public class GetShareHistoryAction implements Action {

  private final ThreadExecutor threadExecutor;
  private final PostExecutionThread postExecutionThread;
  private final RemoteStockRepository remoteStockRepository;
  private String symbol;
  private Callback<Observable<Stock>> callback;

  @Inject public GetShareHistoryAction(ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread, RemoteStockRepository remoteStockRepository) {
    this.threadExecutor = threadExecutor;
    this.postExecutionThread = postExecutionThread;
    this.remoteStockRepository = remoteStockRepository;
  }

  public void getHistory(String symbol, Callback<Observable<Stock>> callback) {
    this.symbol = symbol;
    this.callback = callback;
    threadExecutor.execute(this);
  }

  @Override public void run() {
    this.postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(remoteStockRepository.getHistory(symbol));
      }
    });
  }
}
