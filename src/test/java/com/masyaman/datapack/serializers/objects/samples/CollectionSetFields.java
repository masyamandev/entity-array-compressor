package com.masyaman.datapack.serializers.objects.samples;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class CollectionSetFields {
    protected Set objects;
    protected Set<String> strings;

    public CollectionSetFields() {
    }

    public CollectionSetFields(Collection objects, Collection<String> strings) {
        if (objects != null) {
            this.objects = new HashSet<>(objects);
        }
        if (strings != null) {
            this.strings = new TreeSet<>(strings);
        }
    }

    public Set getObjects() {
        return objects;
    }

    public void setObjects(Set objects) {
        this.objects = objects;
    }

    public Set<String> getStrings() {
        return strings;
    }

    public void setStrings(Set<String> strings) {
        this.strings = strings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollectionSetFields that = (CollectionSetFields) o;

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
