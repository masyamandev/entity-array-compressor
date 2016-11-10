package com.masyaman.datapack.serializers.formats;

import com.masyaman.datapack.annotations.deserialization.AsJson;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.utils.MathUtils;

import java.io.IOException;

public class FormatsDeserializerWrappers {

    public static Deserializer wrapNumber(Deserializer deserializer) throws IOException {
        return new Deserializer<Object>() {
            @Override
            public <T> T deserialize(TypeDescriptor<T> type) throws IOException {
                Object val = deserializer.deserialize(type);
                if (val == null) {
                    return null;
                } else if (type.getAnnotation(AsJson.class) != null && type.getAnnotation(AsJson.class).numbersAsStrings()) {
                    return (T) toJsonString(val.toString());
                } if (Number.class.isAssignableFrom(type.getType()) && val instanceof Number) {
                    return (T) MathUtils.convertToType((Number) val, (TypeDescriptor) type);
                } else if (String.class.isAssignableFrom(type.getType())) {
                    return (T) val.toString();
                } else {
                    return (T) val;
                }
            }
        };
    }

    public static Deserializer wrap(Deserializer deserializer) throws IOException {
        return new Deserializer<Object>() {
            @Override
            public <T> T deserialize(TypeDescriptor<T> type) throws IOException {
                Object val = deserializer.deserialize(type);
                if (val == null) {
                    return null;
                } else if (type.getAnnotation(AsJson.class) != null) {
                    return (T) toJsonString(val.toString());
                } if (String.class.isAssignableFrom(type.getType())) {
                    return (T) val.toString();
                } else {
                    return (T) val;
                }
            }
        };
    }

    public static String toJsonString(String val) {
        return "\"" + val.toString().replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
