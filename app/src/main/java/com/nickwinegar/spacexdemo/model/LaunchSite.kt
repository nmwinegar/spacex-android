package com.nickwinegar.spacexdemo.model

import com.google.gson.annotations.SerializedName

data class LaunchSite(@SerializedName("site_id") val id: String,
                      @SerializedName("site_name_long") val name: String)
