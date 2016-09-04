package com.masyaman.datapack.annotations;

import com.masyaman.datapack.serializers.SerializationFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeValueBy {
    Class<? extends SerializationFactory> value();
}
