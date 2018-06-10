package com.masyaman.datapack.serializers.booleans;

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
public class BooleanSerializationFactory extends SerializationFactory<Boolean> implements GloballyDefined {

    public static final BooleanSerializationFactory INSTANCE = new BooleanSerializationFactory();
    private static final Long ONE = 1L;
    private static final Long ZERO = 0L;

    private BooleanSerializationFactory() {
        super("_B");
    }

    @Override
    public TypeDescriptor<? extends Boolean> getDefaultType() {
        return TypeDescriptor.BOOLEAN;
    }

    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return Boolean.class.isAssignableFrom(type.getType());
    }

    @Override
    public <E extends Boolean> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type) throws IOException {
        return new Serializer<E>() {
            @Override
            public void serialize(Boolean b) throws IOException {
                if (b == null) {
                    os.writeUnsignedLong(null);
                } else {
                    os.writeUnsignedLong(b ? ONE : ZERO);
                }
            }
        };
    }

    @Override
    public Deserializer createDeserializer(DataReader is) throws IOException {
        return wrap(new Deserializer() {
            @Override
            public Object deserialize(TypeDescriptor type) throws IOException {
                Long val = is.readUnsignedLong();
                return val == null ? null : Boolean.valueOf(val.longValue() != 0);
            }
        });
    }
}
