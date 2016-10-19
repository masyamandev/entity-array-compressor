package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.annotations.deserialization.AsJson;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

/**
 * Serialization factory for any user objects. However, object type should be known at the moment of serialization creation.
 */
public class ObjectSerializationFactory extends SerializationFactory<Object> {

    public static final ObjectSerializationFactory INSTANCE = new ObjectSerializationFactory();

    private ObjectSerializationFactory() {
        super("_O");
    }

    @Override
    public TypeDescriptor<Object> getDefaultType() {
        return new TypeDescriptor(Object.class);
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return true;
    }

    @Override
    public <E> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type) throws IOException {
        return new ObjectSerializer(os, type);
    }

    @Override
    public <E> Deserializer<E> createDeserializer(DataReader is, TypeDescriptor<E> type) throws IOException {
        if (type.getAnnotation(AsJson.class) != null) {
            return (Deserializer<E>) new JsonObjectDeserializer(is, type);
        } else {
            return new ObjectDeserializer<>(is, type);
        }
    }
}
