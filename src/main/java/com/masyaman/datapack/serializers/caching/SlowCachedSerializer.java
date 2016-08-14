package com.masyaman.datapack.serializers.caching;

import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SlowCachedSerializer<E> implements Serializer<E> {

    private DataWriter os;
    private Serializer<E> serializer;
    private int cacheSize;

    private LinkedList<E> cache = new LinkedList<>();
    private int uncached = 0b00111111; // TODO 0b01111111

    public SlowCachedSerializer(DataWriter os, Serializer<E> serializer, int cacheSize) throws IOException {
        this.os = os;
        this.serializer = serializer;
        this.cacheSize = cacheSize;
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
        int id = cache.indexOf(o); // TODO: remove in 1 pass, use cached set

        if (id >= 0) {
            os.writeUnsignedLong((long) id);
            cache.remove(id);
            cache.addFirst(o);
        } else {
            os.writeUnsignedLong((long) uncached);
            serializer.serialize(o);
            cache.addFirst(o);
        }
        while (cacheSize > 0 && cache.size() > cacheSize) {
            cache.removeLast();
        }
    }
}
