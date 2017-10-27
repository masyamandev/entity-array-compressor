package com.masyaman.datapack.annotations.deserialization;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;

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


    @DateFormatPattern
    class Instance extends AbstractAnnotationInstance implements DateFormatPattern {

        private String format;
        private String timezone;

        public Instance() {
            this(DateFormatPattern.DEFAULT_FORMAT, DateFormatPattern.DEFAULT_TZ);
        }

        public Instance(String format) {
            this(format, DateFormatPattern.DEFAULT_TZ);
        }
        public Instance(String format, String timezone) {
            this.format = format;
            this.timezone = timezone;
        }

        public Instance(DateFormatPattern toClone) {
            this(toClone.format(), toClone.timezone());
        }

        @Override
        public String format() {
            return format;
        }

        @Override
        public String timezone() {
            return timezone;
        }

        public Instance setFormat(String format) {
            this.format = format;
            return this;
        }

        public Instance setTimezone(String timezone) {
            this.timezone = timezone;
            return this;
        }
    }
}
