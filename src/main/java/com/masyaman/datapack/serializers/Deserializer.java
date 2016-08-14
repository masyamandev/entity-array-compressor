package com.masyaman.datapack.serializers;

import java.io.IOException;

public interface Deserializer<T> {
    T deserialize() throws IOException;
}
