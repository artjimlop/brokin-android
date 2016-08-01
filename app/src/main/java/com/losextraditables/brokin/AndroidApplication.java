/**
 * Copyright (C) 2016 Sergi Castillo Open Source Project
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
package com.losextraditables.brokin;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.losextraditables.brokin.re_brokin.android.infrastructure.injector.component.ApplicationComponent;
import com.losextraditables.brokin.re_brokin.android.infrastructure.injector.component.DaggerApplicationComponent;
import com.losextraditables.brokin.re_brokin.android.infrastructure.injector.module.ApplicationModule;
import com.losextraditables.brokin.re_brokin.android.infrastructure.tools.CrashReportTool;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import javax.inject.Inject;

public class AndroidApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "VOXHXZ7tMkJB4XkI66ezpSDs1";
    private static final String TWITTER_SECRET = "Q6SMvgFOhv53UZzcev0XZOkvvWYgRwLYDubK5G8VArIYsI7N1E";


    @Inject CrashReportTool crashReportTool;
    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeInjector();
        initializeBugDetector();
        setupNotificationManager();
    }

    private void setupNotificationManager() {
        component.provideNotificationManager().handleNotificationsNYSE(NOTIFICATION_SERVICE);
    }

    private void initializeBugDetector() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        } else {
            Fabric.with(this, new Twitter(authConfig));
        }
    }

    private void initializeInjector() {
        component = DaggerApplicationComponent.builder()
            .applicationModule(new ApplicationModule(this))
            .build();
    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
