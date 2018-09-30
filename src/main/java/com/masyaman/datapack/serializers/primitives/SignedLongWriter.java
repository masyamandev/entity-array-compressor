package com.masyaman.datapack.serializers.primitives;

import com.masyaman.datapack.serializers.Serializer;

import java.io.IOException;
import java.io.OutputStream;

import static com.masyaman.datapack.serializers.primitives.Constants.*;

public class SignedLongWriter implements Serializer<Long> {

    private OutputStream os;

    public SignedLongWriter(OutputStream os) {
        this.os = os;
    }

    @Override
    public void serialize(Long l) throws IOException {
        if (l == null) {
            os.write(NULL_VALUE);
            return;
        }

        long value = l;

        if (value == -64L) {
            os.write(0b10111111);
            os.write(0b01000000);
            return;
        }

        boolean isNegative = value < 0;
        if (isNegative) {
            value = ~value;
        }

        while (true) {
            long next = value & SEVEN_BITS_MASK;
            value = (value >>> 7);// & BIT_MASK;
            if (value != 0L || (next & ~0b00111111L) != 0) {
                next |= MORE_BYTES_MASK;
                os.write((byte) next);
            } else {
                if (isNegative) {
                    next |= NEGATIVE_BYTE_MASK;
                }
                os.write((byte) next);
                return;
            }
        }
    }
}
