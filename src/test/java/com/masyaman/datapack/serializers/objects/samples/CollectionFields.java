package com.masyaman.datapack.serializers.objects.samples;

import java.util.Collection;

public class CollectionFields {
    protected Collection objects;
    protected Collection<String> strings;

    public CollectionFields() {
    }

    public CollectionFields(Collection objects, Collection<String> strings) {
        this.objects = objects;
        this.strings = strings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollectionFields that = (CollectionFields) o;

        if (objects != null ? !objects.equals(that.objects) : that.objects != null) return false;
        return !(strings != null ? !strings.equals(that.strings) : that.strings != null);

    }

    @Override
    public int hashCode() {
        int result = objects != null ? objects.hashCode() : 0;
        result = 31 * result + (strings != null ? strings.hashCode() : 0);
        return result;
    }
}
