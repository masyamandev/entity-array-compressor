package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapSerializationFactory extends SerializationFactory {

    public static final MapSerializationFactory INSTANCE = new MapSerializationFactory();

    private MapSerializationFactory() {
        super("_M");
    }

    @Override
    public TypeDescriptor getDefaultType() {
        return new TypeDescriptor(HashMap.class);
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return Map.class.isAssignableFrom(type.getType());
    }

    @Override
    public Serializer createSerializer(DataWriter os, TypeDescriptor type) throws IOException {
        return new MapSerializer(os, new TypeDescriptor(type.getParametrizedType(0)), new TypeDescriptor(type.getParametrizedType(1))); // TODO
    }

    @Override
    public Deserializer createDeserializer(DataReader is, TypeDescriptor type) throws IOException {
        return new MapDeserializer(is, new TypeDescriptor(type.getParametrizedType(0)), new TypeDescriptor(type.getParametrizedType(1))); // TODO
    }
}
