package com.masyaman.datapack.serializers.formats;

import com.masyaman.datapack.annotations.deserialization.AsJson;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;

import java.io.IOException;

public class FormatsDeserializerWrappers {

    public static <T> Deserializer<T> wrapNumber(Deserializer deserializer, TypeDescriptor<T> type) throws IOException {
        if (type.getAnnotation(AsJson.class) != null && type.getAnnotation(AsJson.class).numbersAsStrings()) {
            return (Deserializer<T>) toJsonString(deserializer);
        } if (String.class.isAssignableFrom(type.getType())) {
            return (Deserializer<T>) toString(deserializer);
        } else {
            return deserializer;
        }
    }

    public static <T> Deserializer<T> wrapDate(Deserializer deserializer, TypeDescriptor<T> type) throws IOException {
        if (type.getAnnotation(AsJson.class) != null) {
            return (Deserializer<T>) toJsonString(deserializer);
        } else {
            return deserializer;
        }
    }

    public static <T> Deserializer<T> wrap(Deserializer deserializer, TypeDescriptor<T> type) throws IOException {
        if (type.getAnnotation(AsJson.class) != null) {
            return (Deserializer<T>) toJsonString(deserializer);
        } else {
            return deserializer;
        }
    }

    private static Deserializer<String> toString(Deserializer deserializer) throws IOException {
        return new Deserializer<String>() {
            @Override
            public String deserialize() throws IOException {
                Object val = deserializer.deserialize();
                return val == null ? null : val.toString();
            }
        };
    }

    private static Deserializer<String> toJsonString(Deserializer deserializer) throws IOException {
        return new Deserializer<String>() {
            @Override
            public String deserialize() throws IOException {
                Object val = deserializer.deserialize();
                return val == null ? null : toJsonString(val.toString());
            }
        };
    }

    public static String toJsonString(String val) {
        return "\"" + val.toString().replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
