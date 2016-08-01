package com.losextraditables.brokin.re_brokin.core.actions;

import com.losextraditables.brokin.brokin_old.models.UserModel;
import com.losextraditables.brokin.brokin_old.models.UserStockModel;
import com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.stock.LocalStockRepository;
import com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.user.UserRepository;
import com.losextraditables.brokin.re_brokin.core.infrastructure.executor.PostExecutionThread;
import com.losextraditables.brokin.re_brokin.core.infrastructure.executor.ThreadExecutor;
import javax.inject.Inject;
import yahoofinance.Stock;

public class BuyShareAction implements Action {

  private final ThreadExecutor threadExecutor;
  private final PostExecutionThread postExecutionThread;
  private final UserRepository userRepository;
  private final LocalStockRepository localStockRepository;
  private Stock stock;
  private Long moneyToSpend;
  private Callback<Void> callback;

  @Inject public BuyShareAction(ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread, UserRepository userRepository,
      LocalStockRepository localStockRepository) {
    this.threadExecutor = threadExecutor;
    this.postExecutionThread = postExecutionThread;
    this.userRepository = userRepository;
    this.localStockRepository = localStockRepository;
  }

  public void buy(Stock stock, Long money, Callback<Void> callback) {
    this.stock = stock;
    this.moneyToSpend = money;
    this.callback = callback;
    threadExecutor.execute(this);
  }

  @Override public void run() {
    UserModel userModel = getUserInfoFromDB();
    float price = stock.getQuote().getPrice().floatValue();
    float stocksToBuy = moneyToSpend / price;
    Integer userCash = userModel.getCash().intValue();
    if (userCash > 0) {
      if(stocksToBuy > 0) {
        userModel.setCash(userCash - stocksToBuy * price);
        UserStockModel
            userStockModel = createStockModel(userModel, stock, stocksToBuy);
        insertStockInDB(userStockModel);
        updateUserInfoInDB(userModel);

        notifySuccess();
      }

    } else {
      notifyError();
    }
  }

  private void updateUserInfoInDB(UserModel userModel) {
    userRepository.putUser(userModel);
  }

  private UserModel getUserInfoFromDB() {
    return userRepository.getCurrentUser();
  }

  private UserStockModel createStockModel(UserModel userModel, Stock stockModel, float stocksToBuy) {
    UserStockModel userStockModel = new UserStockModel();
    userStockModel.setName(stockModel.getName());
    userStockModel.setValue(stockModel.getQuote().getPrice().floatValue());
    userStockModel.setCurrentValue(stockModel.getQuote().getPrice().floatValue());
    userStockModel.setSymbol(stockModel.getSymbol());
    userStockModel.setUserId(userModel.getUserId());
    userStockModel.setNumberOfStocks((long) stocksToBuy);
    return userStockModel;
  }

  private void insertStockInDB(UserStockModel userStockModel) {
    localStockRepository.setStock(userStockModel);
  }

  private void notifySuccess() {
    this.postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onComplete();
      }
    });
  }

  private void notifyError() {
    this.postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onError();
      }
    });
  }
}
