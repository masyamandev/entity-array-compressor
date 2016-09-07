package com.masyaman.datapack.cache;

public interface ObjectIdCache<E> {

    int size();
    int maxSize();

    E get(int position);
    boolean contains(E element);

    int removeElement(E element);
    E removePosition(int position);
    E addHead(E element);
}
