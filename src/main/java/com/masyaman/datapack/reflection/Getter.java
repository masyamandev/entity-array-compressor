package com.masyaman.datapack.reflection;

public interface Getter<T> {
    T get(Object o) throws ReflectiveOperationException;
    TypeDescriptor<?> type();
}
