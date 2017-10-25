package com.masyaman.datapack.annotations.deserialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Field name could be specified to add original Class type. Currently supports deserialization objects to Json and Map.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeFieldName {

    String NO_FIELD_TYPE = "";

    String typeField() default NO_FIELD_TYPE;
}
