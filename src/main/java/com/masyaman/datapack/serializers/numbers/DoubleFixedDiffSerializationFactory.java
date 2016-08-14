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

public class DoubleFixedDiffSerializationFactory extends SerializationFactory<Number> {

    public static final DoubleFixedDiffSerializationFactory INSTANCE = new DoubleFixedDiffSerializationFactory();
    public static final int DEFAULT_DECIMAL_SCALE = 6;

    private DoubleFixedDiffSerializationFactory() {
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
        return scaleBy(os, diffSerializer(new LongSerializer(os)), AnnotationsHelper.getDecimalPrecision(type, DEFAULT_DECIMAL_SCALE));
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        type = NumberTypeResolver.readType(is, type);
        return convertTo(scaleBy(is, diffDeserializer(new LongDeserializer(is))), type);
    }


}
