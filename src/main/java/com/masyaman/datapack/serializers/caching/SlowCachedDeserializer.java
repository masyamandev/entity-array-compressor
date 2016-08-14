package com.masyaman.datapack.serializers.caching;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SlowCachedDeserializer<E> implements Deserializer<E> {

    private DataReader is;
    private Deserializer<E> deserializer;
    private int cacheSize;

    private LinkedList<E> cache = new LinkedList<>();

    public SlowCachedDeserializer(DataReader is, Deserializer<E> deserializer) throws IOException {
        this.is = is;
        this.deserializer = deserializer;
        cacheSize = is.readUnsignedLong().intValue();
    }

    @Override
    public E deserialize() throws IOException {
        Long id = is.readUnsignedLong();
        if (id == null) {
            return null;
        }
        E value;
        if (id >= cache.size()) {
            value = deserializer.deserialize();
        } else {
            value = cache.remove(id.intValue());
        }
        cache.addFirst(value);
        while (cacheSize > 0 && cache.size() > cacheSize) {
            cache.removeLast();
        }
        return value;
    }
}
