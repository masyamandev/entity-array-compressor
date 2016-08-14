package com.masyaman.datapack.serializers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

public abstract class SerializationFactory<T> {

    private String name;

    protected SerializationFactory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract TypeDescriptor<? extends T> getDefaultType(); // TODO: remove?

    public abstract boolean isApplicable(TypeDescriptor type);

    public abstract <E extends T> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type) throws IOException;
    public abstract <E extends T> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException;

}
