package com.masyaman.datapack.compare.objects;

import com.univocity.parsers.annotations.Parsed;

public class GpsPositionWithSpeed {

    @Parsed // Parse value from CSV
    private double lat;

    @Parsed // Parse value from CSV
    private double lon;

    @Parsed // Parse value from CSV
    private double speed;

    @Parsed // Parse value from CSV
    private long timestamp;


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
