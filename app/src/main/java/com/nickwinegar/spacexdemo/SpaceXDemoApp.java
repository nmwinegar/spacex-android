package com.nickwinegar.spacexdemo;

import android.app.Application;

import com.nickwinegar.spacexdemo.di.AppComponent;
import com.nickwinegar.spacexdemo.di.AppModule;
import com.nickwinegar.spacexdemo.di.DaggerAppComponent;

public class SpaceXDemoApp extends Application {

    public AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);
    }
}
