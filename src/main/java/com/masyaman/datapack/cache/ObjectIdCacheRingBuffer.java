package com.masyaman.datapack.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// This implementation should not be used for big sized caches due to performance reasons
public class ObjectIdCacheRingBuffer<E> implements ObjectIdCache<E> {

    public static int DEFAULT_LENGTH = 3;

    private int maxSize = 0;
    private Object[] data;
    private int head = 0;
    private int size = 0;

    public ObjectIdCacheRingBuffer(int maxSize) {
        this.maxSize = maxSize;
        this.data = new Object[Math.min(maxSize, DEFAULT_LENGTH)];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int maxSize() {
        return maxSize;
    }

    @Override
    public boolean contains(E element) {
        for (int i = 0; i < size; i++) {
            if (data[(i + head) % data.length].equals(element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public E get(int position) {
        if (position >= 0 && position < size()) {
            return (E) data[(position + head) % data.length];
        }
        return null;
    }

    @Override
    public int removeElement(E element) {
        int idx = -1;
        for (int i = 0; i < size; i++) {
            if (data[(i + head) % data.length].equals(element)) {
                idx = i;
                break;
            }
        }
        removePosition(idx);
        return idx;
    }

    @Override
    public E removePosition(int position) {
        if (position >= 0 && position < size) {
            size--;
            int dataPos = (position + head) % data.length;
            E elemnt = (E) data[dataPos];
            // optimization for copy length could be done
            if (dataPos >= head) {
                if (dataPos - head > 0) {
                    System.arraycopy(data, head, data, head + 1, dataPos - head);
                }
                data[head] = null;
                head = (head + 1) % data.length;
            } else {
                int tail = (head + size) % data.length;
                if (tail - dataPos > 0) {
                    System.arraycopy(data, dataPos + 1, data, dataPos, tail - dataPos);
                }
                data[tail] = null;
            }
            return elemnt;
        }
        return null;
    }

    @Override
    public E addHead(E element) {
        if (size < maxSize && size == data.length) {
            Object[] oldData = data;
            data = new Object[Math.min(maxSize, data.length * 2)];
            System.arraycopy(oldData, head, data, 0, oldData.length - head);
            if (head > 0) {
                System.arraycopy(oldData, 0, data, oldData.length - head, head);
            }
            head = 0;
        }

        head = (head + data.length - 1) % data.length;

        E tailElement = (E) data[head];
        data[head] = element;

        if (size < data.length) {
            size++;
        }
        return tailElement;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size(); i++) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}