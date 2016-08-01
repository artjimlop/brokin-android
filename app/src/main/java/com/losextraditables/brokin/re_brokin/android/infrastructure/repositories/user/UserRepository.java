package com.losextraditables.brokin.re_brokin.android.infrastructure.repositories.user;

import com.losextraditables.brokin.brokin_old.models.UserModel;

public interface UserRepository {
  UserModel getCurrentUser();

  void putUser(UserModel userModel);
}
