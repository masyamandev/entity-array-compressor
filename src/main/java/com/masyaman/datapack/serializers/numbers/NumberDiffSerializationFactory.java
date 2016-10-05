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
import static com.masyaman.datapack.serializers.numbers.NumberDeserializerWrappers.*;
import static com.masyaman.datapack.serializers.numbers.NumberSerializerWrappers.*;

/**
 * Serialization factory for Numbers.
 * Values are stored as fixed-points Longs.
 * Used by default for all Number serializations.
 * During serialization it saves difference to previous value. This gives result close to 0 on small value changes, so
 * it could use less bytes in stream.
 */
public class NumberDiffSerializationFactory extends SerializationFactory<Number> {

    public static final NumberDiffSerializationFactory INSTANCE = new NumberDiffSerializationFactory();

    private NumberDiffSerializationFactory() {
        super("_ND");
    }

    @Override
    public TypeDescriptor<? extends Number> getDefaultType() {
        return new TypeDescriptor(Double.class);
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
        return scaleBy(os, round(diffSerializer(new LongSerializer(os)), roundingMode), decimalPrecision, roundingMode);
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        type = NumberTypeResolver.readType(is, type);
        return scaleBy(is, convertTo(diffDeserializer(new LongDeserializer(is)), type), RoundingMode.HALF_UP);
    }

}
