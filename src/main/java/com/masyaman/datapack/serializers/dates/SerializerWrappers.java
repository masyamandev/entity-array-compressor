package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.utils.MathUtils;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.Date;

final class SerializerWrappers {

    private SerializerWrappers() {}

    public static <E> Serializer<E> convertFrom(Serializer<Long> longSerializer, TypeDescriptor<E> type) {
        if (type.getType().isAssignableFrom(Long.class) || long.class.isAssignableFrom(type.getType()) || Long.class.isAssignableFrom(type.getType())) {
            return (Serializer<E>) longSerializer;
        } else if (Date.class.isAssignableFrom(type.getType())) {
            return new Serializer<E>() {
                @Override
                public void serialize(E o) throws IOException {
                    longSerializer.serialize(o == null ? null : ((Date) o).getTime());
                }
            };
        } else {
            throw new IllegalArgumentException("Class " + type.getType().getName() + " is not supported");
        }
    }

    public static Serializer<Long> scale(Serializer<Long> longSerializer, long scale, RoundingMode roundingMode) {
        return new Serializer<Long>() {
            @Override
            public void serialize(Long o) throws IOException {
                longSerializer.serialize(o == null ? null : MathUtils.divLongs(o, scale, roundingMode));
            }
        };
    }
}
