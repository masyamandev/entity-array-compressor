package com.masyaman.datapack.serializers.enums;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

import static com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers.wrap;

/**
 * Abstract serialization factory for Enums.
 * Values are serialized as cached strings.
 */
public abstract class AbstractEnumsSerializationFactory<E extends Enum> extends SerializationFactory<E> {

    protected AbstractEnumsSerializationFactory(String name) {
        super(name);
    }

    @Override
    public TypeDescriptor<E> getDefaultType() {
        return new TypeDescriptor(Enum.class);
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return type.getType() == Enum.class || type.getType().isEnum();
    }

    @Override
    public <E1 extends E> Serializer<E1> createSerializer(DataWriter os, TypeDescriptor<E1> type) throws IOException {
        Serializer serializer = getSerializer(os);
        return new Serializer<E1>() {
            @Override
            public void serialize(E1 o) throws IOException {
                serializer.serialize(o == null ? null : o.name());
            }
        };
    }

    @Override
    public Deserializer createDeserializer(DataReader is) throws IOException {
        Deserializer<String> deserializer = getDeserializer(is);
        return wrap(new Deserializer<Object>() {
            @Override
            public <T> T deserialize(TypeDescriptor<T> type) throws IOException {
                String value = deserializer.deserialize(TypeDescriptor.STRING);
                if (String.class.isAssignableFrom(type.getType())) {
                    return (T) value;
                }
                if (value == null) {
                    return null;
                } else {
                    return (T) Enum.valueOf((Class) type.getType(), value);
                }
            }
        });
    }

    public abstract Serializer getSerializer(DataWriter os) throws IOException;

    public abstract Deserializer getDeserializer(DataReader is) throws IOException;
}
