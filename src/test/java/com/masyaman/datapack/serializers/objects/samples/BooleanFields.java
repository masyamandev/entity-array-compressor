package com.masyaman.datapack.serializers.objects.samples;

public class BooleanFields {
    protected boolean primitive;
    protected Boolean wrapper;

    public BooleanFields() {
    }

    public BooleanFields(boolean primitive, Boolean wrapper) {
        this.primitive = primitive;
        this.wrapper = wrapper;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    public Boolean getWrapper() {
        return wrapper;
    }

    public void setWrapper(Boolean wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BooleanFields that = (BooleanFields) o;

        if (primitive != that.primitive) return false;
        return wrapper != null ? wrapper.equals(that.wrapper) : that.wrapper == null;
    }

    @Override
    public int hashCode() {
        int result = (primitive ? 1 : 0);
        result = 31 * result + (wrapper != null ? wrapper.hashCode() : 0);
        return result;
    }
}
