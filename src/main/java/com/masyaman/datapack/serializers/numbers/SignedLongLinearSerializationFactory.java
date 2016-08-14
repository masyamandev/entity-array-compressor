package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

import static com.masyaman.datapack.serializers.numbers.NumberDeserializerWrappers.convertTo;
import static com.masyaman.datapack.serializers.numbers.NumberDeserializerWrappers.linearDeserializer;
import static com.masyaman.datapack.serializers.numbers.NumberSerializerWrappers.convertFrom;
import static com.masyaman.datapack.serializers.numbers.NumberSerializerWrappers.linearSerializer;

public class SignedLongLinearSerializationFactory extends SerializationFactory<Number> {

    public static final SignedLongLinearSerializationFactory INSTANCE = new SignedLongLinearSerializationFactory();

    private SignedLongLinearSerializationFactory() {
        super("_SLL");
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
        return convertFrom(linearSerializer(new LongSerializer(os)), type);
    }

    @Override
    public <E extends Number> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        type = NumberTypeResolver.readType(is, type);
        return convertTo(linearDeserializer(new LongDeserializer(is)), type);
    }


}
