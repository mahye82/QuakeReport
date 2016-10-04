package com.example.android.quakereport;

/**
 * This class represents an individual earthquake event.
 */
public class Earthquake {
    private String location;
    private double magnitude;
    private long timeInMilliseconds;
    private String url;

    public Earthquake(String location, double magnitude, long timeInMilliseconds, String url) {
        this.location = location;
        this.magnitude = magnitude;
        this.timeInMilliseconds = timeInMilliseconds;
        this.url = url;
    }

    /**
     * Gets the name of the location closest to where the location occurred.
     * @return a String representing the location's name.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the magnitude of the earthquake in terms of the Richter scale.
     * @return a double value representing the earthquake's magnitude.
     */
    public double getMagnitude() {
        return magnitude;
    }

    /**
     * Gets the (Unix) time in milliseconds when the earthquake occurred.
     * @return a long value representing the time when the earthquake occurred.
     */
    public long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

    /**
     * Gets the URL to the map on the USGS Earthquakes website.
     * @return a string value for URL that displays the map/data on the USGS Earthquakes website.
     */
    public String getUrl() {
        return url;
    }
}
