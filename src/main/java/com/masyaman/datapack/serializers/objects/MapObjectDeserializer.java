package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.annotations.deserialization.TypeFieldName;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.objects.ObjectDeserializer.FieldDeserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.masyaman.datapack.reflection.TypeDescriptor.OBJECT;
import static com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers.toJsonString;

class MapObjectDeserializer implements Deserializer<Map> {

    private DataReader is;

    private String className;
    private List<FieldDeserializer> deserialization;

    public MapObjectDeserializer(DataReader is, String className, List<FieldDeserializer> deserialization) {
        this.is = is;
        this.className = className;
        this.deserialization = deserialization;
    }

    @Override
    public <T extends Map> T deserialize(TypeDescriptor<T> type) throws IOException {

        Map map = new HashMap();

        boolean allNulls = true;

        String classField = "";
        if (type.getAnnotation(TypeFieldName.class) != null) {
            classField = type.getAnnotation(TypeFieldName.class).typeField();
        }

        for (FieldDeserializer fieldDeserializer : deserialization) {
            Object field = fieldDeserializer.getDeserializer().deserialize(OBJECT);
            allNulls &= field == null;

            map.put(fieldDeserializer.getFieldName(), field);

            if (fieldDeserializer.getFieldName().equals(classField)) {
                classField = "";
            }
        }

        if (allNulls && is.readUnsignedLong() == null) {
            return null;
        }

        if (!classField.isEmpty()) {
            map.put(toJsonString(classField), toJsonString(className));
        }

        return (T) map;
    }

}
