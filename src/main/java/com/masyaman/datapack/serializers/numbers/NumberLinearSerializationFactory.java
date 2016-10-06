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
 * Serialization factory for Numbers.
 * Values are stored as fixed-points Longs.
 * During serialization it saves difference and predicted value. Prediction is made as linear interpolation using 2
 * previous values.
 */
public class NumberLinearSerializationFactory extends AbstractNumberSerializationFactory {

    public static final NumberLinearSerializationFactory INSTANCE = new NumberLinearSerializationFactory();

    private NumberLinearSerializationFactory() {
        super("_NL");
    }

    @Override
    public <E extends Number> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type, int decimalPrecision, RoundingMode roundingMode) throws IOException {
        return scaleBy(round(linearSerializer(new LongSerializer(os)), roundingMode), decimalPrecision, roundingMode);
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type, int decimalPrecision) throws IOException {
        return scaleBy(convertTo(linearDeserializer(new LongDeserializer(is)), type), decimalPrecision, RoundingMode.HALF_UP);
    }

}
