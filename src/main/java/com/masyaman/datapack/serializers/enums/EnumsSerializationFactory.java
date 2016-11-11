package com.masyaman.datapack.serializers.enums;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.caching.LatestFirstCachedDeserializer;
import com.masyaman.datapack.serializers.caching.LatestFirstCachedSerializer;
import com.masyaman.datapack.serializers.strings.StringCachedSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringDeserializer;
import com.masyaman.datapack.serializers.strings.StringSerializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

import static com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers.wrap;

/**
 * Serialization factory for Enums.
 * Values are serialized as cached strings. See {@link StringCachedSerializationFactory}
 */
public class EnumsSerializationFactory<E extends Enum> extends AbstractEnumsSerializationFactory<E> {

    public static final EnumsSerializationFactory INSTANCE = new EnumsSerializationFactory();

    private EnumsSerializationFactory() {
        super("_E");
    }

    @Override
    public Serializer getSerializer(DataWriter os) throws IOException {
        return new LatestFirstCachedSerializer(os, new StringSerializer(os), 0);
    }

    @Override
    public Deserializer getDeserializer(DataReader is) throws IOException {
        return wrap(new LatestFirstCachedDeserializer(is, new StringDeserializer(is), 0));
    }
}
