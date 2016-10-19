package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.*;

class JsonMapDeserializer implements Deserializer<String> {

    private static final Class[] CLASSES = {ArrayList.class, LinkedList.class,
            HashSet.class, TreeSet.class, LinkedHashSet.class};

    private DataReader is;
    private Deserializer<String> keyDeserializer;
    private Deserializer<String> valueDeserializer;

    public JsonMapDeserializer(DataReader is, TypeDescriptor type) throws IOException {
        this.is = is;
        keyDeserializer = is.createAndRegisterDeserializer(type);
        valueDeserializer = is.createAndRegisterDeserializer(type);
    }

    @Override
    public String deserialize() throws IOException {
        Long length = is.readUnsignedLong();
        if (length == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            keys.add(keyDeserializer.deserialize());
        }
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            String key = keys.get(i);
            if (!key.startsWith("\"")) {
                key = FormatsDeserializerWrappers.toJsonString(key); // workaround for wrapping key as string
            }
            sb.append(key);
            sb.append(":");
            sb.append(valueDeserializer.deserialize());
        }

        sb.append("}");

        return sb.toString();
    }
}
