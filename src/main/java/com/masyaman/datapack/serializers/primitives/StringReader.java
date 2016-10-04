package com.masyaman.datapack.serializers.primitives;

import com.masyaman.datapack.serializers.Deserializer;

import java.io.IOException;
import java.io.InputStream;

import static com.masyaman.datapack.utils.Constants.CHARSET;

public class StringReader implements Deserializer<String> {

    private InputStream is;
    private Deserializer<Long> lengthReader;

    public StringReader(InputStream is, Deserializer<Long> lengthReader) {
        this.is = is;
        this.lengthReader = lengthReader;
    }

    @Override
    public String deserialize() throws IOException {
        Long length = lengthReader.deserialize();
        if (length == null) {
            return null;
        }
        int len = length.intValue();
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) is.read();
        }
        return new String(bytes, CHARSET);
    }
}
