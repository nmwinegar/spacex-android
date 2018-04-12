package com.nickwinegar.spacexdemo.ui.launch;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.nickwinegar.spacexdemo.SpaceXDemoApp;
import com.nickwinegar.spacexdemo.api.SpaceXService;
import com.nickwinegar.spacexdemo.model.Launch;
import com.nickwinegar.spacexdemo.util.ConnectionService;
import com.nickwinegar.spacexdemo.util.SingleLiveEvent;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class LaunchListViewModel extends AndroidViewModel {

    @Inject
    public SpaceXService spaceXService;
    @Inject
    public ConnectionService connectionService;

    private final MutableLiveData<List<Launch>> launches;
    private SingleLiveEvent<String> errorMessage;

    public LaunchListViewModel(@NonNull Application application) {
        super(application);
        ((SpaceXDemoApp) application).getAppComponent().inject(this);
        launches = new MutableLiveData<>();
        errorMessage = new SingleLiveEvent<>();
    }

    public LiveData<List<Launch>> getLaunches() {
        return launches;
    }

    public void loadPreviousLaunches() {
        if (!connectionService.isConnected()) {
            errorMessage.setValue("Unable to get launches, network is unavailable.");
            return;
        }

        spaceXService.getLaunches()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(launches -> {
                    // Sort launches from most recent to oldest
                    Collections.sort(launches, (launch1, launch2) -> Long.compare(launch2.getLaunchDateTimestamp(), launch1.getLaunchDateTimestamp()));
                    this.launches.setValue(launches);
                }, error -> errorMessage.setValue("Error retrieving launch information."));
    }

    public void loadUpcomingLaunches() {
        if (!connectionService.isConnected()) {
            errorMessage.setValue("Unable to get launches, network is unavailable.");
            return;
        }

        spaceXService.getUpcomingLaunches()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(launches -> {
                    // Sort launches ascending
                    Collections.sort(launches, (launch1, launch2) -> Long.compare(launch1.getLaunchDateTimestamp(), launch2.getLaunchDateTimestamp()));
                    this.launches.setValue(launches);
                }, error -> errorMessage.setValue("Error retrieving launch information."));
    }

    public SingleLiveEvent<String> getErrorMessage() {
        return errorMessage;
    }
}
