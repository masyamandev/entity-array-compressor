package com.masyaman.datapack.annotations.serialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Ignore annotated field during serialization.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoredField {
}
