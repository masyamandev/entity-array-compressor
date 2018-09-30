package com.masyaman.datapack.serializers.primitives;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;

import java.io.IOException;
import java.io.InputStream;

import static com.masyaman.datapack.serializers.primitives.Constants.MORE_BYTES_MASK;
import static com.masyaman.datapack.serializers.primitives.Constants.NULL_VALUE;
import static com.masyaman.datapack.serializers.primitives.Constants.SEVEN_BITS_MASK;

public class UnsignedLongReader implements Deserializer<Long> {

    private InputStream is;

    public UnsignedLongReader(InputStream is) {
        this.is = is;
    }

    @Override
    public Long deserialize(TypeDescriptor unused) throws IOException {
        int b = is.read();

        if (b == NULL_VALUE) {
            return null;
        }

        int bytesToRead = 0;
        long result = b & SEVEN_BITS_MASK;
        while ((b & MORE_BYTES_MASK) != 0) {
            bytesToRead++;
            b = is.read();
            result |= (b & SEVEN_BITS_MASK) << (7 * bytesToRead);
        }
        return result;
    }
}
