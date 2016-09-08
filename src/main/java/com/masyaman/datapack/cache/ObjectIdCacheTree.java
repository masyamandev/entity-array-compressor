package com.masyaman.datapack.cache;

import java.util.HashMap;
import java.util.Map;

public class ObjectIdCacheTree<E> implements ObjectIdCache<E> {

    private ObjectIdCacheTreeNode<E> rootNode;

    private Map<E, ObjectIdCacheTreeNode.LookupInfo> lookupInfoMap = new HashMap<>();

    public ObjectIdCacheTree(int maxSize) {
        this.rootNode = new ObjectIdCacheTreeNode<>(0, maxSize, lookupInfoMap);
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
    public int removeElement(E element) {
        ObjectIdCacheTreeNode.LookupInfo lookupInfo = lookupInfoMap.remove(element);
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
        ObjectIdCacheTreeNode.LookupInfo lookupInfo = new ObjectIdCacheTreeNode.LookupInfo();
        lookupInfoMap.put(element, lookupInfo);
        E tail = rootNode.addHead(element, lookupInfo);
        if (tail == null) {
            return null;
        }
        if (rootNode.size() < maxSize()) {
            rootNode = new ObjectIdCacheTreeNode<>(rootNode, tail, lookupInfoMap);
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
