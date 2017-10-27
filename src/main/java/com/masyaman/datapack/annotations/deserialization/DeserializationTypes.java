package com.masyaman.datapack.annotations.deserialization;

import com.masyaman.datapack.reflection.TypeDescriptor;

public final class DeserializationTypes {

    public static final TypeDescriptor<String> JSON_TYPE = new TypeDescriptor(String.class, new AsJson.Instance());
    public static final TypeDescriptor<String> JSON_WITH_TYPES_TYPE = new TypeDescriptor(String.class, new AsJson.Instance(false), new TypeFieldName.Instance("type"));

    private DeserializationTypes() {}
}
