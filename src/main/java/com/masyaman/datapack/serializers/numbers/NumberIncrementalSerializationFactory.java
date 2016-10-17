package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.math.RoundingMode;

import static com.masyaman.datapack.serializers.numbers.DeserializerWrappers.*;
import static com.masyaman.datapack.serializers.numbers.DeserializerWrappers.scaleBy;
import static com.masyaman.datapack.serializers.numbers.SerializerWrappers.*;
import static com.masyaman.datapack.serializers.numbers.SerializerWrappers.scaleBy;

/**
 * Serialization factory for incrementing positive Numbers. Could be slightly better than {@link NumberDiffSerializationFactory}
 * in some cases. Negative or decrementing values are also supported, but not efficient.
 * Values are stored as fixed-points unsigned Longs.
 * During serialization it saves difference to previous value using unsigned Longs.
 */
public class NumberIncrementalSerializationFactory extends AbstractNumberSerializationFactory {

    public static final NumberIncrementalSerializationFactory INSTANCE = new NumberIncrementalSerializationFactory();

    private NumberIncrementalSerializationFactory() {
        super("_NI");
    }

    @Override
    public <E extends Number> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type, int decimalPrecision, RoundingMode roundingMode) throws IOException {
        return scaleBy(round(diffSerializer(new UnsignedLongSerializer(os)), roundingMode), decimalPrecision, roundingMode);
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type, int decimalPrecision) throws IOException {
        return scaleBy(convertTo(diffDeserializer(new UnsignedLongDeserializer(is)), type), decimalPrecision, RoundingMode.HALF_UP);
    }

}
