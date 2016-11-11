package com.masyaman.datapack.serializers.enums;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.GloballyDefined;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.caching.SimpleCachedDeserializer;
import com.masyaman.datapack.serializers.caching.SimpleCachedSerializer;
import com.masyaman.datapack.serializers.strings.StringConstantsSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringDeserializer;
import com.masyaman.datapack.serializers.strings.StringSerializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

import static com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers.wrap;

/**
 * Serialization factory for Enums.
 * Values are serialized as cached constant strings. See {@link StringConstantsSerializationFactory}
 */
public class EnumsConstantsSerializationFactory<E extends Enum> extends AbstractEnumsSerializationFactory<E> implements GloballyDefined {

    public static final EnumsConstantsSerializationFactory INSTANCE = new EnumsConstantsSerializationFactory();

    private EnumsConstantsSerializationFactory() {
        super("_EC");
    }

    @Override
    public Serializer getSerializer(DataWriter os) throws IOException {
        return new SimpleCachedSerializer(os, new StringSerializer(os), 0);
    }

    @Override
    public Deserializer getDeserializer(DataReader is) throws IOException {
        return wrap(new SimpleCachedDeserializer(is, new StringDeserializer(is), 0));
    }
}
