package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.*;

class MapDeserializer<K, V> implements Deserializer<Map<K, V>> {

    private DataReader is;
    private Deserializer<K> keyDeserializer;
    private Deserializer<V> valueDeserializer;

    public MapDeserializer(DataReader is, TypeDescriptor<K> keyType, TypeDescriptor<V> valueType) throws IOException {
        this.is = is;
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
        Map<K, V> map = new HashMap<>();
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
