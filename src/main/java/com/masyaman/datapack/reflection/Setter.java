package com.masyaman.datapack.reflection;

// TODO
public interface Setter {
    void set(Object o, Object value) throws ReflectiveOperationException;
    TypeDescriptor type();
}
