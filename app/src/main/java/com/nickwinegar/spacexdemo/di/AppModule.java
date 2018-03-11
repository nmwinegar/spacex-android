package com.nickwinegar.spacexdemo.di;

import com.nickwinegar.spacexdemo.SpaceXDemoApp;
import com.nickwinegar.spacexdemo.api.SpaceXService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
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
    SpaceXService provideSpaceXService() {
        return new Retrofit.Builder()
                .baseUrl("https://api.spacexdata.com/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(SpaceXService.class);
    }
}
