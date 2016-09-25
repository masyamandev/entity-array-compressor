package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;

class ArraySerializer implements Serializer<Object[]> {

    private DataWriter os;
    private Serializer valueSerializer;

    public ArraySerializer(DataWriter os, SerializationFactory valueSerializationFactory, TypeDescriptor valueType) throws IOException {
        this.os = os;
        this.valueSerializer = os.createAndRegisterSerializer(valueSerializationFactory, valueType);
    }

    @Override
    public void serialize(Object[] array) throws IOException {
        if (array == null) {
           os.writeUnsignedLong(null);
           return;
        }
        os.writeUnsignedLong((long) array.length);

        for (Object val : array) {
            valueSerializer.serialize(val);
        }
    }

}
