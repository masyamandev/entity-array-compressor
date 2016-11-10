package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.lang.reflect.Array;

class ArrayDeserializer implements Deserializer<Object[]> {

    private DataReader is;
    private Deserializer valueDeserializer;

    public ArrayDeserializer(DataReader is, Deserializer valueDeserializer) {
        this.is = is;
        this.valueDeserializer = valueDeserializer;
    }

    @Override
    public Object[] deserialize(TypeDescriptor type) throws IOException {
        Long length = is.readUnsignedLong();
        if (length == null) {
            return null;
        }
        int len = length.intValue();
        Object[] array = (Object[]) Array.newInstance(type.getType().getComponentType(), len);

        for (int i = 0; i < len; i++) {
            array[i] = valueDeserializer.deserialize(type);
        }
        return array;
    }
}
