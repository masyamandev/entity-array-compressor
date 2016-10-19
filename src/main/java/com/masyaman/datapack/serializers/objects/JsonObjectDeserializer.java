package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers.toJsonString;

class JsonObjectDeserializer implements Deserializer<String> {

    private DataReader is;

    private List<String> fields = new ArrayList<>();
    private List<Deserializer> deserializers = new ArrayList<>();

    private final String className;

    public JsonObjectDeserializer(DataReader is, TypeDescriptor type) throws IOException {
        this.is = is;

        className = is.readString();

        Long fieldsNum = is.readUnsignedLong();
        for (int i = 0; i < fieldsNum; i++) {
            String fieldName = is.readString();

            Deserializer deserializer = is.createAndRegisterDeserializer(type);

            deserializers.add(deserializer);
            fields.add(toJsonString(fieldName));
        }
    }

    @Override
    public String deserialize() throws IOException {

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        boolean allNulls = true;

        for (int i = 0; i < fields.size(); i++) {
            Object field = deserializers.get(i).deserialize();
            allNulls &= field == null;

            if (i > 0) {
                sb.append(",");
            }
            sb.append(fields.get(i));
            sb.append(":");
            sb.append(field);
        }

        if (allNulls && is.readUnsignedLong() == null) {
            return "null";
        }

        sb.append("}");

        return sb.toString();
    }
}
