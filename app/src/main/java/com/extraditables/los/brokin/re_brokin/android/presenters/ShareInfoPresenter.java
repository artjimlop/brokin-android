package com.extraditables.los.brokin.re_brokin.android.presenters;

import android.util.Log;
import com.extraditables.los.brokin.re_brokin.android.view.ShareInfoView;
import com.extraditables.los.brokin.re_brokin.core.actions.Action;
import com.extraditables.los.brokin.re_brokin.core.actions.BuyShareAction;
import com.extraditables.los.brokin.re_brokin.core.actions.GetShareHistoryAction;
import com.extraditables.los.brokin.re_brokin.core.actions.SellShareAction;
import com.extraditables.los.brokin.re_brokin.core.model.ShareHistoryMode;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

public class ShareInfoPresenter implements Presenter {

  private final GetShareHistoryAction getShareHistoryAction;
  private final BuyShareAction buyShareAction;
  private final SellShareAction sellShareAction;
  private ShareInfoView shareInfoView;
  private String symbol;
  private Stock share;
  private Integer userStockId;

  @Inject public ShareInfoPresenter(GetShareHistoryAction getShareHistoryAction,
      BuyShareAction buyShareAction, SellShareAction sellShareAction) {
    this.getShareHistoryAction = getShareHistoryAction;
    this.buyShareAction = buyShareAction;
    this.sellShareAction = sellShareAction;
  }

  public void initialize(final ShareInfoView shareInfoView, String symbol, Integer userStockId,
      Boolean sell) {
    this.shareInfoView = shareInfoView;
    this.symbol = symbol;
    this.userStockId = userStockId;
    setupSharesOption(sell);
    loadMonthlyGraphics(shareInfoView, symbol, 1);
  }

  private void loadMonthlyGraphics(final ShareInfoView shareInfoView, String symbol, int quantity) {
    getShareHistoryAction.getHistory(symbol, ShareHistoryMode.MONTH, quantity, new Action.Callback<Observable<Stock>>() {
      @Override public void onLoaded(Observable<Stock> observable) {
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Stock>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {

              }

              @Override public void onNext(Stock stock) {
                setShare(stock);
                setupQuote(stock, shareInfoView);
                setupChange(stock, shareInfoView);
                setupGraphicsData(stock, shareInfoView);
              }
            });
      }

      @Override public void onComplete() {

      }

      @Override public void onError() {

      }
    });
  }

  private void setShare(Stock stock) {
    share = stock;
  }

  private void setupChange(Stock stock, ShareInfoView shareInfoView) {
    BigDecimal chageInPercent = round(stock.getQuote().getChangeInPercent(),2);
    BigDecimal change = stock.getQuote().getChange();
    shareInfoView.setChange(change, chageInPercent);
  }

  private void setupQuote(Stock stock, ShareInfoView shareInfoView) {
    BigDecimal quote = round(stock.getQuote().getPrice(),2);
    shareInfoView.setQuote(quote);
  }

  private BigDecimal round(BigDecimal d, int decimalPlace) {
    return d.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
  }

  private void setupGraphicsData(Stock stock, ShareInfoView shareInfoView) {
    try {
      List<HistoricalQuote> history = stock.getHistory();
      float[] data = new float[history.size()];
      for(int i = 0; i < history.size(); i++) {
        data[i] = history.get(i).getClose().floatValue();
      }
      if (data != null) {
        shareInfoView.showGraphic(data);
      }
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
    buyShareAction.buy(share, sharesToBuy, new Action.Callback<Void>() {
      @Override public void onLoaded(Void aVoid) {

      }

      @Override public void onComplete() {
        Log.d("COMPRADA!", "yatusae");
      }

      @Override public void onError() {
        //TODO implement an error bundle
      }
    });
  }

  public void sell() {
    sellShareAction.sell(userStockId, new Action.Callback<Void>() {
      @Override public void onLoaded(Void aVoid) {

      }

      @Override public void onComplete() {
        Log.d("Vendida!", "yatusae");
      }

      @Override public void onError() {
        //TODO implement an error bundle
      }
    });
  }

  @Override public void resume() {

  }

  @Override public void pause() {

  }

  @Override public void destroy() {

  }

  public void onWeekHistoryClicked() {
    getShareHistoryAction.getHistory(symbol, ShareHistoryMode.DAY, 7, new Action.Callback<Observable<Stock>>() {
      @Override public void onLoaded(Observable<Stock> observable) {
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Stock>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {

              }

              @Override public void onNext(Stock stock) {
                setShare(stock);
                setupQuote(stock, shareInfoView);
                setupChange(stock, shareInfoView);
                setupGraphicsData(stock, shareInfoView);
              }
            });
      }

      @Override public void onComplete() {

      }

      @Override public void onError() {

      }
    });
  }

  public void onMonthHistoryClicked() {
    loadMonthlyGraphics(shareInfoView, symbol, 1);
  }

  public void onThreeHistoryClicked() {
    loadMonthlyGraphics(shareInfoView, symbol, 3);
  }

  public void onSixHistoryClicked() {
    loadMonthlyGraphics(shareInfoView, symbol, 6);
  }

  public void onYearHistoryClicked() {
    loadMonthlyGraphics(shareInfoView, symbol, 12);
  }
}
