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

    public class LaunchLinks {
        @SerializedName("mission_patch")
        public String patchUrl;
        @SerializedName("video_link")
        public String videoUrl;

        public String highlightImageUrl;
    }

    public class LaunchSite {
        @SerializedName("site_id")
        public String id;
        @SerializedName("site_name_long")
        public String name;
    }
}
