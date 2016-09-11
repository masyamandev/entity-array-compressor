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
        int id = cache.getOrDefault(o, -1) + 1;
        os.writeUnsignedLong((long) id);
        if (id == 0) {
            serializer.serialize(o);
            cache.put(o, cache.size());
        }
    }
}
