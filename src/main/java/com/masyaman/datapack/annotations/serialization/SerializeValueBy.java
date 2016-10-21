package com.masyaman.datapack.annotations.serialization;

import com.masyaman.datapack.annotations.InheritFromParent;
import com.masyaman.datapack.serializers.SerializationFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation applied to fields of type Collection, Array or Map. Similar to {@link SerializeBy}, but applied to collection's values.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeValueBy {
    Class<? extends SerializationFactory> value();
    Class serializeAs() default InheritFromParent.class;
    Class annotationsFrom() default InheritFromParent.class;
}
