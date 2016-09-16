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

public class DoubleFixedSerializationFactory extends SerializationFactory<Number> {

    public static final DoubleFixedSerializationFactory INSTANCE = new DoubleFixedSerializationFactory();

    private DoubleFixedSerializationFactory() {
        super("_DF");
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
        return scaleBy(os, round(new LongSerializer(os)), AnnotationsHelper.getDecimalPrecision(type));
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        type = NumberTypeResolver.readType(is, type);
        return scaleBy(is, convertTo(new LongDeserializer(is), type));
    }


}
