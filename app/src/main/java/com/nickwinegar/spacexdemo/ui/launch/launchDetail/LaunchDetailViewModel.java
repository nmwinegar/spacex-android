package com.nickwinegar.spacexdemo.ui.launch.launchDetail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.support.annotation.NonNull;

import com.nickwinegar.spacexdemo.R;
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
    private static final String youtubePattern = "www.youtube.com";

    @Inject public SpaceXService spaceXService;
    @Inject public ConnectionService connectionService;

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

    public LiveData<Launch> getLaunch() {
        return launch;
    }

    public SingleLiveEvent<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadPreviousLaunch(int flightNumber) {
        if (!connectionService.isConnected()) {
            errorMessage.setValue(getApplication().getString(R.string.launch_network_unavailable_message));
            return;
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
                }, error -> errorMessage.setValue("Error retrieving launch information."));
    }


    public void loadUpcomingLaunch(int flightNumber) {
        if (!connectionService.isConnected()) {
            errorMessage.setValue(getApplication().getString(R.string.launch_network_unavailable_message));
            return;
        }

        // Sort launches from most recent to oldest
        spaceXService.getUpcomingLaunches()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(launches -> {
                    for (Launch upcomingLaunch : launches) {
                        if (upcomingLaunch.getFlightNumber() == flightNumber) {
                            launch.setValue(upcomingLaunch);
                            return;
                        }
                    }
                }, error -> errorMessage.setValue("Error retrieving launch information."));
    }

    LiveData<Launchpad> getLaunchpadDetails(String launchpadId) {
        if (!connectionService.isConnected()) {
            errorMessage.setValue(getApplication().getString(R.string.launchpad_network_unavailable));
            return launchpad;
        }

        // Sort launches from most recent to oldest
        spaceXService.getLaunchpad(launchpadId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this.launchpad::setValue, error -> errorMessage.setValue("Error retrieving launchpad information."));

        return launchpad;
    }

    private void getLaunchHighlightImage(Launch highlightLaunch) {
        if (highlightLaunch.getLinks() == null || highlightLaunch.getLinks().getVideoUrl() == null) return;

        String videoUrl = highlightLaunch.getLinks().getVideoUrl();
        if (!videoUrl.isEmpty() && videoUrl.contains(youtubePattern)) {
            UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(videoUrl);
            String videoId = sanitizer.getValue("v");
            highlightLaunch.getLinks().setHighlightImageUrl(String.format(highlightImageFormat, videoId));
        }
    }

    public Uri getVideoWebUri() {
        final String webVideoUriFormat = "http://www.youtube.com/watch?v=%s";
        return getVideoUri(webVideoUriFormat);
    }

    public Uri getVideoAppUri() {
        final String appVideoUriFormat = "vnd.youtube:%s";
        return getVideoUri(appVideoUriFormat);
    }

    private Uri getVideoUri(String uriFormat) {
        if (launch.getValue() != null) {
            // Verify the launch has a video from YouTube
            if (!launch.getValue().getLinks().getVideoUrl().isEmpty() && launch.getValue().getLinks().getVideoUrl().contains(youtubePattern)) {
                UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(launch.getValue().getLinks().getVideoUrl());
                String videoId = sanitizer.getValue("v");
                return Uri.parse(String.format(uriFormat, videoId));
            }
        }
        return Uri.EMPTY;
    }

    double getLaunchpadLatitude() {
        if (launchpad.getValue() != null && launchpad.getValue().getLaunchpadLocation() != null) {
            return launchpad.getValue().getLaunchpadLocation().getLatitude();
        }
        throw new IllegalArgumentException("Launch does not have associated latitude");
    }

    double getLaunchpadLongitude() {
        if (launchpad.getValue() != null && launchpad.getValue().getLaunchpadLocation() != null) {
            return launchpad.getValue().getLaunchpadLocation().getLongitude();
        }
        throw new IllegalArgumentException("Launch does not have associated longitude");
    }

    String getOrbitDescription(String orbit) {
        switch (orbit) {
            case "LEO":
                return getApplication().getString(R.string.lower_earth_orbit);
            case "ISS":
                return getApplication().getString(R.string.international_space_station);
            case "GTO":
                return getApplication().getString(R.string.geo_transfer_orbit);
            case "ES-L1":
                return getApplication().getString(R.string.sun_earth_lagrange);
            case "PO":
                return getApplication().getString(R.string.polar_orbit);
            case "SSO":
                return getApplication().getString(R.string.sun_synch_orbit);
            default:
                return orbit;
        }
    }
}
