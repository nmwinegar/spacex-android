package com.nickwinegar.spacexdemo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nickwinegar.spacexdemo.SpaceXDemoApp;

import javax.inject.Inject;

public class ConnectionService {
    private SpaceXDemoApp application;

    @Inject
    public ConnectionService(SpaceXDemoApp application) {
        this.application = application;
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
