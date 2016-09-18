package com.masyaman.datapack.serializers.enums;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.GloballyDefined;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.strings.StringCachedSerializationFactory;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

public class EnumsSerializationFactory<E extends Enum> extends SerializationFactory<E> implements GloballyDefined {

    public static final EnumsSerializationFactory INSTANCE = new EnumsSerializationFactory();

    private EnumsSerializationFactory() {
        super("_E");
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
        Serializer serializer = StringCachedSerializationFactory.INSTANCE.createSerializer(os, new TypeDescriptor(String.class));
        return new Serializer<E1>() {
            @Override
            public void serialize(E1 o) throws IOException {
                serializer.serialize(o == null ? null : o.name());
            }
        };
    }

    @Override
    public <E1 extends E> Deserializer<E1> createDeserializer(DataReader is, TypeDescriptor<E1> type) throws IOException {
        Deserializer deserializer = StringCachedSerializationFactory.INSTANCE.createDeserializer(is, new TypeDescriptor(String.class));
        return new Deserializer<E1>() {
            @Override
            public E1 deserialize() throws IOException {
                String value = (String) deserializer.deserialize();
                if (value == null) {
                    return null;
                } else {
                    return (E1) Enum.valueOf(type.getType(), value);
                }
            }
        };
    }
}
