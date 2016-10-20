package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.annotations.deserialization.AsJson;
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

    private String typeField;

    public JsonObjectDeserializer(DataReader is, TypeDescriptor<?> type) throws IOException {
        this.is = is;

        String classField = null;
        String className = is.readString();
        if (type.getAnnotation(AsJson.class) != null) {
            classField =  type.getAnnotation(AsJson.class).typeField();
            if (classField.isEmpty()) {
                classField = null;
            }
        }

        Long fieldsNum = is.readUnsignedLong();
        for (int i = 0; i < fieldsNum; i++) {
            String fieldName = is.readString();
            if (fieldName.equals(classField)) {
                classField = null;
            }

            Deserializer deserializer = is.createAndRegisterDeserializer(type);

            deserializers.add(deserializer);
            fields.add(toJsonString(fieldName));
        }

        if (classField != null) {
            typeField = toJsonString(classField) + ":" + toJsonString(className);
        }
    }

    @Override
    public String deserialize() throws IOException {

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        boolean allNulls = true;

        if (typeField != null) {
            sb.append(typeField);
        }

        for (int i = 0; i < fields.size(); i++) {
            Object field = deserializers.get(i).deserialize();
            allNulls &= field == null;

            if (sb.length() > 1) {
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
