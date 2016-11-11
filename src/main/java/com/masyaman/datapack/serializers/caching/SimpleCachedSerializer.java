package com.masyaman.datapack.serializers.caching;

import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Caching wrapper for Serializer.
 * Data format is:
 * [IndexInCache][Value, optional]
 * If value exists in cache, [IndexInCache] is written as index in cache list + 1, [Value] is omit.
 * If value does not exist in cache, [IndexInCache] is written as 0, then [Value] is written.
 * Value is put to the tail of cache list if cache limit is not reached or replaced least recently used element otherwise.
 */
public class SimpleCachedSerializer<E> implements Serializer<E> {

    private DataWriter os;
    private Serializer<E> serializer;
    private int cacheSize;

    private Map<E, IndexWithVersion> cache = new HashMap<>();
    private TreeMap<IndexWithVersion, E> versions;
    private long version = 0L;

    public SimpleCachedSerializer(DataWriter os, Serializer<E> serializer, int cacheSize) {
        this.os = os;
        this.serializer = serializer;
        this.cacheSize = cacheSize;

        if (cacheSize > 0) {
            versions = new TreeMap<>();
        }
    }

    @Override
    public void serialize(E o) throws IOException {
        if (o == null) {
           os.writeUnsignedLong(null);
           return;
        }
        version++;
        IndexWithVersion indexWithVersion = cache.get(o);
        if (indexWithVersion == null) {
            os.writeUnsignedLong(0L);
            serializer.serialize(o);
            if (versions == null || cache.size() < cacheSize) {
                indexWithVersion = new IndexWithVersion(cache.size() + 1, version);
            } else {
                IndexWithVersion oldestVersion = versions.firstKey();
                E elementToRemove = versions.remove(oldestVersion);
                indexWithVersion = cache.remove(elementToRemove);
                indexWithVersion.version = version;
            }
            cache.put(o, indexWithVersion);
            if (versions != null) {
                versions.put(indexWithVersion, o);
            }
        } else {
            os.writeUnsignedLong((long) indexWithVersion.index);
            if (versions != null) {
                versions.remove(indexWithVersion);
                indexWithVersion.version = version;
                versions.put(indexWithVersion, o);
            }
        }
    }

    private static class IndexWithVersion implements Comparable<IndexWithVersion> {
        private int index;
        private long version;

        public IndexWithVersion(int index, long version) {
            this.index = index;
            this.version = version;
        }

        @Override
        public int compareTo(IndexWithVersion o) {
            return Long.compare(version, o.version);
        }

        @Override
        public String toString() {
            return "index=" + index + ", version=" + version;
        }
    }
}
