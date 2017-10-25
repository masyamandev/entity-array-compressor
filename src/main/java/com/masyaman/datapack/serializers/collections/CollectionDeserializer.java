package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.ConstructorUtils;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.*;

class CollectionDeserializer<V> implements Deserializer<Collection<V>> {

    private static final Class[] CLASSES = {ArrayList.class, LinkedList.class,
            HashSet.class, TreeSet.class, LinkedHashSet.class};

    private DataReader is;
    private Deserializer<V> valueDeserializer;

    public CollectionDeserializer(DataReader is, Deserializer<V> valueDeserializer) {
        this.is = is;
        this.valueDeserializer = valueDeserializer;
    }

    @Override
    public Collection<V> deserialize(TypeDescriptor type) throws IOException {
        Long length = is.readUnsignedLong();
        if (length == null) {
            return null;
        }
        Class collectionType = type.getType();
        TypeDescriptor valueType = type.getParametrizedTypeDescriptor(0);
        if (collectionType.isAssignableFrom(Collection.class)) {
            collectionType = Collection.class;
        }

        int len = length.intValue();
        Collection<V> collection = null;
        try {
            collection = (Collection<V>) ConstructorUtils.createInstance(collectionType, CLASSES);
        } catch (ReflectiveOperationException e) {
            throw new IOException("Class initialization exception", e);
        }
        for (int i = 0; i < len; i++) {
            collection.add((V) valueDeserializer.deserialize(valueType));
        }
        return collection;
    }
}
