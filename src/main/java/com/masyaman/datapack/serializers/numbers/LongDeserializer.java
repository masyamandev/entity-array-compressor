package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;

public class LongDeserializer implements Deserializer<Long> {

    private DataReader is;

    public LongDeserializer(DataReader is) {
        this.is = is;
    }

    @Override
    public Long deserialize() throws IOException {
        int b = is.readUnsignedByte();
        if (b == 0x40) {
            return null; // -64 in single byte representation
        }
        int bytesToRead = 0;
        while ((b & (0x80 >> bytesToRead)) != 0) {
            bytesToRead++;
        }
        long result = b & ~(0xFFFFFF80 >> bytesToRead);
        for (int i = 0; i < bytesToRead; i++) {
            result = (result << 8) | is.readUnsignedByte();
        }
        if (bytesToRead < 8) {
            int shift = 64 - 7 * (bytesToRead + 1);
            result = (result << shift) >> shift;
        }
        return result;
    }
}
