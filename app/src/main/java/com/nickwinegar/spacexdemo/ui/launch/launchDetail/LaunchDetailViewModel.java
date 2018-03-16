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
import com.nickwinegar.spacexdemo.model.Launchpad;
import com.nickwinegar.spacexdemo.util.ConnectionService;
import com.nickwinegar.spacexdemo.util.SingleLiveEvent;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LaunchDetailViewModel extends AndroidViewModel {
    private static final String highlightImageFormat = "https://i.ytimg.com/vi/%s/hqdefault.jpg";

    @Inject
    SpaceXService spaceXService;
    @Inject
    ConnectionService connectionService;

    private final MutableLiveData<Launch> launch;
    private final MutableLiveData<Launchpad> launchpad;
    private SingleLiveEvent<String> errorMessage;

    public LaunchDetailViewModel(@NonNull Application application) {
        super(application);
        ((SpaceXDemoApp) application).getAppComponent().inject(this);

        launch = new MutableLiveData<>();
        launchpad = new MutableLiveData<>();
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
                        getLaunchHighlightImage(newLaunch);
                        launch.setValue(newLaunch);
                    } else
                        errorMessage.setValue("More than one launch found for that flight number");
                }, error -> {
                    Log.e("SpaceX", error.getMessage());
                    errorMessage.setValue("Error retrieving launch information.");
                });

        return launch;
    }

    LiveData<Launchpad> getLaunchpadDetails(String launchpadId) {
        if (!connectionService.isConnected()) {
            errorMessage.setValue("Unable to get launchpad details, network is unavailable.");
            return launchpad;
        }

        // Sort launches from most recent to oldest
        spaceXService.getLaunchpad(launchpadId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this.launchpad::setValue, error -> {
                    Log.e("SpaceX", error.getMessage());
                    errorMessage.setValue("Error retrieving launchpad information.");
                });

        return launchpad;
    }

    private void getLaunchHighlightImage(Launch highlightLaunch) {
        String videoUrl = highlightLaunch.links.videoUrl;
        if (!videoUrl.isEmpty() && videoUrl.contains("www.youtube.com")) {
            UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(videoUrl);
            String videoId = sanitizer.getValue("v");
            highlightLaunch.links.highlightImageUrl = String.format(highlightImageFormat, videoId);
        }
    }

    public SingleLiveEvent<String> getErrorMessage() {
        return errorMessage;
    }

    Uri getVideoWebUri() {
        String webVideoUriFormat = "http://www.youtube.com/watch?v=%s";
        return getVideoUri(webVideoUriFormat);
    }

    Uri getVideoAppUri() {
        String appVideoUriFormat = "vnd.youtube:%s";
        return getVideoUri(appVideoUriFormat);
    }

    private Uri getVideoUri(String uriFormat) {
        if (launch.getValue() != null) {
            // Verify the launch has a video from YouTube
            if (!launch.getValue().links.videoUrl.isEmpty() && launch.getValue().links.videoUrl.contains("www.youtube.com")) {
                UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(launch.getValue().links.videoUrl);
                String videoId = sanitizer.getValue("v");
                return Uri.parse(String.format(uriFormat, videoId));
            }
        }
        return Uri.EMPTY;
    }

    double getLaunchpadLatitude() {
        if (launchpad.getValue() != null && launchpad.getValue().launchpadLocation != null) {
            return launchpad.getValue().launchpadLocation.latitude;
        }
        throw new IllegalArgumentException("Launch does not have associated latitude");
    }

    double getLaunchpadLongitude() {
        if (launchpad.getValue() != null && launchpad.getValue().launchpadLocation != null) {
            return launchpad.getValue().launchpadLocation.longitude;
        }
        throw new IllegalArgumentException("Launch does not have associated longitude");
    }

    String getOrbitDescription(String orbit) {
        switch (orbit) {
            case "LEO": return "Lower Earth Orbit";
            case "ISS": return "International Space Station";
            case "GTO": return "Geosynchronous Transfer Orbit";
            case "ES-L1": return "Sun-Earth Lagrange 1";
            case "PO": return "Polar Orbit";
            case "SSO": return "Sun-synchronous Orbit";
            default: return orbit;
        }
    }
}
