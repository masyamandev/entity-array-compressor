package com.masyaman.datapack.cache;

public class ObjectIdCacheTree<E> implements ObjectIdCache<E> {

    private ObjectIdCacheTreeNode<E> rootNode;

    private ObjectIdCacheTreeNode.LeafNodeBuilder leafNodeBuilder = ObjectIdCacheTreeNode.DEFAULT_LEAF_NODE_BUILDER;

    public ObjectIdCacheTree(int maxSize) {
        this.rootNode = new ObjectIdCacheTreeNode<>(0, maxSize, leafNodeBuilder);
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
        return rootNode.contains(element);
    }

    @Override
    public int removeElement(E element) {
        return rootNode.removeElement(element);
    }

    @Override
    public E removePosition(int position) {
        return rootNode.removePosition(position);
    }

    @Override
    public E addHead(E element) {
        E tail = rootNode.addHead(element);
        if (tail != null && rootNode.size() < maxSize()) {
            rootNode = new ObjectIdCacheTreeNode<>(rootNode, tail);
            return null;
        }
        return tail;
    }

    @Override
    public String toString() {
        return rootNode.toString();
    }
}
