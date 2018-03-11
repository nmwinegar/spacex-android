package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

public class Rocket {
    @SerializedName("rocket_id")
    public String rocketId;
    @SerializedName("rocket_name")
    public String name;
    @SerializedName("second_stage")
    public SecondStage secondStage;

    public class SecondStage {
        public Payload[] payloads;

        public class Payload {
            @SerializedName("payload_id")
            public String name;
            public String[] customers;
        }
    }
}
