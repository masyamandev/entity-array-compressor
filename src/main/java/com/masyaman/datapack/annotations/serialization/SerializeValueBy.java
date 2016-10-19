package com.masyaman.datapack.annotations.serialization;

import com.masyaman.datapack.annotations.InheritFromParent;
import com.masyaman.datapack.serializers.SerializationFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeValueBy {
    Class<? extends SerializationFactory> value();
    Class serializeAs() default InheritFromParent.class;
    Class annotationsFrom() default InheritFromParent.class;
}
