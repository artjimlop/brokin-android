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
  private UserStockModel stock;
  private Callback<Void> callback;
  private Integer numberOfStocks;

  @Inject public SellShareAction(ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread, UserRepository userRepository,
      LocalStockRepository localStockRepository) {
    this.threadExecutor = threadExecutor;
    this.postExecutionThread = postExecutionThread;
    this.userRepository = userRepository;
    this.localStockRepository = localStockRepository;
  }

  public void sell(UserStockModel stock, Integer numberOfStocks, Callback<Void> callback) {
    this.stock = stock;
    this.numberOfStocks = numberOfStocks;
    this.callback = callback;
    threadExecutor.execute(this);
  }

  @Override public void run() {
    UserModel userModel = getUserInfoFromDB();
    float price = stock.getCurrentValue();
    Float cash = price * numberOfStocks;

    userModel.setCash(userModel.getCash() + cash);

    removeStockFromDB();
    updateUserInfoInDB(userModel);
  }

  private void updateUserInfoInDB(UserModel userModel) {
    userRepository.putUser(userModel);
  }

  private void removeStockFromDB() {
    localStockRepository.remove(stock);
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

  private void notifyError() {
    this.postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onError();
      }
    });
  }
}
