package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.formats.FormatsDeserializerWrappers;

import java.io.IOException;
import java.util.Date;

final class DeserializerWrappers {

    private DeserializerWrappers() {}

    public static <E> Deserializer<E> convertTo(Deserializer<Long> deserializer, TypeDescriptor<E> type) throws IOException {
        if (type.getType().isAssignableFrom(Date.class)) {
            return new Deserializer<E>() {
                @Override
                public E deserialize() throws IOException {
                    Long val = deserializer.deserialize();
                    return (E) (val == null ? null : new Date(val));
                }
            };
        } else if (type.getType().isAssignableFrom(Long.class) || long.class.isAssignableFrom(type.getType()) || Long.class.isAssignableFrom(type.getType())) {
            return (Deserializer<E>) deserializer;
        } else if (String.class.isAssignableFrom(type.getType())) {
            // TODO add format
            return (Deserializer<E>) FormatsDeserializerWrappers.wrapDate(deserializer, type);
        } else {
            throw new IllegalArgumentException("Class " + type.getType().getName() + " is not supported");
        }
    }

    public static Deserializer<Long> scale(Deserializer<Long> deserializer, long scale) {
        return new Deserializer<Long>() {
            @Override
            public Long deserialize() throws IOException {
                Long val = deserializer.deserialize();
                return (val == null ? null : val * scale);
            }
        };
    }
}
