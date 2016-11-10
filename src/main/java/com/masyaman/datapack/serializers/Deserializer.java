package com.masyaman.datapack.serializers;

import com.masyaman.datapack.reflection.TypeDescriptor;

import java.io.IOException;

public interface Deserializer<E> {
    <T extends E> T deserialize(TypeDescriptor<T> type) throws IOException;
}
