package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

public class Launchpad {
    private String id;
    @SerializedName("full_name") private String fullName;
    @SerializedName("location") private LaunchpadLocation launchpadLocation;

    public Launchpad(String id, String fullName, LaunchpadLocation launchpadLocation) {
        this.id = id;
        this.fullName = fullName;
        this.launchpadLocation = launchpadLocation;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public LaunchpadLocation getLaunchpadLocation() {
        return launchpadLocation;
    }

    public class LaunchpadLocation {
        private String name;
        private String region;
        private double latitude;
        private double longitude;

        public LaunchpadLocation(String name, String region, double latitude, double longitude) {
            this.name = name;
            this.region = region;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public String getRegion() {
            return region;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}
