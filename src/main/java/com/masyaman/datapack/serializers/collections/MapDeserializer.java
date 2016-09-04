package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.ConstructorUtils;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.*;

class MapDeserializer<K, V> implements Deserializer<Map<K, V>> {

    private static final Class[] CLASSES = {HashMap.class, TreeMap.class, LinkedHashMap.class};

    private DataReader is;
    private TypeDescriptor type;
    private Deserializer<K> keyDeserializer;
    private Deserializer<V> valueDeserializer;

    public MapDeserializer(DataReader is, TypeDescriptor type, TypeDescriptor<K> keyType, TypeDescriptor<V> valueType) throws IOException {
        this.is = is;
        this.type = type;
        keyDeserializer = is.createAndRegisterDeserializer(keyType);
        valueDeserializer = is.createAndRegisterDeserializer(valueType);
    }

    @Override
    public Map<K, V> deserialize() throws IOException {
        Long length = is.readUnsignedLong();
        if (length == null) {
            return null;
        }
        int len = length.intValue();
        Map<K, V> map = null;
        try {
            map = (Map<K, V>) ConstructorUtils.createInstance(type.getType(), CLASSES);
        } catch (ReflectiveOperationException e) {
            throw new IOException("Class initializatoin exception", e);
        }
//        for (int i = 0; i < len; i++) {
//            map.put(keyDeserializer.deserialize(), valueDeserializer.deserialize());
//        }

        List<K> keys = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            keys.add(keyDeserializer.deserialize());
        }
        for (int i = 0; i < len; i++) {
            map.put(keys.get(i), valueDeserializer.deserialize());
        }
        return map;
    }
}
