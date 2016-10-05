package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;

import java.io.IOException;
import java.util.Date;

final class DeserializerWrappers {

    private DeserializerWrappers() {}

    public static <E> Deserializer<E> convertTo(Deserializer<Long> deserializer, TypeDescriptor<E> type) {
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
        } else {
            throw new IllegalArgumentException("Class " + type.getType().getName() + " is not supported");
        }
    }
}
