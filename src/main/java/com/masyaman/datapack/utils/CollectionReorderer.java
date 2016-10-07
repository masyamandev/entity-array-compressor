package com.masyaman.datapack.utils;

import com.masyaman.datapack.cache.ObjectIdCache;
import com.masyaman.datapack.cache.ObjectIdCacheRingBuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollectionReorderer<V> {

    private ObjectIdCache<V> usageCache;

    public CollectionReorderer(int cacheSize) {
        this.usageCache = new ObjectIdCacheRingBuffer<V>(cacheSize);
    }

    public List<V> reorderByUsage(Collection<V> collection) {
        List<V> sorted = new ArrayList<>(collection);
        Collections.sort(sorted, (v1, v2) -> Integer.compare(index(v1), index(v2)));
        for (int i = sorted.size() - 1; i >= 0; i--) {
            V element = sorted.get(i);
            if (element != null) {
                usageCache.removeElement(element);
                usageCache.addHead(element);
            }
        }
        return sorted;
    }

    private int index(V v1) {
        int index = usageCache.indexOf(v1);
        return index >= 0 ? index : Integer.MAX_VALUE;
    }
}
