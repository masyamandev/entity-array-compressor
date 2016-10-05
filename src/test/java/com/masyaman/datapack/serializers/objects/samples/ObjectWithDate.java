package com.masyaman.datapack.serializers.objects.samples;

import com.masyaman.datapack.annotations.SerializeBy;
import com.masyaman.datapack.serializers.dates.DateDiffSerializationFactory;

import java.util.Date;

public class ObjectWithDate {

    private Date date;
    private Object asObject;

    @SerializeBy(DateDiffSerializationFactory.class)
    private Long asLong;
    @SerializeBy(DateDiffSerializationFactory.class)
    private long aslong;

    public ObjectWithDate() {}

    public ObjectWithDate(Date date) {
        this.date = date;
        asObject = date;
        if (date != null) {
            asLong = date.getTime();
            aslong = date.getTime();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectWithDate that = (ObjectWithDate) o;

        if (aslong != that.aslong) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (asObject != null ? !asObject.equals(that.asObject) : that.asObject != null) return false;
        return asLong != null ? asLong.equals(that.asLong) : that.asLong == null;

    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (asObject != null ? asObject.hashCode() : 0);
        result = 31 * result + (asLong != null ? asLong.hashCode() : 0);
        result = 31 * result + (int) (aslong ^ (aslong >>> 32));
        return result;
    }
}
