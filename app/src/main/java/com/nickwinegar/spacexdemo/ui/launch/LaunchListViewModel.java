package com.nickwinegar.spacexdemo.ui.launch;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.nickwinegar.spacexdemo.SpaceXDemoApp;
import com.nickwinegar.spacexdemo.api.SpaceXService;
import com.nickwinegar.spacexdemo.model.Launch;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class LaunchListViewModel extends AndroidViewModel {

    @Inject
    SpaceXService spaceXService;

    private final MutableLiveData<List<Launch>> launches;

    public LaunchListViewModel(@NonNull Application application) {
        super(application);
        ((SpaceXDemoApp) application).appComponent.inject(this);
        launches = new MutableLiveData<>();
    }

    LiveData<List<Launch>> getLaunches() {
        spaceXService.getLaunches()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(launches -> {
                    // Sort launches from most recent to oldest
                    Collections.sort(launches, (launch1, launch2) -> Long.compare(launch2.launchDateTimestamp, launch1.launchDateTimestamp));
                    this.launches.setValue(launches);
                });

        return launches;
    }
}
