package com.masyaman.datapack.serializers.caching;

import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SimpleCachedSerializer<E> implements Serializer<E> {

    private DataWriter os;
    private Serializer<E> serializer;

    private Map<E, Integer> cache = new HashMap<>();
    private int uncached = 0b00111111; // TODO 0b01111111

    public SimpleCachedSerializer(DataWriter os, Serializer<E> serializer) {
        this.os = os;
        this.serializer = serializer;
    }

    @Override
    public void serialize(E o) throws IOException {
        if (o == null) {
           os.writeUnsignedLong(null);
           return;
        }
        while (cache.size() >= uncached) {
            uncached <<= 7;
            uncached |= 0xFF;
        }
        int id = cache.getOrDefault(o, uncached);
        os.writeUnsignedLong((long) id);
        if (id >= cache.size()) {
            serializer.serialize(o);
            cache.put(o, cache.size());
        }
    }
}
