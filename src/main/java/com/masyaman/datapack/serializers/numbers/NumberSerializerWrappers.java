package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

abstract class NumberSerializerWrappers<T extends Number> implements Serializer<T> {

    public static <E extends Number> Serializer<E> convertFrom(Serializer<Long> longSerializer, TypeDescriptor<E> type) {
        return new Serializer<E>() {
            @Override
            public void serialize(E o) throws IOException {
                longSerializer.serialize(o == null ? null : o.longValue());
            }
        };
    }

    public static <E extends Number> Serializer<E> scaleBy(DataWriter dw, Serializer<Long> serializer, int decimalScale) throws IOException {
        dw.writeSignedLong((long) decimalScale);
        final double scale = Math.pow(10, decimalScale);
        return new Serializer<E>() {
            @Override
            public void serialize(E o) throws IOException {
                serializer.serialize(o == null ? null : Math.round(o.doubleValue() * scale));
            }
        };
    }

    public static <E extends Number> Serializer<E> scaleByNR(DataWriter dw, Serializer<Long> serializer, int decimalScale) throws IOException {
        dw.writeSignedLong((long) decimalScale);
        final double scale = Math.pow(10, decimalScale);
        return new Serializer<E>() {
            private double prev = 0.0;

            @Override
            public void serialize(E o) throws IOException {
                if (o == null) {
                    serializer.serialize(null);
                } else {
                    double val = o.doubleValue() * scale;
                    if (Math.abs(val - prev) < 0.5) {
                        val = prev;
                    }
                    prev = val;
                    serializer.serialize(Math.round(val));
                }
            }
        };
    }

    public static Serializer<Long> diffSerializer(Serializer<Long> longSerializer) {
        return new Serializer<Long>() {
            long prev = 0L;
            @Override
            public void serialize(Long o) throws IOException {
                if (o == null) {
                    longSerializer.serialize(null);
                } else {
                    longSerializer.serialize(o.longValue() - prev);
                    prev = o.longValue();
                }
            }
        };
    }

    public static Serializer<Long> linearSerializer(Serializer<Long> longSerializer) {
        return new Serializer<Long>() {
            long prev = 0L;
            long prev2 = 0L;
            @Override
            public void serialize(Long o) throws IOException {
                if (o == null) {
                    longSerializer.serialize(null);
                } else {
                    longSerializer.serialize(o.longValue() - (prev * 2 - prev2));
                    prev2 = prev;
                    prev = o.longValue();
                }
            }
        };
    }
}
