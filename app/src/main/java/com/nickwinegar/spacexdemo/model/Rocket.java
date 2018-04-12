package com.nickwinegar.spacexdemo.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Rocket {
    @SerializedName("rocket_name") private String name;
    @SerializedName("first_stage") private FirstStage firstStage;
    @SerializedName("second_stage") private SecondStage secondStage;

    public Rocket(String name, FirstStage firstStage, SecondStage secondStage) {
        this.name = name;
        this.firstStage = firstStage;
        this.secondStage = secondStage;
    }

    public String getName() {
        return name;
    }

    public FirstStage getFirstStage() {
        return firstStage;
    }

    public SecondStage getSecondStage() {
        return secondStage;
    }

    public class FirstStage {
        private List<Core> cores;

        public FirstStage(List<Core> cores) {
            this.cores = cores;
        }

        public List<Core> getCores() {
            return cores;
        }

        public class Core {
            @SerializedName("core_serial") private String serial;
            @SerializedName("flight") private int flightCount;
            @SerializedName("land_success") private boolean landingSuccess;

            public Core(String serial, int flightCount, boolean landingSuccess) {
                this.serial = serial;
                this.flightCount = flightCount;
                this.landingSuccess = landingSuccess;
            }

            public String getSerial() {
                return serial;
            }

            public int getFlightCount() {
                return flightCount;
            }

            public boolean isLandingSuccess() {
                return landingSuccess;
            }
        }
    }

    public class SecondStage {
        private List<Payload> payloads;

        public SecondStage(List<Payload> payloads) {
            this.payloads = payloads;
        }

        public List<Payload> getPayloads() {
            return payloads;
        }

        public class Payload {
            @SerializedName("payload_id") private String name;
            private List<String> customers;
            @SerializedName("payload_type") private String payloadType;
            private String orbit;

            public Payload(String name, List<String> customers, String payloadType, String orbit) {
                this.name = name;
                this.customers = customers;
                this.payloadType = payloadType;
                this.orbit = orbit;
            }

            public String getName() {
                return name;
            }

            public List<String> getCustomers() {
                return customers;
            }

            public String getPayloadType() {
                return payloadType;
            }

            public String getOrbit() {
                return orbit;
            }
        }
    }
}
