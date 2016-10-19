package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.math.RoundingMode;

import static com.masyaman.datapack.annotations.AnnotationsHelper.getDecimalPrecision;
import static com.masyaman.datapack.annotations.AnnotationsHelper.getRoundingMode;
import static com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers.wrapNumber;

/**
 * Abstract Serialization factory for Numbers.
 * Values are stored as fixed-points Longs.
 * Very basic serialization using signed variable-length coding.
 */
public abstract class AbstractNumberSerializationFactory extends SerializationFactory<Number> {

    protected AbstractNumberSerializationFactory(String name) {
        super(name);
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

        NumberTypeResolver.writeType(os, type);
        os.writeSignedLong((long) decimalPrecision);

        return createSerializer(os, type, decimalPrecision, roundingMode);
    }

    @Override
    public <E> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        TypeDescriptor numberType = NumberTypeResolver.readType(is, type);
        int decimalPrecision = -is.readSignedLong().intValue();

        Deserializer deserializer = createDeserializer(is, numberType, decimalPrecision);
        return wrapNumber(deserializer, type);
    }

    public abstract <E extends Number> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type, int decimalPrecision, RoundingMode roundingMode) throws IOException;

    public abstract <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type, int decimalPrecision) throws IOException;

}
