package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;

import java.io.Closeable;
import java.io.IOException;

public interface ObjectReader extends Closeable {
    Object readObject() throws IOException;
    <T> T readObject(TypeDescriptor<T> type) throws IOException;
    boolean hasObjects() throws IOException;

    <T> Iterable<T> asIterable(Class<T> clazz);
}
