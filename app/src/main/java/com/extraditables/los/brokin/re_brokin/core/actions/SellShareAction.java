package com.extraditables.los.brokin.re_brokin.core.actions;

import com.extraditables.los.brokin.brokin_old.models.UserModel;
import com.extraditables.los.brokin.brokin_old.models.UserStockModel;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock.LocalStockRepository;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.user.UserRepository;
import com.extraditables.los.brokin.re_brokin.core.infrastructure.executor.PostExecutionThread;
import com.extraditables.los.brokin.re_brokin.core.infrastructure.executor.ThreadExecutor;
import javax.inject.Inject;

public class SellShareAction implements Action {

  private final ThreadExecutor threadExecutor;
  private final PostExecutionThread postExecutionThread;
  private final UserRepository userRepository;
  private final LocalStockRepository localStockRepository;
  private Callback<Void> callback;
  private Integer stockId;

  @Inject public SellShareAction(ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread, UserRepository userRepository,
      LocalStockRepository localStockRepository) {
    this.threadExecutor = threadExecutor;
    this.postExecutionThread = postExecutionThread;
    this.userRepository = userRepository;
    this.localStockRepository = localStockRepository;
  }

  public void sell(Integer stockId, Callback<Void> callback) {
    this.stockId = stockId;
    this.callback = callback;
    threadExecutor.execute(this);
  }

  @Override public void run() {
    UserStockModel userStock = localStockRepository.getStock(stockId);
    UserModel userModel = getUserInfoFromDB();
    float price = userStock.getCurrentValue();
    Float cash = price * userStock.getNumberOfStocks();

    userModel.setCash(userModel.getCash() + cash);

    removeStockFromDB(userStock);
    updateUserInfoInDB(userModel);

    notifySuccess();
  }

  private void updateUserInfoInDB(UserModel userModel) {
    userRepository.putUser(userModel);
  }

  private void removeStockFromDB(UserStockModel userStock) {
    localStockRepository.remove(userStock);
  }

  private UserModel getUserInfoFromDB() {
    return userRepository.getCurrentUser();
  }

  private void notifySuccess() {
    this.postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onComplete();
      }
    });
  }

}
