package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

public class UnsignedLongSerializer implements Serializer<Long> {

    private DataWriter os;

    public UnsignedLongSerializer(DataWriter os) {
        this.os = os;
    }

    @Override
    public void serialize(Long l) throws IOException {
        os.writeUnsignedLong(l);
    }
}
