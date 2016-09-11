package com.masyaman.datapack.serializers.strings;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.caching.*;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

public class StringCachedSerializationFactory extends SerializationFactory<String> {

    public static final StringCachedSerializationFactory INSTANCE = new StringCachedSerializationFactory();

    private StringCachedSerializationFactory() {
        super("_SC");
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
        // TODO cache size
        return (Serializer<E>) new CachedSerializer(os, new StringSerializer(os), 0);
    }

    @Override
    public <E extends String> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        return (Deserializer<E>) new CachedDeserializer(is, new StringDeserializer(is));
    }


}
