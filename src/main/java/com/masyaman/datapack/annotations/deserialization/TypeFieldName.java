package com.masyaman.datapack.annotations.deserialization;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Field name could be specified to add original Class type. Currently supports deserialization objects to Json and Map.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeFieldName {

    String NO_FIELD_TYPE = "";

    String typeField() default NO_FIELD_TYPE;


    @TypeFieldName
    class Instance extends AbstractAnnotationInstance implements TypeFieldName {
        private String typeField;

        public Instance(String typeField) {
            this.typeField = typeField;
        }

        @Override
        public String typeField() {
            return typeField;
        }

        public Instance setTypeField(String typeField) {
            this.typeField = typeField;
            return this;
        }
    }
}
