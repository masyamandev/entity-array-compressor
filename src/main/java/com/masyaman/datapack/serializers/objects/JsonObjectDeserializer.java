package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.annotations.deserialization.AsJson;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.objects.ObjectDeserializer.FieldDeserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.List;

import static com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers.toJsonString;

class JsonObjectDeserializer implements Deserializer<String> {


    private DataReader is;

    private String className;
    private List<FieldDeserializer> deserialization;

    public JsonObjectDeserializer(DataReader is, String className, List<FieldDeserializer> deserialization) {
        this.is = is;
        this.className = className;
        this.deserialization = deserialization;
    }

    @Override
    public <T extends String> T deserialize(TypeDescriptor<T> type) throws IOException {

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        boolean allNulls = true;

        String classField = "";
        if (type.getAnnotation(AsJson.class) != null) {
            classField = type.getAnnotation(AsJson.class).typeField();
        }

        for (FieldDeserializer fieldDeserializer : deserialization) {
            Object field = fieldDeserializer.getDeserializer().deserialize(type);
            allNulls &= field == null;

            if (sb.length() > 1) {
                sb.append(",");
            }
            sb.append(toJsonString(fieldDeserializer.getFieldName()));
            sb.append(":");
            sb.append(field);

            if (fieldDeserializer.getFieldName().equals(classField)) {
                classField = "";
            }
        }

        if (allNulls && is.readUnsignedLong() == null) {
            return (T) "null";
        }

        if (!classField.isEmpty()) {
            if (sb.length() > 1) {
                sb.append(",");
            }
            sb.append(toJsonString(classField) + ":" + toJsonString(className));
        }

        sb.append("}");

        return (T) sb.toString();
    }

}
