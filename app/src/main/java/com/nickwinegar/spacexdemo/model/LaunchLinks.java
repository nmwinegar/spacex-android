package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

public class LaunchLinks {
    @SerializedName("mission_patch")
    public String patchUrl;
    @SerializedName("video_link")
    public String videoUrl;

    public String highlightImageUrl;
}
