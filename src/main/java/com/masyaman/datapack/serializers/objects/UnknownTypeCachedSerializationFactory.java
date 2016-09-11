package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.annotations.AnnotationsHelper;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.caching.CachedDeserializer;
import com.masyaman.datapack.serializers.caching.CachedSerializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

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
        return new CachedSerializer(os, new UnknownTypeSerializer(os, type), AnnotationsHelper.getCacheSize(type));
    }

    @Override
    public <E1 extends E> Deserializer<E1> createDeserializer(DataReader is, TypeDescriptor<E1> type) throws IOException {
        return new CachedDeserializer(is, new UnknownTypeDeserializer(is, type));
    }
}
