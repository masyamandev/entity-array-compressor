package com.masyaman.datapack.annotations.deserialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DateFormatPattern {

    String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    String DEFAULT_TZ = "UTC";

    String format() default DEFAULT_FORMAT;
    String timezone() default DEFAULT_TZ;
}
