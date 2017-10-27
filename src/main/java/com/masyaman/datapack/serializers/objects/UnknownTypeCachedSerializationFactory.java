package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.annotations.AnnotationsHelper;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.caching.LatestFirstCachedDeserializer;
import com.masyaman.datapack.serializers.caching.LatestFirstCachedSerializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

/**
 * Serialization factory for any user objects.
 * Cached version of {@link UnknownTypeSerializationFactory}.
 * Caution: serializer stores previous values, so objects may be stored incorrectly if they're not immutable.
 */
public final class UnknownTypeCachedSerializationFactory<E> extends SerializationFactory<E> {

    public static final UnknownTypeCachedSerializationFactory INSTANCE = new UnknownTypeCachedSerializationFactory();

    private UnknownTypeCachedSerializationFactory() {
        super("_UC");
    }

    @Override
    public TypeDescriptor<E> getDefaultType() {
        return (TypeDescriptor<E>) TypeDescriptor.OBJECT;
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return true;
    }

    @Override
    public <E1 extends E> Serializer<E1> createSerializer(DataWriter os, TypeDescriptor<E1> type) throws IOException {
        int cacheSize = AnnotationsHelper.getCacheSize(type, 0);
        os.writeUnsignedLong((long) cacheSize);
        return new LatestFirstCachedSerializer(os, new UnknownTypeSerializer(os), cacheSize);
    }

    @Override
    public Deserializer createDeserializer(DataReader is) throws IOException {
        int cacheSize = is.readUnsignedLong().intValue();
        return new LatestFirstCachedDeserializer(is, new UnknownTypeDeserializer(is), cacheSize);
    }
}
