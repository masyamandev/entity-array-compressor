package com.masyaman.datapack.serializers.objects;

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
import java.util.HashMap;
import java.util.Map;

public final class UnknownTypeCachedSerializationFactory<E> extends SerializationFactory<E> {

    public static final UnknownTypeCachedSerializationFactory INSTANCE = new UnknownTypeCachedSerializationFactory();

    private UnknownTypeCachedSerializationFactory() {
        super("_UC");
    }

    @Override
    public TypeDescriptor<E> getDefaultType() {
        return new TypeDescriptor(Object.class);
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return true;
    }

    @Override
    public <E1 extends E> Serializer<E1> createSerializer(DataWriter os, TypeDescriptor<E1> type) throws IOException {
//        return new SimpleCachedSerializer(os, new UnknownTypeSerializer(os, type));
        return new SlowCachedSerializer(os, new UnknownTypeSerializer(os, type), 0);
    }

    @Override
    public <E1 extends E> Deserializer<E1> createDeserializer(DataReader is, TypeDescriptor<E1> type) throws IOException {
//        return new SimpleCachedDeserializer(is, new UnknownTypeDeserializer(is, type));
        return new SlowCachedDeserializer(is, new UnknownTypeDeserializer(is, type));
    }
}
