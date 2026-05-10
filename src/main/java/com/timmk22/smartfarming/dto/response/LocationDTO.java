package com.timmk22.smartfarming.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationDTO {
    @JsonProperty("display_name")
    private String displayName;
    private String lat;
    private String lon;


    public LocationDTO() {
        // default constructor for Jackson
    }

    public LocationDTO(String displayName, String lat, String lon) {
        this.displayName = displayName;
        this.lat = lat;
        this.lon = lon;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}