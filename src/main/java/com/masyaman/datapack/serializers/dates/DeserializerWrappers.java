package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.annotations.deserialization.DateFormatPattern;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.masyaman.datapack.reflection.TypeDescriptor.LONG;

final class DeserializerWrappers {

    private DeserializerWrappers() {}

    public static <E> Deserializer convertTo(Deserializer<Long> deserializer) throws IOException {
        return FormatsDeserializerWrappers.wrap(new Deserializer<E>() {
            @Override
            public <T extends E> T deserialize(TypeDescriptor<T> type) throws IOException {
                Long val = deserializer.deserialize(LONG);
                if (val == null) {
                    return null;
                } else if (type.getType().isAssignableFrom(Date.class)) {
                    return (T) new Date(val);
                } else if (type.getType().isAssignableFrom(Long.class) || long.class.isAssignableFrom(type.getType()) || Long.class.isAssignableFrom(type.getType())) {
                    return (T) val;
                } else if (String.class.isAssignableFrom(type.getType())) {
                    DateFormatPattern pattern = type.getAnnotation(DateFormatPattern.class);
                    String format = pattern != null ? pattern.format() : DateFormatPattern.DEFAULT_FORMAT;
                    String tz = pattern != null ? pattern.timezone() : DateFormatPattern.DEFAULT_TZ;

                    if (DateFormatPattern.MILLIS_FORMAT.equals(format)) {
                        return (T) ("" + val.longValue());
                    } else if (DateFormatPattern.SECONDS_FORMAT.equals(format)) {
                        return (T) ("" + val.longValue() / 1000L);
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                        dateFormat.setTimeZone(TimeZone.getTimeZone(tz));
                        return (T) dateFormat.format(new Date(val));
                    }
                } else {
                    throw new IllegalArgumentException("Class " + type.getType().getName() + " is not supported");
                }
            }
        });
    }

    public static Deserializer<Long> scale(Deserializer<Long> deserializer, long scale) {
        return new Deserializer<Long>() {
            @Override
            public Long deserialize(TypeDescriptor type) throws IOException {
                Long val = deserializer.deserialize(LONG);
                return (val == null ? null : val * scale);
            }
        };
    }
}
