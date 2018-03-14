package com.nickwinegar.spacexdemo.di;

import com.nickwinegar.spacexdemo.SpaceXDemoApp;
import com.nickwinegar.spacexdemo.api.SpaceXService;
import com.nickwinegar.spacexdemo.util.ConnectionService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    private SpaceXDemoApp spaceXDemoApp;

    public AppModule(SpaceXDemoApp spaceXDemoApp) {
        this.spaceXDemoApp = spaceXDemoApp;
    }

    @Singleton @Provides
    SpaceXDemoApp providesApplication() {
        return spaceXDemoApp;
    }

    @Singleton @Provides
    ConnectionService provideConnectionService(SpaceXDemoApp spaceXApp) {
        return new ConnectionService(spaceXApp);
    }

    @Singleton @Provides
    SpaceXService provideSpaceXService() {
        OkHttpClient client = getClient();
        return new Retrofit.Builder()
                .baseUrl("https://api.spacexdata.com/v2/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(SpaceXService.class);
    }

    // Launch details rarely change. Cache responses to reduce network requests
    private OkHttpClient getClient() {
        int cacheSize = 5 * 1024 * 1024; // 5 MiB
        Cache cache = new Cache(spaceXDemoApp.getCacheDir(), cacheSize);

        return new OkHttpClient.Builder()
                .cache(cache)
                .build();
    }
}
