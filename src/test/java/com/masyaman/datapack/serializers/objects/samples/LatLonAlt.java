package com.masyaman.datapack.serializers.objects.samples;

public class LatLonAlt extends LatLon {
    protected double alt;

    public LatLonAlt() {
    }

    public LatLonAlt(double lat, double lon, double alt) {
        super(lat, lon);
        this.alt = alt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        LatLonAlt latLonAlt = (LatLonAlt) o;

        return Double.compare(latLonAlt.alt, alt) == 0;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(alt);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "LatLonAlt{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", alt=" + alt +
                '}';
    }
}
