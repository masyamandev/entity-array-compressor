package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.utils.MathUtils;

import java.io.IOException;
import java.math.RoundingMode;

import static com.masyaman.datapack.utils.MathUtils.median;

abstract class NumberSerializerWrappers<T extends Number> implements Serializer<T> {

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

    public static <E extends Number> Serializer<E> scaleBy(DataWriter dw, Serializer<E> serializer, int decimalScale, RoundingMode roundingMode) throws IOException {
        dw.writeSignedLong((long) decimalScale);
        return new Serializer<E>() {
            @Override
            public void serialize(E o) throws IOException {
                serializer.serialize((E) MathUtils.scale(o, decimalScale, roundingMode));
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

    public static Serializer<Long> medianSerializer(Serializer<Long> longSerializer) {
        return new Serializer<Long>() {
            long prev = 0L;
            long[] diffs = new long[3];
            int pos = 0;
            @Override
            public void serialize(Long o) throws IOException {
                if (o == null) {
                    longSerializer.serialize(null);
                } else {
                    long serializedValue = o.longValue() - (prev + median(diffs));
//                    System.out.println("" + (o.longValue() - prev) + "\t\t" + serializedValue + "\t\t" +
//                            Arrays.asList(diffs[0], diffs[1], diffs[2]) + "\t\t" + median(diffs));
                    longSerializer.serialize(serializedValue);

                    diffs[pos] = o.longValue() - prev;
                    pos = (pos + 1) % diffs.length;

                    prev = o.longValue();
                }
            }
        };
    }
}
