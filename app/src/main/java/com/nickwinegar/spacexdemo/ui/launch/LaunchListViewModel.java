package com.nickwinegar.spacexdemo.ui.launch;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.SpaceXDemoApp;
import com.nickwinegar.spacexdemo.api.SpaceXService;
import com.nickwinegar.spacexdemo.model.Launch;
import com.nickwinegar.spacexdemo.util.ConnectionService;
import com.nickwinegar.spacexdemo.util.SingleLiveEvent;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class LaunchListViewModel extends AndroidViewModel {

    @Inject
    public SpaceXService spaceXService;
    @Inject
    public ConnectionService connectionService;

    private final MutableLiveData<List<Launch>> launches;
    private SingleLiveEvent<String> errorMessage;
    private final CompositeDisposable compositeDisposable;

    public LaunchListViewModel(@NonNull Application application) {
        super(application);
        ((SpaceXDemoApp) application).getAppComponent().inject(this);
        launches = new MutableLiveData<>();
        errorMessage = new SingleLiveEvent<>();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<Launch>> getLaunches() {
        return launches;
    }

    public void loadPreviousLaunches() {
        if (!connectionService.isConnected()) {
            errorMessage.setValue(getApplication().getString(R.string.launches_network_unavailable_message));
            return;
        }

        Disposable previousLaunchSubscription = spaceXService.getLaunches()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(launches -> {
                    // Sort launches from most recent to oldest
                    Collections.sort(launches, (launch1, launch2) -> Long.compare(launch2.getLaunchDateTimestamp(), launch1.getLaunchDateTimestamp()));
                    this.launches.setValue(launches);
                }, error -> errorMessage.setValue(getApplication().getString(R.string.launch_retrieval_error)));
        compositeDisposable.add(previousLaunchSubscription);
    }

    public void loadUpcomingLaunches() {
        if (!connectionService.isConnected()) {
            errorMessage.setValue(getApplication().getString(R.string.launches_network_unavailable_message));
            return;
        }

        Disposable upcomingLaunchSubscription = spaceXService.getUpcomingLaunches()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(launches -> {
                    // Sort launches ascending
                    Collections.sort(launches, (launch1, launch2) -> Long.compare(launch1.getLaunchDateTimestamp(), launch2.getLaunchDateTimestamp()));
                    this.launches.setValue(launches);
                }, error -> errorMessage.setValue(getApplication().getString(R.string.launch_retrieval_error)));
        compositeDisposable.add(upcomingLaunchSubscription);
    }

    public SingleLiveEvent<String> getErrorMessage() {
        return errorMessage;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
    }
}
