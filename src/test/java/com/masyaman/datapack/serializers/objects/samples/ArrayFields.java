package com.masyaman.datapack.serializers.objects.samples;

import java.util.Arrays;

public class ArrayFields {
    protected Object[] objects;
    protected String[] strings;

    public ArrayFields() {
    }

    public ArrayFields(Object[] objects, String[] strings) {
        this.objects = objects;
        this.strings = strings;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }

    public String[] getStrings() {
        return strings;
    }

    public void setStrings(String[] strings) {
        this.strings = strings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayFields that = (ArrayFields) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(objects, that.objects)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(strings, that.strings);

    }

    @Override
    public int hashCode() {
        int result = objects != null ? Arrays.hashCode(objects) : 0;
        result = 31 * result + (strings != null ? Arrays.hashCode(strings) : 0);
        return result;
    }
}
