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

    public abstract TypeDescriptor<? extends T> getDefaultType();

    public abstract boolean isApplicable(TypeDescriptor type);

    public abstract <E extends T> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type) throws IOException;

    public abstract Deserializer createDeserializer(DataReader is) throws IOException;

    public static <I extends SerializationFactory> SerializationFactory<I> getInstance(Class<I> serializationFactoryClass) {
        try {
            return (SerializationFactory) serializationFactoryClass.getField("INSTANCE").get(null);
        } catch (ReflectiveOperationException e) {
            System.out.println("Unable to find static field INSTANCE in " + serializationFactoryClass.getClass());
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerializationFactory<?> that = (SerializationFactory<?>) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
