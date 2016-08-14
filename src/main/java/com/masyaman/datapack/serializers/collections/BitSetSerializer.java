package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.BitSet;

class BitSetSerializer implements Serializer<BitSet> {

    private DataWriter os;

    public BitSetSerializer(DataWriter os) {
        this.os = os;
    }

    @Override
    public void serialize(BitSet s) throws IOException {
        if (s == null) {
           os.writeUnsignedLong(null);
           return;
        }
        byte[] bytes = s.toByteArray();
        os.writeUnsignedLong((long) bytes.length);
        for (byte b : bytes) {
            os.writeByte(b);
        }

    }
}
