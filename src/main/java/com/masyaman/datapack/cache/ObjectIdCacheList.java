package com.masyaman.datapack.cache;

import java.util.ArrayList;
import java.util.List;

// This implementation should not be used due to performance reasons, it's used for tests only
class ObjectIdCacheList<E> implements ObjectIdCache<E> {

    private int maxSize = 0;
    private List<E> data;

    public ObjectIdCacheList(int maxSize) {
        this.maxSize = maxSize;
        this.data = new ArrayList<>(maxSize);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public int maxSize() {
        return maxSize;
    }

    @Override
    public boolean contains(E element) {
        return data.contains(element);
    }

    @Override
    public int indexOf(E element) {
        return data.indexOf(element);
    }

    @Override
    public E get(int position) {
        if (position >= 0 && position < data.size()) {
            return data.get(position);
        }
        return null;
    }

    @Override
    public int removeElement(E element) {
        int idx = data.indexOf(element);
        if (idx >= 0) {
            data.remove(idx);
        }
        return idx;
    }

    @Override
    public E removePosition(int position) {
        if (position >= 0 && position < data.size()) {
            return data.remove(position);
        }
        return null;
    }

    @Override
    public E addHead(E element) {
        E lastElement = data.size() >= maxSize ? data.remove(data.size() - 1) : null;
        data.add(0, element);
        return lastElement;
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
