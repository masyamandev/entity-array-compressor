package com.masyaman.datapack.serializers.primitives;

import com.masyaman.datapack.serializers.Serializer;

import java.io.IOException;
import java.io.OutputStream;

import static com.masyaman.datapack.serializers.primitives.Constants.*;

public class UnsignedLongWriter implements Serializer<Long> {

    private OutputStream os;

    public UnsignedLongWriter(OutputStream os) {
        this.os = os;
    }

    @Override
    public void serialize(Long l) throws IOException {
        if (l == null) {
           os.write(NULL_VALUE);
           return;
        }

        long value = l;

        if (value == NULL_VALUE) {
            os.write(0xFF);
            os.write(0);
            return;
        }

        do {
            long next = value & SEVEN_BITS_MASK;
            value = (value >>> 7);// & BIT_MASK;
            if (value != 0L) {
                next |= MORE_BYTES_MASK;
            }
            os.write((byte) next);
        } while (value != 0L);
    }
}
