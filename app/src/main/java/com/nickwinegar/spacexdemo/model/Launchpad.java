package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

public class Launchpad {
    public String id;
    @SerializedName("full_name")
    public String fullName;
    @SerializedName("location")
    public LaunchpadLocation launchpadLocation;

    public class LaunchpadLocation {
        public String name;
        public String region;
        public double latitude;
        public double longitude;
    }
}
