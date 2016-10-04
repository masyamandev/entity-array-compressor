package com.masyaman.datapack.serializers.primitives;

import com.masyaman.datapack.serializers.Serializer;

import java.io.IOException;
import java.io.OutputStream;

import static com.masyaman.datapack.utils.Constants.CHARSET;

public class StringWriter implements Serializer<String> {

    private OutputStream os;
    private Serializer<Long> lengthWriter;

    public StringWriter(OutputStream os, Serializer<Long> lengthWriter) {
        this.os = os;
        this.lengthWriter = lengthWriter;
    }

    @Override
    public void serialize(String s) throws IOException {
        if (s == null) {
           lengthWriter.serialize(null);
           return;
        }
        lengthWriter.serialize((long) s.length());
        for (byte b : s.getBytes(CHARSET)) {
            os.write(b);
        }

    }
}
