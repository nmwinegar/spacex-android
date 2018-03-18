package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

public class LaunchSite {
    @SerializedName("site_id")
    public String id;
    @SerializedName("site_name_long")
    public String name;
}
