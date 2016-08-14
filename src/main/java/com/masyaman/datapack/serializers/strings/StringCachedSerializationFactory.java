package com.masyaman.datapack.serializers.strings;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.caching.SimpleCachedDeserializer;
import com.masyaman.datapack.serializers.caching.SimpleCachedSerializer;
import com.masyaman.datapack.serializers.caching.SlowCachedDeserializer;
import com.masyaman.datapack.serializers.caching.SlowCachedSerializer;
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
//        return (Serializer<E>) new SimpleCachedSerializer(os, new StringSerializer(os));
        return (Serializer<E>) new SlowCachedSerializer(os, new StringSerializer(os), 0);
    }

    @Override
    public <E extends String> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
//        return (Deserializer<E>) new SimpleCachedDeserializer(is, new StringDeserializer(is));
        return (Deserializer<E>) new SlowCachedDeserializer(is, new StringDeserializer(is));
    }


}
