package com.masyaman.datapack.serializers.objects.samples;

public class LatLonTsTz {
    protected LatLon latLon;
    protected TsTz tsTz;

    public LatLonTsTz() {
    }

    public LatLonTsTz(LatLon latLon, TsTz tsTz) {
        this.latLon = latLon;
        this.tsTz = tsTz;
    }

    public LatLon getLatLon() {
        return latLon;
    }

    public void setLatLon(LatLon latLon) {
        this.latLon = latLon;
    }

    public TsTz getTsTz() {
        return tsTz;
    }

    public void setTsTz(TsTz tsTz) {
        this.tsTz = tsTz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LatLonTsTz that = (LatLonTsTz) o;

        if (latLon != null ? !latLon.equals(that.latLon) : that.latLon != null) return false;
        return tsTz != null ? tsTz.equals(that.tsTz) : that.tsTz == null;

    }

    @Override
    public int hashCode() {
        int result = latLon != null ? latLon.hashCode() : 0;
        result = 31 * result + (tsTz != null ? tsTz.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LatLonTsTz{" +
                "latLon=" + latLon +
                ", tsTz=" + tsTz +
                '}';
    }
}
