package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.annotations.AnnotationsHelper;
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
 * During serialization it saves difference and predicted value.
 * Prediction is a previous value + median of three previous value changes.
 */
public class NumberMedianSerializationFactory extends AbstractNumberSerializationFactory {

    public static final NumberMedianSerializationFactory INSTANCE = new NumberMedianSerializationFactory();
    public static final int DIFF_LEN = 3;

    private NumberMedianSerializationFactory() {
        super("_NM");
    }

    @Override
    public <E extends Number> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type, int decimalPrecision, RoundingMode roundingMode) throws IOException {
        int diffLength = AnnotationsHelper.getCacheSize(type, DIFF_LEN);
        os.writeUnsignedLong((long) diffLength);
        return scaleBy(round(medianSerializer(new LongSerializer(os), diffLength), roundingMode), decimalPrecision, roundingMode);
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type, int decimalPrecision) throws IOException {
        int diffLength = is.readUnsignedLong().intValue();
        return scaleBy(convertTo(medianDeserializer(new LongDeserializer(is), diffLength), type), decimalPrecision, RoundingMode.HALF_UP);
    }

}
