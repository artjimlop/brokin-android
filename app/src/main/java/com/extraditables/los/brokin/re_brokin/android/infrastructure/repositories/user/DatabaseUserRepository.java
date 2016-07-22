package com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.user;

import com.extraditables.los.brokin.brokin_old.db.DatabaseHelper;
import com.extraditables.los.brokin.brokin_old.models.UserModel;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.tools.CrashReportTool;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class DatabaseUserRepository implements UserRepository {

  private final DatabaseHelper databaseHelper;
  private final CrashReportTool crashReportTool;

  @Inject public DatabaseUserRepository(DatabaseHelper databaseHelper,
      CrashReportTool crashReportTool) {
    this.databaseHelper = databaseHelper;
    this.crashReportTool = crashReportTool;
  }

  @Override public UserModel getCurrentUser() {
    UserModel userModel = new UserModel();
    try {
      List<UserModel> userModels = databaseHelper.getUserModelDao().queryForAll();
      if (userModels != null && !userModels.isEmpty()) {
        userModel = userModels.get(0);
      }
      OpenHelperManager.releaseHelper();
    } catch (SQLException e) {
      crashReportTool.logException(e);
    }
    return userModel;
  }

  @Override public void putUser(UserModel userModel) {
    Dao dao;
    try {
      dao = databaseHelper.getUserModelDao();
      dao.update(userModel);
    } catch (SQLException e) {
      crashReportTool.logException(e);
    }
    OpenHelperManager.releaseHelper();
  }
}
