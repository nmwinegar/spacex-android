package com.nickwinegar.spacexdemo.di;

import com.nickwinegar.spacexdemo.SpaceXDemoApp;
import com.nickwinegar.spacexdemo.ui.launch.LaunchListActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(SpaceXDemoApp spaceXDemoApp);
    void inject(LaunchListActivity launchListActivity);
}
