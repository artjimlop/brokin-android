package com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.stock;

import com.losextraditables.brokin.brokin_old.models.UserStockModel;

public interface LocalStockRepository {
  void setStock(UserStockModel userStockModel);

  void remove(UserStockModel stock);

  UserStockModel getStock(Integer id);
}
