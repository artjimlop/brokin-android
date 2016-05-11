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
package com.extraditables.los.brokin.injector.component;

import com.extraditables.los.brokin.injector.PerActivity;
import com.extraditables.los.brokin.injector.module.ActivityModule;
import com.extraditables.los.brokin.injector.module.StocksModule;
import com.extraditables.los.brokin.views.activity.StockInfoActivity;
import dagger.Component;

/**
 * A scoped {@link PerActivity} component.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, StocksModule.class})
public interface StocksComponent extends ActivityComponent {

    // injections
    void inject(StockInfoActivity stockInfoActivity);
}
