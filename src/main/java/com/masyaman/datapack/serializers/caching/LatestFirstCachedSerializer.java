package com.masyaman.datapack.serializers.caching;

import com.masyaman.datapack.cache.ObjectIdCache;
import com.masyaman.datapack.cache.ObjectIdCacheRingBuffer;
import com.masyaman.datapack.cache.ObjectIdCacheRingTree;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.LinkedList;

public class LatestFirstCachedSerializer<E> implements Serializer<E> {

    private DataWriter os;
    private Serializer<E> serializer;

    private ObjectIdCache<E> cache;

    public LatestFirstCachedSerializer(DataWriter os, Serializer<E> serializer, int cacheSize) throws IOException {
        this.os = os;
        this.serializer = serializer;
        this.cache = new ObjectIdCacheRingTree<>(cacheSize);
        os.writeUnsignedLong((long) cacheSize);
    }

    @Override
    public void serialize(E o) throws IOException {
        if (o == null) {
           os.writeUnsignedLong(null);
           return;
        }
        int id = cache.removeElement(o);

        if (id >= 0) {
            os.writeUnsignedLong((long) id + 1);
        } else {
            os.writeUnsignedLong(0L);
            serializer.serialize(o);
        }
        cache.addHead(o);
    }
}
