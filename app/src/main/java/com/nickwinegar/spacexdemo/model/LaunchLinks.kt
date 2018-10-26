package com.nickwinegar.spacexdemo.model

import com.google.gson.annotations.SerializedName

data class LaunchLinks(@SerializedName("mission_patch") val patchUrl: String?,
                       @SerializedName("video_link") val videoUrl: String?,
                       var highlightImageUrl: String?)
