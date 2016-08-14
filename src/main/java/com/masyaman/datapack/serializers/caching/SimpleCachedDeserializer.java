package com.masyaman.datapack.serializers.caching;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SimpleCachedDeserializer<E> implements Deserializer<E> {

    private DataReader is;
    private Deserializer<E> deserializer;

    private Map<Integer, E> cache = new HashMap<>();

    public SimpleCachedDeserializer(DataReader is, Deserializer<E> deserializer) {
        this.is = is;
        this.deserializer = deserializer;
    }

    @Override
    public E deserialize() throws IOException {
        Long id = is.readUnsignedLong();
        if (id == null) {
            return null;
        }
        E value = cache.get(id.intValue());
        if (value == null) {
            value = deserializer.deserialize();
            cache.put(cache.size(), value);
        }
        return value;
    }
}
