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
package com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.component;

import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module.ApplicationModule;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.notifications.NotificationsManager;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock.LocalStockRepository;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.stock.RemoteStockRepository;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.repositories.user.UserRepository;
import com.extraditables.los.brokin.re_brokin.android.view.activities.BaseActivity;
import com.extraditables.los.brokin.re_brokin.core.infrastructure.executor.PostExecutionThread;
import com.extraditables.los.brokin.re_brokin.core.infrastructure.executor.ThreadExecutor;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);

    ThreadExecutor getThreadExecutor();

    PostExecutionThread getPostExecutionThread();

    RemoteStockRepository getStockRepository();

    LocalStockRepository getLocalStockRepository();

    UserRepository getLocalDatabaseUserRepository();

    NotificationsManager provideNotificationManager();
}
