package com.masyaman.datapack.serializers.enums;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.GloballyDefined;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.strings.StringConstantsSerializationFactory;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

import static com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers.wrap;

/**
 * Serialization factory for Enums.
 * Values are serialized as cached constant strings. See {@link StringConstantsSerializationFactory}
 */
public class EnumsConstantsSerializationFactory<E extends Enum> extends SerializationFactory<E> implements GloballyDefined {

    public static final EnumsConstantsSerializationFactory INSTANCE = new EnumsConstantsSerializationFactory();

    private EnumsConstantsSerializationFactory() {
        super("_EC");
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
        Serializer serializer = StringConstantsSerializationFactory.INSTANCE.createSerializer(os, new TypeDescriptor(String.class));
        return new Serializer<E1>() {
            @Override
            public void serialize(E1 o) throws IOException {
                serializer.serialize(o == null ? null : o.name());
            }
        };
    }

    @Override
    public Deserializer createDeserializer(DataReader is) throws IOException {
        Deserializer<String> deserializer = StringConstantsSerializationFactory.INSTANCE.createDeserializer(is);
        return wrap(new Deserializer<Object>() {
            @Override
            public <T> T deserialize(TypeDescriptor<T> type) throws IOException {
                String value = deserializer.deserialize(TypeDescriptor.STRING);
                if (type.getType().isAssignableFrom(String.class)) {
                    return (T) value;
                }
                if (value == null) {
                    return null;
                } else if (type.getType().isEnum()) {
                    return (T) Enum.valueOf((Class) type.getType(), value);
                } else {
                    throw new IOException("Unable to deserialize enum as type " + type.getType().getName());
                }
            }
        }); // TODO: remove code duplication
    }
}
