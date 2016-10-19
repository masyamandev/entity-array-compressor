package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;

class JsonCollectionDeserializer implements Deserializer<String> {

    private DataReader is;
    private Deserializer<String> valueDeserializer;

    public JsonCollectionDeserializer(DataReader is, TypeDescriptor type) throws IOException {
        this.is = is;
        valueDeserializer = is.createAndRegisterDeserializer(type);
    }

    @Override
    public String deserialize() throws IOException {
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
            sb.append(valueDeserializer.deserialize());
        }

        sb.append("]");

        return sb.toString();
    }
}
