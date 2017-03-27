package com.companyx.cabservice.resource;

/**
 * Created by sr250345 on 3/27/17.
 */
public class Location {

    private double longitude;
    private double latitude;
    private String name;

    public Location()
    {

    }
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
