package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.objects.UnknownTypeCachedSerializationFactory;
import com.masyaman.datapack.serializers.objects.UnknownTypeSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringCachedSerializationFactory;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.Map;

class MapSerializer<K, V> implements Serializer<Map<K, V>> {

    private DataWriter os;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;

    public MapSerializer(DataWriter os, TypeDescriptor<K> keyType, TypeDescriptor<V> valueType) throws IOException {
        this.os = os;
        keySerializer = os.createAndRegisterSerializer(getSerializer(keyType), keyType);
        valueSerializer = os.createAndRegisterSerializer(getSerializer(valueType), valueType);
    }

    private <T> SerializationFactory<T> getSerializer(TypeDescriptor<T> type) {
        if (type.getType() == String.class) {
            return (SerializationFactory<T>) StringCachedSerializationFactory.INSTANCE;
        }
        SerializationFactory serializationFactory = os.getSerializationFactoryLookup().getSerializationFactory(type);
        if (serializationFactory == null) {
//            serializationFactory = UnknownTypeSerializationFactory.INSTANCE;
            serializationFactory = UnknownTypeCachedSerializationFactory.INSTANCE;
        }
        return serializationFactory;
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
    }
}
