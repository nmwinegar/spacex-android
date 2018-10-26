package com.nickwinegar.spacexdemo.model

import com.google.gson.annotations.SerializedName

data class Launchpad(val id: String,
                     @SerializedName("full_name") val fullName: String,
                     @SerializedName("location") val launchpadLocation: LaunchpadLocation) {

    inner class LaunchpadLocation(val name: String, val region: String, val latitude: Double, val longitude: Double)
}
