package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NumberTypeResolver {
    private static final Map<Class, String> CLASS_TO_TYPE = new HashMap<>();
    private static final Map<String, Class> TYPE_TO_CLASS = new HashMap<>();
    static {
        CLASS_TO_TYPE.put(long.class, "64");
        CLASS_TO_TYPE.put(Long.class, "64");
        CLASS_TO_TYPE.put(int.class, "32");
        CLASS_TO_TYPE.put(Integer.class, "32");
        CLASS_TO_TYPE.put(double.class, "64f");
        CLASS_TO_TYPE.put(Double.class, "64f");
        CLASS_TO_TYPE.put(float.class, "32f");
        CLASS_TO_TYPE.put(Float.class, "32f");

        TYPE_TO_CLASS.put("64", Long.class);
        TYPE_TO_CLASS.put("32", Integer.class);
        TYPE_TO_CLASS.put("64f", Double.class);
        TYPE_TO_CLASS.put("32f", Float.class);

    }

    public static void writeType(DataWriter dw, TypeDescriptor type) throws IOException {
        String typeString = CLASS_TO_TYPE.get(type.getType());
        if (typeString == null) {
            throw new IOException("Unable to serialize type " + type.getType().getCanonicalName() + " as number.");
        }
        dw.writeString(typeString);
    }

    public static TypeDescriptor readType(DataReader dr, TypeDescriptor expectedType) throws IOException {
        String t = dr.readString();
        if (expectedType != null && CLASS_TO_TYPE.containsKey(expectedType.getType())) {
            return expectedType;
        }
        return new TypeDescriptor(TYPE_TO_CLASS.get(t));
    }
}
