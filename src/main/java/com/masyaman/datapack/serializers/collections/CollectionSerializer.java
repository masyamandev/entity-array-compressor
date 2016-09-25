package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.Collection;

class CollectionSerializer<V> implements Serializer<Collection<V>> {

    private DataWriter os;
    private Serializer<V> valueSerializer;

    public CollectionSerializer(DataWriter os, SerializationFactory<V> valueSerializationFactory, TypeDescriptor<V> valueType) throws IOException {
        this.os = os;
        this.valueSerializer = os.createAndRegisterSerializer(valueSerializationFactory, valueType);

    }

    @Override
    public void serialize(Collection<V> collection) throws IOException {
        if (collection == null) {
           os.writeUnsignedLong(null);
           return;
        }
        os.writeUnsignedLong((long) collection.size());

        for (V val : collection) {
            valueSerializer.serialize(val);
        }
    }

}
