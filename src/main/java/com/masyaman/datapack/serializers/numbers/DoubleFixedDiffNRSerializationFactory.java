package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.annotations.AnnotationsHelper;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

import static com.masyaman.datapack.serializers.numbers.NumberDeserializerWrappers.*;
import static com.masyaman.datapack.serializers.numbers.NumberSerializerWrappers.*;

// Can give slightly better compression for the cost of non-precise rounding
public class DoubleFixedDiffNRSerializationFactory extends SerializationFactory<Number> {

    public static final DoubleFixedDiffNRSerializationFactory INSTANCE = new DoubleFixedDiffNRSerializationFactory();
    public static final int DEFAULT_DECIMAL_SCALE = 6;

    private DoubleFixedDiffNRSerializationFactory() {
        super("_DFD");
    }

    @Override
    public TypeDescriptor<? extends Number> getDefaultType() {
        return new TypeDescriptor(Double.class);
    }

    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return Double.class.isAssignableFrom(type.getType()) || Float.class.isAssignableFrom(type.getType());
    }

    @Override
    public <E extends Number> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type) throws IOException {
        NumberTypeResolver.writeType(os, type);
        return scaleByNR(os, diffSerializer(new LongSerializer(os)), AnnotationsHelper.getDecimalPrecision(type, DEFAULT_DECIMAL_SCALE));
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        type = NumberTypeResolver.readType(is, type);
        return convertTo(scaleBy(is, diffDeserializer(new LongDeserializer(is))), type);
    }


}
