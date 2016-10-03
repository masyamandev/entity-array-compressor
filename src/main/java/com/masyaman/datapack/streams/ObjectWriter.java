package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;

import java.io.Closeable;
import java.io.IOException;

public interface ObjectWriter extends Closeable {
    <T> void writeObject(T o) throws IOException;
    public <T> void writeObject(T o, TypeDescriptor<T> type) throws IOException;
}
