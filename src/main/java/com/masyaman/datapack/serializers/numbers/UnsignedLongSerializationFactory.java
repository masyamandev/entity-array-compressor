package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.math.RoundingMode;

import static com.masyaman.datapack.annotations.AnnotationsHelper.*;
import static com.masyaman.datapack.serializers.numbers.DeserializerWrappers.*;
import static com.masyaman.datapack.serializers.numbers.SerializerWrappers.*;

/**
 * Serialization factory for Numbers.
 * Values are stored as fixed-points Longs.
 * Very basic serialization using unsigned variable-length coding. Negative values could be saves as well, but it'll
 * require 9 bytes per value in stream.
 */
public class UnsignedLongSerializationFactory extends SerializationFactory<Number> {

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
        int decimalPrecision = getDecimalPrecision(type);
        RoundingMode roundingMode = getRoundingMode(type);

        NumberTypeResolver.writeType(os, type);
        os.writeSignedLong((long) decimalPrecision);
        return scaleBy(round(new UnsignedLongSerializer(os), roundingMode), decimalPrecision, roundingMode);
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        type = NumberTypeResolver.readType(is, type);
        int decimalScale = -is.readSignedLong().intValue();
        return scaleBy(convertTo(new UnsignedLongDeserializer(is), type), decimalScale, RoundingMode.HALF_UP);
    }

}
