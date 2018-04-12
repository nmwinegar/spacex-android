package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

public class LaunchSite {
    @SerializedName("site_id") private String id;
    @SerializedName("site_name_long") private String name;

    public LaunchSite(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
