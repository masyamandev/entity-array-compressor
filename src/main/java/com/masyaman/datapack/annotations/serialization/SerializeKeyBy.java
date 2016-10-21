package com.masyaman.datapack.annotations.serialization;

import com.masyaman.datapack.annotations.InheritFromParent;
import com.masyaman.datapack.serializers.SerializationFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation applied to fields of type Map. Similar to {@link SerializeBy}, but applied to Map's keys.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeKeyBy {
    Class<? extends SerializationFactory> value();
    Class serializeAs() default InheritFromParent.class;
    Class annotationsFrom() default InheritFromParent.class;
}
