package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

public class ObjectSerializationFactory extends SerializationFactory<Object> {

    public static final ObjectSerializationFactory INSTANCE = new ObjectSerializationFactory();

    private ObjectSerializationFactory() {
        super("_O");
    }

    @Override
    public TypeDescriptor<Object> getDefaultType() {
        return new TypeDescriptor(Object.class);
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return true;
    }

    @Override
    public <E> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type) throws IOException {
        return new ObjectSerializer(os, type);
    }

    @Override
    public <E> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        return new ObjectDeserializer<>(is, type);
    }
}
