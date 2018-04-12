package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

public class Launch {
    @SerializedName("flight_number") private int flightNumber;
    private String details;
    private Rocket rocket;
    private LaunchLinks links;
    @SerializedName("launch_date_unix") private long launchDateTimestamp;
    @SerializedName("launch_success") private boolean launchSuccess;
    @SerializedName("launch_site") private LaunchSite launchSite;

    public Launch(int flightNumber, String details, Rocket rocket, LaunchLinks links, long launchDateTimestamp, boolean launchSuccess, LaunchSite launchSite) {
        this.flightNumber = flightNumber;
        this.details = details;
        this.rocket = rocket;
        this.links = links;
        this.launchDateTimestamp = launchDateTimestamp;
        this.launchSuccess = launchSuccess;
        this.launchSite = launchSite;
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    public String getDetails() {
        return details;
    }

    public Rocket getRocket() {
        return rocket;
    }

    public LaunchLinks getLinks() {
        return links;
    }

    public long getLaunchDateTimestamp() {
        return launchDateTimestamp;
    }

    public boolean isLaunchSuccess() {
        return launchSuccess;
    }

    public LaunchSite getLaunchSite() {
        return launchSite;
    }
}
