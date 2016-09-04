package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.Map;

class MapSerializer<K, V> implements Serializer<Map<K, V>> {

    private DataWriter os;
//    private TypeDescriptor<K> keyType;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;


    public MapSerializer(DataWriter os, SerializationFactory<K> keySerializationFactory, TypeDescriptor<K> keyType,
                         SerializationFactory<V> valueSerializationFactory, TypeDescriptor<V> valueType) throws IOException {
        this.os = os;
        this.keySerializer = os.createAndRegisterSerializer(keySerializationFactory, keyType);
        this.valueSerializer = os.createAndRegisterSerializer(valueSerializationFactory, valueType);
    }

    @Override
    public void serialize(Map<K, V> map) throws IOException {
        if (map == null) {
           os.writeUnsignedLong(null);
           return;
        }
        os.writeUnsignedLong((long) map.size());
//        for (Map.Entry<K, V> entry : map.entrySet()) {
//            keySerializer.serialize(entry.getKey());
//            valueSerializer.serialize(entry.getValue());
//        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keySerializer.serialize(entry.getKey());
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            valueSerializer.serialize(entry.getValue());
        }
//        List<K> keys = new ArrayList<>(map.keySet());
//        if (!(map instanceof SortedMap) && (Comparable.class.isAssignableFrom(keyType.getType()))) {
//            Collections.sort(keys, (Comparator<? super K>) Comparator.naturalOrder());
//        }
//        for (K key : keys) {
//            keySerializer.serialize(key);
//        }
//        for (K key : keys) {
//            valueSerializer.serialize(map.get(key));
//        }
    }
}
