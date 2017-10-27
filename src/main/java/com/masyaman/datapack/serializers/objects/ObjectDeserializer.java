package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.Setter;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.masyaman.datapack.reflection.TypeDescriptor.OBJECT;
import static com.masyaman.datapack.settings.SettingsKeys.IGNORE_UNKNOWN_FIELDS;

class ObjectDeserializer<T> implements Deserializer<T> {

    private DataReader is;

    private String className;
    private List<FieldDeserializer> deserialization;

    public ObjectDeserializer(DataReader is, String className, List<FieldDeserializer> deserialization) {
        this.is = is;
        this.className = className;
        this.deserialization = deserialization;
    }

    @Override
    public T deserialize(TypeDescriptor type) throws IOException {

        boolean allNulls = true;

        T object = (T) is.getClassManager().objectFactoryByName(className).create();
        Map<String, Setter> setterMap = is.getClassManager().setterMap(object.getClass());

        for (FieldDeserializer fieldDeserializer : deserialization) {
            Setter setter = setterMap.get(fieldDeserializer.getFieldName());
            if (setter == null) {
                if (is.getSettings().get(IGNORE_UNKNOWN_FIELDS)) {
                    Object field = fieldDeserializer.getDeserializer().deserialize(OBJECT);
                    if (field != null) {
                        allNulls = false;
                    }
                } else {
                    throw new IOException("Unable to find setter for field " + className + "." + fieldDeserializer.fieldName);
                }
            } else {
                Object field = fieldDeserializer.getDeserializer().deserialize(setter.type());
                if (field != null) {
                    try {
                        setter.set(object, field);
                    } catch (ReflectiveOperationException e) {
                        throw new IOException("Unable to set field " + className + "." + fieldDeserializer.fieldName);
                    }
                    allNulls = false;
                }
            }
        }

        if (allNulls && is.readUnsignedLong() == null) {
            return null;
        }

        return object;
    }

    public static class FieldDeserializer {
        private String fieldName;
        private Deserializer deserializer;

        public FieldDeserializer(String fieldName, Deserializer deserializer) {
            this.fieldName = fieldName;
            this.deserializer = deserializer;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Deserializer getDeserializer() {
            return deserializer;
        }
    }
}
