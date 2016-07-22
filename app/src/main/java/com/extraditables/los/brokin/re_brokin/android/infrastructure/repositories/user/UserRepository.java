package com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.user;

import com.extraditables.los.brokin.brokin_old.models.UserModel;

public interface UserRepository {
  UserModel getCurrentUser();

  void putUser(UserModel userModel);
}
