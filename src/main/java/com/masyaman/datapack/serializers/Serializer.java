package com.masyaman.datapack.serializers;

import java.io.IOException;

public interface Serializer<T> {
    void serialize(T o) throws IOException;
}
