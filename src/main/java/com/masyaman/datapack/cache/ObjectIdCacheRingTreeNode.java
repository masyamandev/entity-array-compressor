package com.masyaman.datapack.cache;

import java.util.*;

class ObjectIdCacheRingTreeNode<E> {

    private int depth;
    private int size;
    private int maxSize;

    private Map<E, ObjectIdCacheRingTreeNode.LookupInfo> lookupInfoMap;

    private ObjectIdCacheRingTreeNode<E> headTailNode;
    private ObjectIdCacheRingTreeNode<E> middleNode;
    private int head = 0;

    boolean rotated = false;

    private ObjectIdCache<E> leafElements = null;

    ObjectIdCacheRingTreeNode(int depth, int maxSize, Map<E, ObjectIdCacheRingTreeNode.LookupInfo> lookupInfoMap) {
        this.depth = depth;
        this.maxSize = maxSize;
        this.size = 0;
        this.lookupInfoMap = lookupInfoMap;
        if (depth == 0) {
            leafElements = new ObjectIdCacheRingBuffer<E>(Math.min(maxSize, ObjectIdCacheRingTree.LEAF_BUFFER_SIZE));
        }
    }

    ObjectIdCacheRingTreeNode(ObjectIdCacheRingTreeNode<E> headNode, E tailElement, Map<E, ObjectIdCacheRingTreeNode.LookupInfo> lookupInfoMap) {
        this.depth = headNode.depth + 1;
        this.maxSize = headNode.maxSize;
        this.lookupInfoMap = lookupInfoMap;

        this.middleNode = headNode;

        LookupInfo tailLookupInfo = lookupInfoMap.get(tailElement);

        this.headTailNode = new ObjectIdCacheRingTreeNode(headNode.depth, headNode.maxSize, lookupInfoMap);
        this.headTailNode.addHead(tailElement, tailLookupInfo);

        this.rotated = true;
        tailLookupInfo.setHead(depth, rotated);

        this.size = headNode.size() + 1;

        //System.out.println("depth = " + depth + ", size = " + size);
    }

    public int size() {
        return (depth == 0) ? leafElements.size() : size;
    }

    public int maxSize() {
        return maxSize;
    }

    public int removeElement(E element, LookupInfo lookupInfo) {
        if (depth == 0) {
            return leafElements.removeElement(element);
        }

        size--;
        if (lookupInfo.isHead(depth, rotated)) {
            int pos = getHeadTailNode().removeElement(element, lookupInfo);
            if (pos < head) {
                head--;
                return pos;
            } else {
                return pos + getMiddleNode().size();
            }
        } else {
            return getMiddleNode().removeElement(element, lookupInfo) + head;
        }
    }

    public E removePosition(int position) {
        if (depth == 0) {
            return leafElements.removePosition(position);
        }
        if (position < 0 || position >= size()) {
            return null;
        }

        size--;
        if (position < head) {
            E e = getHeadTailNode().removePosition(position);
            head--;
            return e;
        } else if (position < head + getMiddleNode().size()) {
            E e = getMiddleNode().removePosition(position - head);
            return e;
        } else {
            E e = getHeadTailNode().removePosition(position - getMiddleNode().size());
            return e;
        }
    }

    public E get(int position) {
        if (depth == 0) {
            return leafElements.get(position);
        }
        if (position < 0 || position >= size()) {
            return null;
        }

        if (position < head) {
            return getHeadTailNode().get(position);
        } else if (position < head + getMiddleNode().size()) {
            return getMiddleNode().get(position - head);
        } else {
            return getHeadTailNode().get(position - getMiddleNode().size());
        }
    }

    public E addHead(E element, LookupInfo lookupInfo) {
        if (depth == 0) {
            return leafElements.addHead(element);
        }
        ObjectIdCacheRingTreeNode<E> left = getHeadTailNode();
        lookupInfo.setHead(depth, rotated);
        E tail = left.addHead(element, lookupInfo);
        head++;
        if (head > left.size()) {
            headTailNode = middleNode;
            middleNode = left;
            rotated = !rotated;

            head = 0;

            if (tail != null) {
                LookupInfo tailLookupInfo = lookupInfoMap.get(tail);
                tailLookupInfo.setHead(depth, rotated);
                tail = getHeadTailNode().addHead(tail, tailLookupInfo);
            }
        }
        if (tail == null) {
            size++;
        }
        return size() > maxSize() ? removePosition(size() - 1) : tail;
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

    private ObjectIdCacheRingTreeNode<E> getHeadTailNode() {
        if (headTailNode == null) {
            headTailNode = new ObjectIdCacheRingTreeNode(depth - 1, maxSize, lookupInfoMap);
        }
        return headTailNode;
    }

    private ObjectIdCacheRingTreeNode<E> getMiddleNode() {
        if (middleNode == null) {
            middleNode = new ObjectIdCacheRingTreeNode(depth - 1, maxSize, lookupInfoMap);
        }
        return middleNode;
    }

    public static class LookupInfo {
        private long nodeLookup;
        public boolean isHead(int depth, boolean rotated) {
            return ((nodeLookup & (1L << (depth - 1))) == 0) ^ rotated;
        }
        public void setHead(int depth, boolean rotated) {
            if (rotated) {
                nodeLookup |= (1L << (depth - 1));
            } else {
                nodeLookup &= ~(1L << (depth - 1));
            }
        }

        @Override
        public String toString() {
            return "" + nodeLookup;
        }
    }
}
