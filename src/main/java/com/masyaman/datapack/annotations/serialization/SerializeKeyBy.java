package com.masyaman.datapack.annotations.serialization;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;
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


    @SerializeKeyBy(SerializationFactory.class)
    class Instance extends AbstractAnnotationInstance implements SerializeKeyBy {

        private final Class<? extends SerializationFactory> value;
        private final Class serializeAs;
        private final Class annotationsFrom;

        public Instance(Class<? extends SerializationFactory> value) {
            this(value, InheritFromParent.class, InheritFromParent.class);
        }

        public Instance(Class<? extends SerializationFactory> value, Class serializeAs, Class annotationsFrom) {
            this.value = value;
            this.serializeAs = serializeAs;
            this.annotationsFrom = annotationsFrom;
        }

        @Override
        public Class<? extends SerializationFactory> value() {
            return value;
        }

        @Override
        public Class serializeAs() {
            return serializeAs;
        }

        @Override
        public Class annotationsFrom() {
            return annotationsFrom;
        }
    }
}
