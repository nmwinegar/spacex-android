package com.nickwinegar.spacexdemo.model

import com.google.gson.annotations.SerializedName

data class Launch(@SerializedName("flight_number") val flightNumber: Int,
                  val details: String,
                  val rocket: Rocket,
                  val links: LaunchLinks,
                  @SerializedName("launch_date_unix") val launchDateTimestamp: Long,
                  @SerializedName("launch_success") val isLaunchSuccess: Boolean,
                  @SerializedName("launch_site") val launchSite: LaunchSite)
