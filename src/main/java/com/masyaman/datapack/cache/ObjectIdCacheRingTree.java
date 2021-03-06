package com.masyaman.datapack.cache;

import java.util.HashMap;
import java.util.Map;

public class ObjectIdCacheRingTree<E> implements ObjectIdCache<E> {

    public static int LEAF_BUFFER_SIZE = ObjectIdCacheRingBuffer.DEFAULT_BUFFER_SIZE;

    private ObjectIdCacheRingTreeNode<E> rootNode;

    private Map<E, ObjectIdCacheRingTreeNode.LookupInfo> lookupInfoMap = new HashMap<>();

    public ObjectIdCacheRingTree(int maxSize) {
        if (maxSize <= 0) {
            maxSize = Integer.MAX_VALUE;
        }
        this.rootNode = new ObjectIdCacheRingTreeNode<>(0, maxSize, lookupInfoMap);
    }

    @Override
    public int size() {
        return rootNode.size();
    }

    @Override
    public int maxSize() {
        return rootNode.maxSize();
    }

    @Override
    public E get(int position) {
        return rootNode.get(position);
    }

    @Override
    public boolean contains(E element) {
        return lookupInfoMap.containsKey(element);
    }

    @Override
    public int indexOf(E element) {
        ObjectIdCacheRingTreeNode.LookupInfo lookupInfo = lookupInfoMap.get(element);
        if (lookupInfo == null) {
            return -1;
        }
        return rootNode.indexOf(element, lookupInfo);
    }

    @Override
    public int removeElement(E element) {
        ObjectIdCacheRingTreeNode.LookupInfo lookupInfo = lookupInfoMap.remove(element);
        if (lookupInfo == null) {
            return -1;
        }
        return rootNode.removeElement(element, lookupInfo);
    }

    @Override
    public E removePosition(int position) {
        E element = rootNode.removePosition(position);
        if (element != null) {
            lookupInfoMap.remove(element);
        }
        return element;
    }

    @Override
    public E addHead(E element) {
        ObjectIdCacheRingTreeNode.LookupInfo lookupInfo = new ObjectIdCacheRingTreeNode.LookupInfo();
        lookupInfoMap.put(element, lookupInfo);
        E tail = rootNode.addHead(element, lookupInfo);
        if (tail == null) {
            return null;
        }
        if (rootNode.size() < maxSize()) {
            rootNode = new ObjectIdCacheRingTreeNode<>(rootNode, tail, lookupInfoMap);
            return null;
        } else {
            lookupInfoMap.remove(tail);
            return tail;
        }
    }

    @Override
    public String toString() {
        return rootNode.toString();
    }
}
