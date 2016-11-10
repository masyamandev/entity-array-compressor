package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;

class JsonCollectionDeserializer implements Deserializer<String> {

    private DataReader is;
    private Deserializer<String> valueDeserializer;

    public JsonCollectionDeserializer(DataReader is, Deserializer valueDeserializer) {
        this.is = is;
        this.valueDeserializer = valueDeserializer;
    }

    @Override
    public String deserialize(TypeDescriptor type) throws IOException {
        Long length = is.readUnsignedLong();
        if (length == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        int len = length.intValue();
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(valueDeserializer.deserialize(type));
        }

        sb.append("]");

        return sb.toString();
    }
}
