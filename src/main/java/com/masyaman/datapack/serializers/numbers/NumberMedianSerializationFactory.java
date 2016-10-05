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
 * During serialization it saves difference and predicted value.
 * Prediction is a previous value + median of three previous value changes.
 */
public class NumberMedianSerializationFactory extends SerializationFactory<Number> {

    public static final NumberMedianSerializationFactory INSTANCE = new NumberMedianSerializationFactory();

    private NumberMedianSerializationFactory() {
        super("_NM");
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
        int decimalPrecision = getDecimalPrecision(type);
        RoundingMode roundingMode = getRoundingMode(type);
        int diffLength = 3;

        NumberTypeResolver.writeType(os, type);
        os.writeSignedLong((long) decimalPrecision);
        os.writeUnsignedLong((long) diffLength);
        return scaleBy(round(medianSerializer(new LongSerializer(os), diffLength), roundingMode), decimalPrecision, roundingMode);
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        type = NumberTypeResolver.readType(is, type);
        int decimalScale = -is.readSignedLong().intValue();
        int diffLength = is.readUnsignedLong().intValue();
        return scaleBy(convertTo(medianDeserializer(new LongDeserializer(is), diffLength), type), decimalScale, RoundingMode.HALF_UP);
    }

}
