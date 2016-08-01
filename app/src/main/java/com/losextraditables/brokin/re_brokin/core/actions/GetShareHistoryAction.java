package com.losextraditables.brokin.re_brokin.core.actions;

import com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.stock.RemoteStockRepository;
import com.losextraditables.brokin.re_brokin.core.infrastructure.executor.PostExecutionThread;
import com.losextraditables.brokin.re_brokin.core.infrastructure.executor.ThreadExecutor;
import com.losextraditables.brokin.re_brokin.core.model.ShareHistoryMode;
import javax.inject.Inject;
import rx.Observable;
import yahoofinance.Stock;

public class GetShareHistoryAction implements Action {

  private final ThreadExecutor threadExecutor;
  private final PostExecutionThread postExecutionThread;
  private final RemoteStockRepository remoteStockRepository;
  private String symbol;
  private Callback<Observable<Stock>> callback;
  private String historyMode;
  private Integer quantity;

  @Inject public GetShareHistoryAction(ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread, RemoteStockRepository remoteStockRepository) {
    this.threadExecutor = threadExecutor;
    this.postExecutionThread = postExecutionThread;
    this.remoteStockRepository = remoteStockRepository;
  }

  public void getHistory(String symbol, String historyMode, Integer quantity, Callback<Observable<Stock>> callback) {
    this.symbol = symbol;
    this.historyMode = historyMode;
    this.quantity = quantity;
    this.callback = callback;
    threadExecutor.execute(this);
  }

  @Override public void run() {
    if (historyMode.equals(ShareHistoryMode.DAY)) {
      notifyLoaded(remoteStockRepository.getWeekHistory(symbol));
    } else {
      notifyLoaded(remoteStockRepository.getMonthHistory(symbol, quantity));
    }
  }

  private void notifyLoaded(final Observable<Stock> history) {
    this.postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(history);
      }
    });
  }
}
