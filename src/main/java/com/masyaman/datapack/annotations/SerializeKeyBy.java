package com.masyaman.datapack.annotations;

import com.masyaman.datapack.serializers.SerializationFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeKeyBy {
    Class<? extends SerializationFactory> value();
}
