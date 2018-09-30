package com.masyaman.datapack.serializers.primitives;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;

import java.io.IOException;
import java.io.InputStream;

import static com.masyaman.datapack.serializers.primitives.Constants.*;

public class SignedLongReader implements Deserializer<Long> {

    private InputStream is;

    public SignedLongReader(InputStream is) {
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

        if ((b & NEGATIVE_BYTE_MASK) != 0) {
            result = (~result) | (NEGATIVE_BYTE_MASK << (7 * bytesToRead));
        }

        return result;
    }
}
