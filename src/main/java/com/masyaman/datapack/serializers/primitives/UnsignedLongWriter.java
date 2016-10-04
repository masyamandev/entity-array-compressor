package com.masyaman.datapack.serializers.primitives;

import com.masyaman.datapack.serializers.Serializer;

import java.io.IOException;
import java.io.OutputStream;

public class UnsignedLongWriter implements Serializer<Long> {

    private OutputStream os;

    public UnsignedLongWriter(OutputStream os) {
        this.os = os;
    }

    @Override
    public void serialize(Long l) throws IOException {
        if (l == null) {
           os.write(0x7F); // 127 in single byte representation
           return;
        }
        int minBytes = l >= 127 ? 2 : 1; // preserve 127 for 1-byte null value
        for (int i = minBytes; i <= 8; i++) {
            int shift = 64 - 7 * i;
            if ((l & ~(0x8000000000000000L >> (shift - 1))) == l) {
                int prefix = 0xFFFFFF00 >> (i - 1);
                os.write((byte) (prefix | (l >> ((i - 1) * 8)) & ~(prefix >> 1)));
                for (int j = i - 2; j >= 0; j--) {
                    os.write((byte) (l >> (j * 8)));
                }
                return;
            }
        }
        os.write(0xFF);
        for (int i = 0; i < 8; i++) {
            os.write((byte) (l >> (56 - i * 8)));
        }
    }
}
