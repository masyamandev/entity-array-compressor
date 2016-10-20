package com.masyaman.datapack.annotations.deserialization.instances;

import com.masyaman.datapack.annotations.AbstractAnnotationInstance;
import com.masyaman.datapack.annotations.deserialization.DateFormatPattern;

@DateFormatPattern
public class DateFormatPatternInstance extends AbstractAnnotationInstance implements DateFormatPattern {

    private String format;
    private String timezone;

    public DateFormatPatternInstance() {
        this(DateFormatPattern.DEFAULT_FORMAT, DateFormatPattern.DEFAULT_TZ);
    }

    public DateFormatPatternInstance(String format, String timezone) {
        this.format = format;
        this.timezone = timezone;
    }

    public DateFormatPatternInstance(DateFormatPattern toClone) {
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

    public DateFormatPatternInstance setFormat(String format) {
        this.format = format;
        return this;
    }

    public DateFormatPatternInstance setTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }
}
