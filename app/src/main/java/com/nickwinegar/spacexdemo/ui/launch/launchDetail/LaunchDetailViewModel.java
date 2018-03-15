package com.nickwinegar.spacexdemo.ui.launch.launchDetail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nickwinegar.spacexdemo.SpaceXDemoApp;
import com.nickwinegar.spacexdemo.api.SpaceXService;
import com.nickwinegar.spacexdemo.model.Launch;
import com.nickwinegar.spacexdemo.util.ConnectionService;
import com.nickwinegar.spacexdemo.util.SingleLiveEvent;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LaunchDetailViewModel extends AndroidViewModel {
    @Inject
    SpaceXService spaceXService;
    @Inject
    ConnectionService connectionService;

    private final MutableLiveData<Launch> launch;
    private SingleLiveEvent<String> errorMessage;

    public LaunchDetailViewModel(@NonNull Application application) {
        super(application);
        ((SpaceXDemoApp) application).getAppComponent().inject(this);

        launch = new MutableLiveData<>();
        errorMessage = new SingleLiveEvent<>();
    }

    LiveData<Launch> getLaunch(int flightNumber) {
        if (!connectionService.isConnected()) {
            errorMessage.setValue("Unable to get launches, network is unavailable.");
            return launch;
        }

        // Sort launches from most recent to oldest
        spaceXService.getLaunch(flightNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(launches -> {
                    if (launches.size() == 1) {
                        Launch newLaunch = launches.get(0);
                        String videoUrl = newLaunch.links.videoUrl;
                        if (!videoUrl.isEmpty() && videoUrl.contains("www.youtube.com")) {
                            UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(videoUrl);
                            String videoId = sanitizer.getValue("v");
                            newLaunch.links.highlightImageUrl = String.format("https://i.ytimg.com/vi/%s/hqdefault.jpg", videoId);
                        }
                        launch.setValue(newLaunch);
                    } else
                        errorMessage.setValue("More than one launch found for that flight number");
                }, error -> {
                    Log.e("SpaceX", error.getMessage());
                    errorMessage.setValue("Error retrieving launch information.");
                });

        return launch;
    }

    public SingleLiveEvent<String> getErrorMessage() {
        return errorMessage;
    }

    Uri getVideoWebUri() {
        if (launch.getValue() != null) {
            if (!launch.getValue().links.videoUrl.isEmpty() && launch.getValue().links.videoUrl.contains("www.youtube.com")) {
                UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(launch.getValue().links.videoUrl);
                String videoId = sanitizer.getValue("v");
                return Uri.parse("http://www.youtube.com/watch?v=" + videoId);
            }
        }
        return Uri.EMPTY;
    }

    Uri getVideoAppUri() {
        if (launch.getValue() != null) {
            if (!launch.getValue().links.videoUrl.isEmpty() && launch.getValue().links.videoUrl.contains("www.youtube.com")) {
                UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(launch.getValue().links.videoUrl);
                String videoId = sanitizer.getValue("v");
                return Uri.parse("vnd.youtube:" + videoId);
            }
        }
        return Uri.EMPTY;
    }
}
