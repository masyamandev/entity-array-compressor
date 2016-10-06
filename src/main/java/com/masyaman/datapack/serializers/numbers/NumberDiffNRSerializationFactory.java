package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.math.RoundingMode;

import static com.masyaman.datapack.serializers.numbers.DeserializerWrappers.*;
import static com.masyaman.datapack.serializers.numbers.SerializerWrappers.diffSerializer;
import static com.masyaman.datapack.serializers.numbers.SerializerWrappers.scaleByNR;

/**
 * Serialization factory for Numbers.
 * Values are stored as fixed-points Longs.
 * Same as {@link NumberDiffSerializationFactory} with compatible deserializer.
 * Can give slightly better compression for the cost of non-precise rounding.
 */
public class NumberDiffNRSerializationFactory extends AbstractNumberSerializationFactory {

    public static final NumberDiffNRSerializationFactory INSTANCE = new NumberDiffNRSerializationFactory();

    private NumberDiffNRSerializationFactory() {
        super("_ND");
    }

    @Override
    public <E extends Number> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type, int decimalPrecision, RoundingMode roundingMode) throws IOException {
        return scaleByNR(diffSerializer(new LongSerializer(os)), decimalPrecision);
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type, int decimalPrecision) throws IOException {
        return scaleBy(convertTo(diffDeserializer(new LongDeserializer(is)), type), decimalPrecision, RoundingMode.HALF_UP);
    }

}
