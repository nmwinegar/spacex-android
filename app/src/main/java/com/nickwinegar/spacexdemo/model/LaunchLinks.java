package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

public class LaunchLinks {
    @SerializedName("mission_patch") private String patchUrl;
    @SerializedName("video_link") private String videoUrl;
    private String highlightImageUrl;

    public LaunchLinks(String patchUrl, String videoUrl, String highlightImageUrl) {
        this.patchUrl = patchUrl;
        this.videoUrl = videoUrl;
        this.highlightImageUrl = highlightImageUrl;
    }

    public String getPatchUrl() {
        return patchUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getHighlightImageUrl() {
        return highlightImageUrl;
    }

    public void setHighlightImageUrl(String highlightImageUrl) {
        this.highlightImageUrl = highlightImageUrl;
    }
}
