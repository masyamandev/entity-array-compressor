package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.utils.MathUtils;

import java.io.IOException;
import java.math.RoundingMode;

import static com.masyaman.datapack.utils.MathUtils.median;

final class DeserializerWrappers {

    private DeserializerWrappers() {}

    public static <E extends Number> Deserializer<E> convertTo(Deserializer<? extends Number> deserializer, TypeDescriptor<E> type) {
        if (type.getType().isAssignableFrom(Long.class) || long.class.isAssignableFrom(type.getType()) || Long.class.isAssignableFrom(type.getType())) {
            return (Deserializer<E>) deserializer;
        } else if (int.class.isAssignableFrom(type.getType()) || Integer.class.isAssignableFrom(type.getType())) {
            return new Deserializer<E>() {
                @Override
                public E deserialize() throws IOException {
                    Number val = deserializer.deserialize();
                    return (E) (val == null ? null : val.intValue());
                }
            };
        } else if (double.class.isAssignableFrom(type.getType()) || Double.class.isAssignableFrom(type.getType())) {
            return new Deserializer<E>() {
                @Override
                public E deserialize() throws IOException {
                    Number val = deserializer.deserialize();
                    return (E) (val == null ? null : val.doubleValue());
                }
            };
        } else if (float.class.isAssignableFrom(type.getType()) || Float.class.isAssignableFrom(type.getType())) {
            return new Deserializer<E>() {
                @Override
                public E deserialize() throws IOException {
                    Number val = deserializer.deserialize();
                    return (E) (val == null ? null : val.floatValue());
                }
            };
        } else {
            throw new IllegalArgumentException("Class " + type.getType().getName() + " is not supported");
        }
    }

    public static <E extends Number> Deserializer<E> scaleBy(DataReader dr, Deserializer<E> deserializer, RoundingMode roundingMode) throws IOException {
        final int decimalScale = -dr.readSignedLong().intValue();
        return new Deserializer<E>() {
            @Override
            public E deserialize() throws IOException {
                return MathUtils.scale(deserializer.deserialize(), decimalScale, roundingMode);
            }
        };
    }

    public static Deserializer<Long> diffDeserializer(Deserializer<Long> deserializer) {
        return new Deserializer<Long>() {
            private long prev = 0L;
            @Override
            public Long deserialize() throws IOException {
                Long val = deserializer.deserialize();
                if (val == null) {
                    return null;
                }
                val += prev;
                prev = val;
                return val;
            }
        };
    }

    public static Deserializer<Long> linearDeserializer(Deserializer<Long> deserializer) {
        return new Deserializer<Long>() {
            private long prev = 0L;
            private long prev2 = 0L;
            @Override
            public Long deserialize() throws IOException {
                Long val = deserializer.deserialize();
                if (val == null) {
                    return null;
                }
                val += prev * 2 - prev2;
                prev2 = prev;
                prev = val;
                return val;
            }
        };
    }

    public static Deserializer<Long> medianDeserializer(Deserializer<Long> deserializer) {
        return new Deserializer<Long>() {
            long prev = 0L;
            long[] diffs = new long[3];
            int pos = 0;
            @Override
            public Long deserialize() throws IOException {
                Long val = deserializer.deserialize();
                if (val == null) {
                    return null;
                }
                val += prev + median(diffs);

                diffs[pos] = val.longValue() - prev;
                pos = (pos + 1) % diffs.length;

                prev = val;
                return val;
            }
        };
    }
}
