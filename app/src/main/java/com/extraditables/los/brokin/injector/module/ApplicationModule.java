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
package com.extraditables.los.brokin.injector.module;

import android.content.Context;
import com.extraditables.los.brokin.AndroidApplication;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class ApplicationModule {

    private final AndroidApplication application;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }

    // provide dependencies
    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }
/*

    @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }
    @Provides
    @Singleton
    ComicsRepository provideComicsRepository(ComicsRepositoryImpl comicsRepository) {
        return comicsRepository;
    }

    @Provides
    @Singleton
    @Named("retrofit_comic_datastore")
    ComicDataStore provideRetrofitComicDataStore(RetrofitComicDataStore retrofitComicDataStore) {
        return retrofitComicDataStore;
    }

    @Provides
    @Singleton
    @Named("realm_comic_datastore")
    ComicDataStore provideRealmComicDataStore(RealmComicDataStore realmComicDataStore) {
        return realmComicDataStore;
    }

    @Provides
    @Singleton
    ComicApiService provideComicApiService(AuthInterceptor authInterceptor) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).addInterceptor(authInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.ENDPOINT)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ComicApiService.class);
    }
    */
}
