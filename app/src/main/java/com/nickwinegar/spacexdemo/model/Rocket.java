package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Rocket {
    @SerializedName("rocket_name")
    public String name;
    @SerializedName("first_stage")
    public FirstStage firstStage;
    @SerializedName("second_stage")
    public SecondStage secondStage;

    public class FirstStage {
        public List<Core> cores;

        public class Core {
            @SerializedName("core_serial")
            public String serial;
            @SerializedName("flight")
            public int flightCount;
            @SerializedName("land_success")
            public boolean landingSuccess;
        }
    }

    public class SecondStage {
        public List<Payload> payloads;

        public class Payload {
            @SerializedName("payload_id")
            public String name;
            public List<String> customers;
            @SerializedName("payload_type")
            public String payloadType;
            public String orbit;
        }
    }
}
