package com.masyaman.datapack.serializers.strings;

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

import static com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers.*;

/**
 * Serialization factory for Strings.
 * Cached version of {@link StringSerializationFactory}
 * Could be used for storing big variety of strings, some of which are occurred many times, but most of them occurred only once.
 */
public class StringCachedSerializationFactory extends SerializationFactory<String> {

    public static final StringCachedSerializationFactory INSTANCE = new StringCachedSerializationFactory();

    private StringCachedSerializationFactory() {
        super("_SC");
    }

    @Override
    public TypeDescriptor<String> getDefaultType() {
        return TypeDescriptor.STRING;
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return String.class.isAssignableFrom(type.getType());
    }

    @Override
    public <E extends String> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type) throws IOException {
        int cacheSize = AnnotationsHelper.getCacheSize(type, 0);
        os.writeUnsignedLong((long) cacheSize);
        return (Serializer<E>) new LatestFirstCachedSerializer(os, new StringSerializer(os), cacheSize);
    }

    @Override
    public Deserializer createDeserializer(DataReader is) throws IOException {
        int cacheSize = is.readUnsignedLong().intValue();
        return wrap(new LatestFirstCachedDeserializer(is, new StringDeserializer(is), cacheSize));
    }

}
