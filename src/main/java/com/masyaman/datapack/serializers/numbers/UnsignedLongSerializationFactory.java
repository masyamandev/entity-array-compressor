package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.GloballyDefined;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.math.RoundingMode;

import static com.masyaman.datapack.annotations.AnnotationsHelper.*;
import static com.masyaman.datapack.serializers.numbers.NumberDeserializerWrappers.*;
import static com.masyaman.datapack.serializers.numbers.NumberSerializerWrappers.*;

/**
 * Serialization factory for Numbers.
 * Values are stored as fixed-points Longs.
 * Very basic serialization using unsigned variable-length coding. Negative values could be saves as well, but it'll
 * require 9 bytes per value in stream.
 */
public class UnsignedLongSerializationFactory extends SerializationFactory<Number> implements GloballyDefined {

    public static final UnsignedLongSerializationFactory INSTANCE = new UnsignedLongSerializationFactory();

    private UnsignedLongSerializationFactory() {
        super("_UL");
    }

    @Override
    public TypeDescriptor<? extends Number> getDefaultType() {
        return new TypeDescriptor(Long.class);
    }

    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return Number.class.isAssignableFrom(type.getType());
    }

    @Override
    public <E extends Number> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type) throws IOException {
        NumberTypeResolver.writeType(os, type);
        int decimalPrecision = getDecimalPrecision(type);
        RoundingMode roundingMode = getRoundingMode(type);
        return scaleBy(os, round(new UnsignedLongSerializer(os), roundingMode), decimalPrecision, roundingMode);
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        type = NumberTypeResolver.readType(is, type);
        return scaleBy(is, convertTo(new UnsignedLongDeserializer(is), type), RoundingMode.HALF_UP);
    }

}
