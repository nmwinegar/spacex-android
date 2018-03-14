package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

public class Launch {
    @SerializedName("flight_number")
    public int flightNumber;
    public Rocket rocket;
    public LaunchLinks links;
    @SerializedName("launch_date_unix")
    public long launchDateTimestamp;

    public class LaunchLinks {
        @SerializedName("mission_patch")
        public String patchUrl;
        @SerializedName("video_link")
        public String videoUrl;

        public String highlightImageUrl;
    }
}
