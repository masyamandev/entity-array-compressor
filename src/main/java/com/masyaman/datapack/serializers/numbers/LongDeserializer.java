package com.masyaman.datapack.serializers.numbers;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;

public class LongDeserializer implements Deserializer<Long> {

    private DataReader is;

    public LongDeserializer(DataReader is) {
        this.is = is;
    }

    @Override
    public Long deserialize(TypeDescriptor type) throws IOException {
        return is.readSignedLong();
    }
}
