package com.nickwinegar.spacexdemo.model

import com.google.gson.annotations.SerializedName

data class Rocket(@SerializedName("rocket_name") val name: String,
                  @SerializedName("first_stage") val firstStage: FirstStage,
                  @SerializedName("second_stage") val secondStage: SecondStage) {

    inner class FirstStage(val cores: List<Core>) {

        inner class Core(@SerializedName("core_serial") val serial: String,
                         @SerializedName("flight") val flightCount: Int,
                         @SerializedName("land_success") val isLandingSuccess: Boolean)
    }

    inner class SecondStage(val payloads: List<Payload>) {

        inner class Payload(@SerializedName("payload_id") val name: String,
                            val customers: List<String>,
                            @SerializedName("payload_type") val payloadType: String,
                            val orbit: String)
    }
}
