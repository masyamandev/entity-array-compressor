package com.masyaman.datapack.annotations.deserialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AsJson {

    String NO_FIELD_TYPE = "";

    boolean numbersAsStrings() default false;
    String typeField() default NO_FIELD_TYPE;
}
