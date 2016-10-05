package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.utils.MathUtils;

import java.io.IOException;
import java.math.RoundingMode;

import static com.masyaman.datapack.utils.MathUtils.median;

final class SerializerWrappers {

    private SerializerWrappers() {}

    public static <E extends Number> Serializer<E> convertFrom(Serializer<Long> longSerializer, TypeDescriptor<E> type) {
        return new Serializer<E>() {
            @Override
            public void serialize(E o) throws IOException {
                longSerializer.serialize(o == null ? null : o.longValue());
            }
        };
    }

    public static <E extends Number> Serializer<E> round(Serializer<Long> serializer, RoundingMode roundingMode) throws IOException {
        return new Serializer<E>() {
            @Override
            public void serialize(E o) throws IOException {
                serializer.serialize(MathUtils.round(o, roundingMode));
            }
        };
    }

    public static <E extends Number> Serializer<E> scaleBy(Serializer<E> serializer, int decimalScale, RoundingMode roundingMode) throws IOException {
        return new Serializer<E>() {
            @Override
            public void serialize(E o) throws IOException {
                serializer.serialize((E) MathUtils.scale(o, decimalScale, roundingMode));
            }
        };
    }

    public static <E extends Number> Serializer<E> scaleByNR(Serializer<Long> serializer, int decimalScale) throws IOException {
        final double scale = Math.pow(10, decimalScale);
        return new Serializer<E>() {
            private long prev = 0;

            @Override
            public void serialize(E o) throws IOException {
                if (o == null) {
                    serializer.serialize(null);
                } else {
                    double val = o.doubleValue() * scale;
                    double diff = val - prev;
                    long diffRounded = (long) diff;
                    prev += diffRounded;
                    serializer.serialize(prev);
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
            private long prev = 0L;
            private long prev2 = 0L;
            private boolean isFirst = true;
            @Override
            public void serialize(Long o) throws IOException {
                if (o == null) {
                    longSerializer.serialize(null);
                } else {
                    longSerializer.serialize(o.longValue() - (prev * 2 - prev2));
                    prev2 = prev;
                    prev = o.longValue();
                    if (isFirst) {
                        isFirst = false;
                        prev2 = prev;
                    }
                }
            }
        };
    }

    public static Serializer<Long> medianSerializer(Serializer<Long> longSerializer, int diffLength) {
        return new Serializer<Long>() {
            private long prev = 0L;
            private long[] diffs = new long[diffLength];
            private int pos = 0;
            private boolean isFirst = true;
            @Override
            public void serialize(Long o) throws IOException {
                if (o == null) {
                    longSerializer.serialize(null);
                } else {
                    long serializedValue = o.longValue() - (prev + median(diffs));

                    longSerializer.serialize(serializedValue);

                    if (isFirst) {
                        isFirst = false;
                    } else {
                        diffs[pos] = o.longValue() - prev;
                        pos = (pos + 1) % diffs.length;
                    }

                    prev = o.longValue();
                }
            }
        };
    }
}
