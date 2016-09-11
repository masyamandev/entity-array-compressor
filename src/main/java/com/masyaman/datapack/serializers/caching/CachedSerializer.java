package com.masyaman.datapack.serializers.caching;

import com.masyaman.datapack.cache.ObjectIdCache;
import com.masyaman.datapack.cache.ObjectIdCacheRingBuffer;
import com.masyaman.datapack.cache.ObjectIdCacheRingTree;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.LinkedList;

public class CachedSerializer<E> implements Serializer<E> {

    private DataWriter os;
    private Serializer<E> serializer;
    private int cacheSize;

    private ObjectIdCache<E> cache;
    private int uncached = 0b00111111; // TODO 0b01111111

    public CachedSerializer(DataWriter os, Serializer<E> serializer, int cacheSize) throws IOException {
        this.os = os;
        this.serializer = serializer;
        this.cacheSize = cacheSize;
        this.cache = new ObjectIdCacheRingTree<>(cacheSize);
        os.writeUnsignedLong((long) cacheSize);
    }

    @Override
    public void serialize(E o) throws IOException {
        if (o == null) {
           os.writeUnsignedLong(null);
           return;
        }
        while (cache.size() > uncached) {
            uncached <<= 7;
            uncached |= 0xFF;
        }
        int id = cache.removeElement(o);

        if (id >= 0) {
            os.writeUnsignedLong((long) id);
        } else {
            os.writeUnsignedLong((long) uncached);
            serializer.serialize(o);
        }
        cache.addHead(o);
    }
}
