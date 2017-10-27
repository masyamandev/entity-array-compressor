package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.GloballyDefined;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.math.RoundingMode;

import static com.masyaman.datapack.serializers.numbers.DeserializerWrappers.convertTo;
import static com.masyaman.datapack.serializers.numbers.DeserializerWrappers.scaleBy;
import static com.masyaman.datapack.serializers.numbers.SerializerWrappers.round;
import static com.masyaman.datapack.serializers.numbers.SerializerWrappers.scaleBy;

/**
 * Serialization factory for Numbers.
 * Values are stored as fixed-points Longs.
 * Very basic serialization using unsigned variable-length coding. Negative values could be saves as well, but it'll
 * require 9 bytes per value in stream.
 */
public class UnsignedLongSerializationFactory extends AbstractNumberSerializationFactory implements GloballyDefined {

    public static final UnsignedLongSerializationFactory INSTANCE = new UnsignedLongSerializationFactory();

    private UnsignedLongSerializationFactory() {
        super("_UL");
    }

    @Override
    public TypeDescriptor<? extends Number> getDefaultType() {
        return TypeDescriptor.LONG;
    }

    @Override
    public <E extends Number> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type, int decimalPrecision, RoundingMode roundingMode) throws IOException {
        return scaleBy(round(new UnsignedLongSerializer(os), roundingMode), decimalPrecision, roundingMode);
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type, int decimalPrecision) throws IOException {
        return scaleBy(convertTo(new UnsignedLongDeserializer(is), type), decimalPrecision, RoundingMode.HALF_UP);
    }

}
