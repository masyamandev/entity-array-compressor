package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

/**
 * Serialization factory for any user objects. Object type may not be known at the moment of serialization creation.
 */
public final class UnknownTypeSerializationFactory<E> extends SerializationFactory<E> {

    public static final UnknownTypeSerializationFactory INSTANCE = new UnknownTypeSerializationFactory();

    private UnknownTypeSerializationFactory() {
        super("_U");
    }

    @Override
    public TypeDescriptor<E> getDefaultType() {
        return new TypeDescriptor(Object.class);
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return true;
    }

    @Override
    public <E1 extends E> Serializer<E1> createSerializer(DataWriter os, TypeDescriptor<E1> type) throws IOException {
        return new UnknownTypeSerializer(os, type);
    }

    @Override
    public Deserializer createDeserializer(DataReader is) throws IOException {
        return new UnknownTypeDeserializer(is);
    }
}
