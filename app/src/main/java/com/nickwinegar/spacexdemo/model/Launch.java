package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

public class Launch {
    @SerializedName("flight_number")
    public int flightNumber;
    public String details;
    public Rocket rocket;
    public LaunchLinks links;
    @SerializedName("launch_date_unix")
    public long launchDateTimestamp;
    @SerializedName("launch_success")
    public boolean launchSuccess;
    @SerializedName("launch_site")
    public LaunchSite launchSite;
}
