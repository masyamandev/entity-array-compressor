package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

/**
 * Temporary stub for unsupported or ignored types.
 */
public final class UnsupportedSerializationFactory<E> extends SerializationFactory<E> {

    public static final UnsupportedSerializationFactory INSTANCE = new UnsupportedSerializationFactory();

    private UnsupportedSerializationFactory() {
        super("");
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
        return new Serializer<E1>() {
            @Override
            public void serialize(E1 o) throws IOException {
            }
        };
    }

    @Override
    public <E1 extends E> Deserializer<E1> createDeserializer(DataReader is, TypeDescriptor<E1> type) throws IOException {
        return new Deserializer<E1>() {
            @Override
            public E1 deserialize() throws IOException {
                return null;
            }
        };
    }
}
