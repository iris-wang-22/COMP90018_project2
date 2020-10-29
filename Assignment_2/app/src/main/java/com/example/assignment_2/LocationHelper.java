package com.example.assignment_2;

public class LocationHelper {

    private double Longitude;
    private double Latitude;
    private String username;

    public LocationHelper(double longitude, double latitude) {
        Longitude = longitude;
        Latitude = latitude;
        this.username = username;
    }

    public double getLongitude() {
        return Longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public String getUsername() {
        return username;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
