package com.masyaman.datapack.annotations.deserialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specify Date format when deserialized as String or Json.
 * Additionally to standard date pattern it could be deserialized as Unix Timestamp in seconds or millis.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFormatPattern {

    String MILLIS_FORMAT = "MILLIS";
    String SECONDS_FORMAT = "SECONDS";

    String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    String DEFAULT_TZ = "UTC";

    String format() default DEFAULT_FORMAT;
    String timezone() default DEFAULT_TZ;
}
