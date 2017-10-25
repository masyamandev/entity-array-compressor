package com.masyaman.datapack.annotations.deserialization;

import com.masyaman.datapack.annotations.deserialization.instances.AsJsonInstance;
import com.masyaman.datapack.annotations.deserialization.instances.TypeFieldNameInstance;
import com.masyaman.datapack.reflection.TypeDescriptor;

public final class DeserializationTypes {

    public static final TypeDescriptor<String> JSON_TYPE = new TypeDescriptor(String.class, new AsJsonInstance());
    public static final TypeDescriptor<String> JSON_WITH_TYPES_TYPE = new TypeDescriptor(String.class, new AsJsonInstance(false), new TypeFieldNameInstance("type"));

    private DeserializationTypes() {}
}
