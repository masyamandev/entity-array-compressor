package com.masyaman.datapack.annotations.serialization;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;
import com.masyaman.datapack.annotations.InheritFromParent;
import com.masyaman.datapack.serializers.SerializationFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use specified serializer and it's settings.
 * Common use case is to annotate field to override default serializer for better compression or performance.
 * Specified Serializer must be compatible with field type and actual data.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeBy {
    /**
     * Specify SerializationFactory.
     */
    Class<? extends SerializationFactory> value();

    /**
     * Override default type of a field.
     * E.g. when a field has type Object, but it's known that real type will be some type, it's possible to explicitly
     * specify type.
     * Another use case it to specify exact type of a field and this will omit object inheritance.
     */
    Class serializeAs() default InheritFromParent.class;

    /**
     * Get annotations for annotated field from specified class's annotations.
     */
    Class annotationsFrom() default InheritFromParent.class;



    @SerializeBy(SerializationFactory.class)
    class Instance extends AbstractAnnotationInstance implements SerializeBy {

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
