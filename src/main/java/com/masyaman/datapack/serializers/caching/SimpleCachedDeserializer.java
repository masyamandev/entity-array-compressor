package com.masyaman.datapack.serializers.caching;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.*;

/**
 * Caching wrapper for Deserializer.
 * For data format see {@link SimpleCachedSerializer}
 */
public class SimpleCachedDeserializer<E> implements Deserializer<E> {

    private DataReader is;
    private Deserializer<E> deserializer;
    private int cacheSize;

    private List<ElementWithVersion<E>> cache = new ArrayList<>();
    private TreeSet<ElementWithVersion<E>> versions;
    private long version = 0L;

    public SimpleCachedDeserializer(DataReader is, Deserializer<E> deserializer, int cacheSize) {
        this.is = is;
        this.deserializer = deserializer;
        this.cacheSize = cacheSize;
        if (cacheSize > 0) {
            versions = new TreeSet<>();
        }
    }

    @Override
    public <T extends E> T deserialize(TypeDescriptor<T> type) throws IOException {
        Long id = is.readUnsignedLong();
        if (id == null) {
            return null;
        }
        version++;
        int index = id.intValue() - 1;
        ElementWithVersion<E> value;
        if (index < 0 || index >= cache.size()) {
            T deserialized = deserializer.deserialize(type);
            if (versions == null || cache.size() < cacheSize) {
                value = new ElementWithVersion<>(deserialized, version);
                cache.add(value);
            } else {
                value = versions.pollFirst();
                value.element = deserialized;
                value.version = version;
            }
            if (versions != null) {
                versions.add(value);
            }
        } else {
            value = cache.get(index);
            if (versions != null) {
                versions.remove(value);
                value.version = version;
                versions.add(value);
            }
        }
        return (T) value.element;
    }

    private static class ElementWithVersion<E> implements Comparable<ElementWithVersion> {
        private E element;
        private long version;

        public ElementWithVersion(E element, long version) {
            this.element = element;
            this.version = version;
        }

        @Override
        public int compareTo(ElementWithVersion o) {
            return Long.compare(version, o.version);
        }

        @Override
        public String toString() {
            return "element=" + element + ", version=" + version;
        }
    }
}
