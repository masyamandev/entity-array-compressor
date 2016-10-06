package com.masyaman.datapack.serializers.strings;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

/**
 * Serialization factory for Strings.
 */
public class StringSerializationFactory extends SerializationFactory<String> {

    public static final StringSerializationFactory INSTANCE = new StringSerializationFactory();

    private StringSerializationFactory() {
        super("_S");
    }

    @Override
    public TypeDescriptor<String> getDefaultType() {
        return new TypeDescriptor(String.class);
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return String.class.isAssignableFrom(type.getType());
    }

    @Override
    public <E extends String> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type) throws IOException {
        return (Serializer<E>) new StringSerializer(os);
    }

    @Override
    public <E extends String> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        return (Deserializer<E>) new StringDeserializer(is);
    }


}
