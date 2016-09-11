package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

public class UnsignedLongSerializer implements Serializer<Long> {

    private DataWriter os;

    public UnsignedLongSerializer(DataWriter os) {
        this.os = os;
    }

    @Override
    public void serialize(Long l) throws IOException {
        if (l == null) {
           os.writeByte(0x7F); // 127 in single byte representation
           return;
        }
        int minBytes = l >= 127 ? 2 : 1; // preserve 127 for 1-byte null value
        for (int i = minBytes; i <= 8; i++) {
            int shift = 64 - 7 * i;
//            if (((l << shift) >> shift) == l) {
            if ((l & ~(0x8000000000000000L >> (shift - 1))) == l) {
                int prefix = 0xFFFFFF00 >> (i - 1);
                os.writeByte((byte) (prefix | (l >> ((i - 1) * 8)) & ~(prefix >> 1)));
                for (int j = i - 2; j >= 0; j--) {
                    os.writeByte((byte) (l >> (j * 8)));
                }
                return;
            }
        }
        os.writeByte(0xFF);
        for (int i = 0; i < 8; i++) {
            os.writeByte((byte) (l >> (56 - i * 8)));
        }
    }
}
