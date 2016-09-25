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
    private TypeDescriptor type;
    private Deserializer<V> valueDeserializer;

    public CollectionDeserializer(DataReader is, TypeDescriptor type, TypeDescriptor<V> valueType) throws IOException {
        this.is = is;
        this.type = type;
        valueDeserializer = is.createAndRegisterDeserializer(valueType);
    }

    @Override
    public Collection<V> deserialize() throws IOException {
        Long length = is.readUnsignedLong();
        if (length == null) {
            return null;
        }
        int len = length.intValue();
        Collection<V> collection = null;
        try {
            collection = (Collection<V>) ConstructorUtils.createInstance(type.getType(), CLASSES);
        } catch (ReflectiveOperationException e) {
            throw new IOException("Class initializatoin exception", e);
        }
        for (int i = 0; i < len; i++) {
            collection.add(valueDeserializer.deserialize());
        }
        return collection;
    }
}
