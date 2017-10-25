package com.masyaman.datapack.annotations.deserialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Deserialize objects as Json Strings. Field name could be specified to add original Class type using {@link TypeFieldName}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AsJson {
    boolean numbersAsStrings() default false;
}
