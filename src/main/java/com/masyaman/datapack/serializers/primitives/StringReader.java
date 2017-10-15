package com.masyaman.datapack.serializers.primitives;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static com.masyaman.datapack.reflection.TypeDescriptor.LONG;

public class StringReader implements Deserializer<String> {

    private InputStream is;
    private Deserializer<Long> lengthReader;
    private Charset charset;

    public StringReader(InputStream is, Deserializer<Long> lengthReader, Charset charset) {
        this.is = is;
        this.lengthReader = lengthReader;
        this.charset = charset;
    }

    @Override
    public String deserialize(TypeDescriptor unused) throws IOException {
        Long length = lengthReader.deserialize(LONG);
        if (length == null) {
            return null;
        }
        int len = length.intValue();
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) is.read();
        }
        return new String(bytes, charset);
    }
}
