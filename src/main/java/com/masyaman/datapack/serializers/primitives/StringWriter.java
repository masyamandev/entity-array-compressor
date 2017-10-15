package com.masyaman.datapack.serializers.primitives;

import com.masyaman.datapack.serializers.Serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class StringWriter implements Serializer<String> {

    private OutputStream os;
    private Serializer<Long> lengthWriter;
    private Charset charset;

    public StringWriter(OutputStream os, Serializer<Long> lengthWriter, Charset charset) {
        this.os = os;
        this.lengthWriter = lengthWriter;
        this.charset = charset;
    }

    @Override
    public void serialize(String s) throws IOException {
        if (s == null) {
           lengthWriter.serialize(null);
           return;
        }
        byte[] bytes = s.getBytes(charset);
        lengthWriter.serialize((long) bytes.length);
        for (byte b : bytes) {
            os.write(b);
        }

    }
}
