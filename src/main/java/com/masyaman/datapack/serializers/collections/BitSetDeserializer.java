package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.BitSet;

class BitSetDeserializer implements Deserializer<BitSet> {

    private DataReader is;

    public BitSetDeserializer(DataReader is) {
        this.is = is;
    }

    @Override
    public BitSet deserialize(TypeDescriptor type) throws IOException {
        Long length = is.readUnsignedLong();
        if (length == null) {
            return null;
        }
        int len = length.intValue();
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = is.readByte();
        }
        return BitSet.valueOf(bytes);
    }
}
