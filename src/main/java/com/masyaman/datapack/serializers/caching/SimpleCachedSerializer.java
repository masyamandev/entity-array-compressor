package com.masyaman.datapack.serializers.caching;

import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Caching wrapper for Serializer.
 * Data format is:
 * [IndexInCache][Value, optional]
 * If value exists in cache, [IndexInCache] is written as index in cache list + 1, [Value] is omit.
 * If value does not exist in cache, [IndexInCache] is written as 0, then [Value] is written. Value is put to the tail of cache list.
 * This cache does not support size limits.
 */
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
