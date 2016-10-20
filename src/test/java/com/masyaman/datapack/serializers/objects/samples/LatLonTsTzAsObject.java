package com.masyaman.datapack.serializers.objects.samples;

import com.masyaman.datapack.annotations.serialization.SerializeBy;
import com.masyaman.datapack.serializers.objects.ObjectSerializationFactory;

public class LatLonTsTzAsObject {
    @SerializeBy(value = ObjectSerializationFactory.class, serializeAs = LatLon.class)
    protected Object latLon;
    protected Object tsTz;

    public LatLonTsTzAsObject() {
    }

    public LatLonTsTzAsObject(LatLon latLon, TsTz tsTz) {
        this.latLon = latLon;
        this.tsTz = tsTz;
    }

    public Object getLatLon() {
        return latLon;
    }

    public void setLatLon(Object latLon) {
        this.latLon = latLon;
    }

    public Object getTsTz() {
        return tsTz;
    }

    public void setTsTz(Object tsTz) {
        this.tsTz = tsTz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LatLonTsTzAsObject that = (LatLonTsTzAsObject) o;

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
        return "LatLonNoAltTsTz{" +
                "latLon=" + latLon +
                ", tsTz=" + tsTz +
                '}';
    }
}
