package com.masyaman.datapack.serializers.strings;

import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

public class StringSerializer implements Serializer<String> {

    private DataWriter os;

    public StringSerializer(DataWriter os) {
        this.os = os;
    }

    @Override
    public void serialize(String s) throws IOException {
        if (s == null) {
           os.writeUnsignedLong(null);
           return;
        }
        os.writeUnsignedLong((long) s.length());
        // TODO encoding
        for (byte b : s.getBytes()) {
            os.writeByte(b);
        }

    }
}
