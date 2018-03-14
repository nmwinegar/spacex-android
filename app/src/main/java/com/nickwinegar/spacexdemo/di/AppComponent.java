package com.nickwinegar.spacexdemo.di;

import com.nickwinegar.spacexdemo.SpaceXDemoApp;
import com.nickwinegar.spacexdemo.ui.launch.LaunchListViewModel;
import com.nickwinegar.spacexdemo.ui.launch.launchDetail.LaunchDetailViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(SpaceXDemoApp spaceXDemoApp);
    void inject(LaunchListViewModel launchListViewModel);
    void inject(LaunchDetailViewModel launchDetailViewModel);
}
