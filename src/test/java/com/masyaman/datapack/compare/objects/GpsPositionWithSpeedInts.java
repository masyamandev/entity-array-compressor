package com.masyaman.datapack.compare.objects;

import com.univocity.parsers.annotations.Parsed;

public class GpsPositionWithSpeedInts {

    @Parsed // Parse value from CSV
    private int lat;

    @Parsed // Parse value from CSV
    private int lon;

    @Parsed // Parse value from CSV
    private int speed;

    @Parsed // Parse value from CSV
    private long timestamp;

    public GpsPositionWithSpeedInts() {
    }

    public GpsPositionWithSpeedInts(GpsPositionWithSpeed ref) {
        lat = (int) Math.round(ref.getLat() * 1000000);
        lon = (int) Math.round(ref.getLon() * 1000000);
        speed = (int) Math.round(ref.getSpeed() * 10);
        timestamp = ref.getTimestamp();
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GpsPositionWithSpeedInts that = (GpsPositionWithSpeedInts) o;

        if (lat != that.lat) return false;
        if (lon != that.lon) return false;
        if (speed != that.speed) return false;
        return timestamp == that.timestamp;
    }

    @Override
    public int hashCode() {
        int result = lat;
        result = 31 * result + lon;
        result = 31 * result + speed;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
