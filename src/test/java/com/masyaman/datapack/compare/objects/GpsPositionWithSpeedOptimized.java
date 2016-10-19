package com.masyaman.datapack.compare.objects;

import com.masyaman.datapack.annotations.serialization.Precision;
import com.masyaman.datapack.annotations.serialization.SerializeBy;
import com.masyaman.datapack.serializers.numbers.NumberLinearSerializationFactory;
import com.univocity.parsers.annotations.Parsed;

public class GpsPositionWithSpeedOptimized {

    @Parsed // Parse value from CSV
    @SerializeBy(NumberLinearSerializationFactory.class)
    private double lat;

    @Parsed // Parse value from CSV
    @SerializeBy(NumberLinearSerializationFactory.class)
    private double lon;

    @Parsed // Parse value from CSV
    @Precision(1)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GpsPositionWithSpeedOptimized that = (GpsPositionWithSpeedOptimized) o;

        if (Double.compare(that.lat, lat) != 0) return false;
        if (Double.compare(that.lon, lon) != 0) return false;
        if (Double.compare(that.speed, speed) != 0) return false;
        return timestamp == that.timestamp;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(speed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "GpsPositionWithSpeedOptimized{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", speed=" + speed +
                ", timestamp=" + timestamp +
                '}';
    }
}
