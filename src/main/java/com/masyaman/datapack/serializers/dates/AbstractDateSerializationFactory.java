package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.Date;

import static com.masyaman.datapack.serializers.dates.SerializerWrappers.convertFrom;

/**
 * Abstract Serialization factory for Dates.
 * Values are stored as fixed-points Longs.
 * Very basic serialization using signed variable-length coding.
 */
abstract class AbstractDateSerializationFactory extends SerializationFactory {

    static final TypeDescriptor LONG_TYPE = new TypeDescriptor(Long.class);

    protected AbstractDateSerializationFactory(String name) {
        super(name);
    }

    protected abstract SerializationFactory<? extends Number> getNumberSerializationFactory();

    @Override
    public TypeDescriptor getDefaultType() {
        return new TypeDescriptor(Date.class);
    }

    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return Date.class.isAssignableFrom(type.getType()) || Long.class.isAssignableFrom(type.getType());
    }

    @Override
    public Serializer createSerializer(DataWriter os, TypeDescriptor type) throws IOException {
        return convertFrom(getNumberSerializationFactory().createSerializer(os, LONG_TYPE), type);
    }

    @Override
    public Deserializer createDeserializer(DataReader is, TypeDescriptor type) throws IOException {
        return DeserializerWrappers.convertTo(getNumberSerializationFactory().createDeserializer(is, LONG_TYPE), type);
    }

}
