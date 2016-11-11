package com.masyaman.datapack.serializers.strings;

import com.masyaman.datapack.annotations.AnnotationsHelper;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.caching.SimpleCachedDeserializer;
import com.masyaman.datapack.serializers.caching.SimpleCachedSerializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

import static com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers.wrap;

/**
 * Serialization factory for Strings.
 * Cached version of {@link StringSerializationFactory}
 * Could be used for storing small amount of unique strings.
 */
public class StringConstantsSerializationFactory extends SerializationFactory<String> {

    public static final StringConstantsSerializationFactory INSTANCE = new StringConstantsSerializationFactory();

    private StringConstantsSerializationFactory() {
        super("_SF");
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
        int cacheSize = AnnotationsHelper.getCacheSize(type);
        os.writeUnsignedLong((long) cacheSize);
        return (Serializer<E>) new SimpleCachedSerializer(os, new StringSerializer(os), cacheSize);
    }

    @Override
    public Deserializer createDeserializer(DataReader is) throws IOException {
        int cacheSize = is.readUnsignedLong().intValue();
        return wrap(new SimpleCachedDeserializer(is, new StringDeserializer(is), cacheSize));
    }

}
