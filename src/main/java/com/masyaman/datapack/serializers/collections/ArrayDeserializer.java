package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.lang.reflect.Array;

class ArrayDeserializer implements Deserializer<Object[]> {

    private DataReader is;
    private TypeDescriptor valueType;
    private Deserializer valueDeserializer;

    public ArrayDeserializer(DataReader is, TypeDescriptor valueType) throws IOException {
        this.is = is;
        this.valueType = valueType;
        valueDeserializer = is.createAndRegisterDeserializer(valueType);
    }

    @Override
    public Object[] deserialize() throws IOException {
        Long length = is.readUnsignedLong();
        if (length == null) {
            return null;
        }
        int len = length.intValue();
        Object[] array = (Object[]) Array.newInstance(valueType.getType(), len);

        for (int i = 0; i < len; i++) {
            array[i] = valueDeserializer.deserialize();
        }
        return array;
    }
}
