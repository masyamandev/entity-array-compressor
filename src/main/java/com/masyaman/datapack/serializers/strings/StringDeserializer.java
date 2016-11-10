package com.masyaman.datapack.serializers.strings;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;

public class StringDeserializer implements Deserializer<String> {

    private DataReader is;

    public StringDeserializer(DataReader is) {
        this.is = is;
    }

    @Override
    public String deserialize(TypeDescriptor unused) throws IOException {
        return is.readString();
    }
}
