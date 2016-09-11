package com.masyaman.datapack.reflection;

public interface Setter {
    void set(Object o, Object value) throws ReflectiveOperationException;
    TypeDescriptor type();
}
