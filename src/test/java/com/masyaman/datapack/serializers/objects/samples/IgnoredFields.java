package com.masyaman.datapack.serializers.objects.samples;

import com.masyaman.datapack.annotations.IgnoredField;

public class IgnoredFields {

    @IgnoredField
    protected String ignored;
    protected String stored;

    public IgnoredFields() {
    }

    public IgnoredFields(String ignored, String stored) {
        this.ignored = ignored;
        this.stored = stored;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IgnoredFields that = (IgnoredFields) o;

        if (ignored != null ? !ignored.equals(that.ignored) : that.ignored != null) return false;
        return !(stored != null ? !stored.equals(that.stored) : that.stored != null);

    }

    @Override
    public int hashCode() {
        int result = ignored != null ? ignored.hashCode() : 0;
        result = 31 * result + (stored != null ? stored.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IgnoredFields{" +
                "ignored='" + ignored + '\'' +
                ", stored='" + stored + '\'' +
                '}';
    }
}
