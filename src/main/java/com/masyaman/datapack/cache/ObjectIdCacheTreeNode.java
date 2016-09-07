package com.masyaman.datapack.cache;

import java.util.*;

class ObjectIdCacheTreeNode<E> implements ObjectIdCache<E> {

    public static final LeafNodeBuilder DEFAULT_LEAF_NODE_BUILDER = () -> new ObjectIdCacheList(1);

    private int depth;
    private int maxSize;

    private Set<E> elementsLeft = new HashSet<>();
    private Set<E> elementsRight = new HashSet<>();
    private ObjectIdCache<E> headTailNode;
    private ObjectIdCache<E> middleNode;
    private int head = 0;

    private LeafNodeBuilder<E> leafNodeBuilder;

    // Could be used in tests only
    ObjectIdCacheTreeNode(int maxSize) {
        this(countBits(maxSize) + 1, maxSize, DEFAULT_LEAF_NODE_BUILDER);
    }

    ObjectIdCacheTreeNode(int depth, int maxSize, LeafNodeBuilder<E> leafNodeBuilder) {
        this.depth = depth;
        this.maxSize = maxSize;
        this.leafNodeBuilder = leafNodeBuilder;
    }

    ObjectIdCacheTreeNode(ObjectIdCacheTreeNode<E> headNode, E tailElement) {
        this.depth = headNode.depth + 1;
        this.maxSize = headNode.maxSize;
        this.leafNodeBuilder = headNode.leafNodeBuilder;
        this.head = 0;

        this.middleNode = headNode;
        this.elementsRight.addAll(headNode.elementsLeft);
        this.elementsRight.addAll(headNode.elementsRight);

        this.headTailNode = new ObjectIdCacheTreeNode(headNode.depth, headNode.maxSize, headNode.leafNodeBuilder);
        this.headTailNode.addHead(tailElement);
        this.elementsLeft.add(tailElement);
    }


    private static int countBits(int i) {
        int cnt = 0;
        while (i != 0) {
            i >>= 1;
            cnt++;
        }
        return cnt;
    }

    @Override
    public int size() {
        return elementsLeft.size() + elementsRight.size();
    }

    @Override
    public int maxSize() {
        return maxSize;
    }

    @Override
    public boolean contains(E element) {
        return elementsLeft.contains(element) || elementsRight.contains(element);
    }

    @Override
    public int removeElement(E element) {
        if (elementsRight.remove(element)) {
            return getMiddleNode().removeElement(element) + head;
        }
        if (elementsLeft.remove(element)) {
            int pos = getHeadTailNode().removeElement(element);
            if (pos < head) {
                head--;
                return pos;
            } else {
                return pos + elementsRight.size();
            }
        }
        return -1;
    }

    @Override
    public E removePosition(int position) {
        if (position < 0 || position >= size()) {
            return null;
        }
        if (position < head) {
            E e = getHeadTailNode().removePosition(position);
            elementsLeft.remove(e);
            head--;
            return e;
        } else if (position < head + elementsRight.size()) {
            E e = getMiddleNode().removePosition(position - head);
            elementsRight.remove(e);
            return e;
        } else {
            E e = getHeadTailNode().removePosition(position - elementsRight.size());
            elementsLeft.remove(e);
            return e;
        }
    }

    @Override
    public E get(int position) {
        if (position < 0 || position >= size()) {
            return null;
        }
        if (position < head) {
            return getHeadTailNode().get(position);
        } else if (position < head + elementsRight.size()) {
            return getMiddleNode().get(position - head);
        } else {
            return getHeadTailNode().get(position - elementsRight.size());
        }
    }

    @Override
    public E addHead(E element) {
        ObjectIdCache<E> left = getHeadTailNode();
        elementsLeft.add(element);
        E tail = left.addHead(element);
        elementsLeft.remove(tail);
        head++;
        if (head > left.size()) {
            headTailNode = middleNode;
            middleNode = left;

            Set<E> elLeft = elementsLeft;
            elementsLeft = elementsRight;
            elementsRight = elLeft;

            head = 0;

            if (tail != null) {
                elementsLeft.add(tail);
                tail = getHeadTailNode().addHead(tail);
                elementsLeft.remove(tail);
            }
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

    private ObjectIdCache<E> getHeadTailNode() {
        if (headTailNode == null) {
            headTailNode = (depth == 0) ? leafNodeBuilder.build() : new ObjectIdCacheTreeNode(depth - 1, maxSize, leafNodeBuilder);
        }
        return headTailNode;
    }

    private ObjectIdCache<E> getMiddleNode() {
        if (middleNode == null) {
            middleNode = (depth == 0) ? leafNodeBuilder.build() : new ObjectIdCacheTreeNode(depth - 1, maxSize, leafNodeBuilder);
        }
        return middleNode;
    }

    public interface LeafNodeBuilder<E> {
        ObjectIdCache<E> build();
    }
}
