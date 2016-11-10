package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.util.*;

class JsonBitSetDeserializer implements Deserializer<String> {

    private DataReader is;

    public JsonBitSetDeserializer(DataReader is) throws IOException {
        this.is = is;
    }

    @Override
    public String deserialize(TypeDescriptor type) throws IOException {
        Long length = is.readUnsignedLong();
        if (length == null) {
            return null;
        }
        int len = length.intValue();
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = is.readByte();
        }
        BitSet bitSet = BitSet.valueOf(bytes);

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = bitSet.nextSetBit(0); i != -1; i = bitSet.nextSetBit(i + 1)) {
            if (sb.length() > 1) {
                sb.append(",");
            }
            sb.append(i);
        }

        sb.append("]");

        return sb.toString();
    }
}
