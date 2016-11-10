package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.utils.MathUtils;

import java.io.IOException;
import java.math.RoundingMode;

import static com.masyaman.datapack.reflection.TypeDescriptor.*;
import static com.masyaman.datapack.utils.MathUtils.median;

final class DeserializerWrappers {

    private DeserializerWrappers() {}

    public static Deserializer convertTo(Deserializer<Long> deserializer, TypeDescriptor type) {
        return new Deserializer<Number>() {
            @Override
            public <T extends Number> T deserialize(TypeDescriptor<T> unused) throws IOException {
                Long val = deserializer.deserialize(LONG);
                return (T) MathUtils.convertToType(val, type);
            }
        };
    }

    public static <E extends Number> Deserializer<E> scaleBy(Deserializer<E> deserializer, int decimalScale, RoundingMode roundingMode) throws IOException {
        return new Deserializer<E>() {
            @Override
            public <T extends E> T deserialize(TypeDescriptor<T> type) throws IOException {
                return MathUtils.scale(deserializer.deserialize(type), decimalScale, roundingMode);
            }
        };
    }

    public static Deserializer<Long> diffDeserializer(Deserializer<Long> deserializer) {
        return new Deserializer<Long>() {
            private long prev = 0L;
            @Override
            public Long deserialize(TypeDescriptor type) throws IOException {
                Long val = deserializer.deserialize(LONG);
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
            private boolean isFirst = true;
            @Override
            public Long deserialize(TypeDescriptor type) throws IOException {
                Long val = deserializer.deserialize(LONG);
                if (val == null) {
                    return null;
                }
                val += prev * 2 - prev2;
                prev2 = prev;
                prev = val;
                if (isFirst) {
                    isFirst = false;
                    prev2 = prev;
                }
                return val;
            }
        };
    }

    public static Deserializer<Long> medianDeserializer(Deserializer<Long> deserializer, int diffLength) {
        return new Deserializer<Long>() {
            private long prev = 0L;
            private long[] diffs = new long[diffLength];
            private int pos = 0;
            private boolean isFirst = true;
            @Override
            public Long deserialize(TypeDescriptor type) throws IOException {
                Long val = deserializer.deserialize(LONG);
                if (val == null) {
                    return null;
                }
                val += prev + median(diffs);

                if (isFirst) {
                    isFirst = false;
                } else {
                    diffs[pos] = val.longValue() - prev;
                    pos = (pos + 1) % diffs.length;
                }

                prev = val;
                return val;
            }
        };
    }
}
