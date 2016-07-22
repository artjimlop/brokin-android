/**
 * Copyright (C) 2016 Arturo Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module;

import android.content.Context;
import com.extraditables.los.brokin.AndroidApplication;
import com.extraditables.los.brokin.UIThread;
import com.extraditables.los.brokin.brokin_old.db.DatabaseHelper;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock.DatabaseStockRepository;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock.LocalStockRepository;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock.RemoteStockRepository;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock.ServiceRemoteStockRepository;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.user.DatabaseUserRepository;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.user.UserRepository;
import com.extraditables.los.brokin.re_brokin.core.infrastructure.executor.JobExecutor;
import com.extraditables.los.brokin.re_brokin.core.infrastructure.executor.PostExecutionThread;
import com.extraditables.los.brokin.re_brokin.core.infrastructure.executor.ThreadExecutor;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module public class ApplicationModule {

  private final AndroidApplication application;

  public ApplicationModule(AndroidApplication application) {
    this.application = application;
  }

  @Provides @Singleton Context provideContext() {
    return application;
  }

  @Provides @Singleton ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
    return jobExecutor;
  }

  @Provides @Singleton PostExecutionThread providePostExecutionThread(UIThread uiThread) {
    return uiThread;
  }

  @Provides @Singleton RemoteStockRepository provideRemoteStockRepository(
      ServiceRemoteStockRepository serviceStockRepository) {
    return serviceStockRepository;
  }

  @Provides DatabaseHelper provideDatabaseHelper() {
    return OpenHelperManager.getHelper(application, DatabaseHelper.class);
  }

  @Provides @Singleton LocalStockRepository provideLocalStockRepository(
      DatabaseStockRepository databaseStockRepository) {
    return databaseStockRepository;
  }

  @Provides @Singleton UserRepository provideUserRepository(
      DatabaseUserRepository databaseUserRepository) {
    return databaseUserRepository;
  }
}
