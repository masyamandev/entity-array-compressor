package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;

class UnknownTypeDeserializer<T> implements Deserializer<T> {

    private DataReader is;
    private TypeDescriptor<T> type;

    public UnknownTypeDeserializer(DataReader is, TypeDescriptor<T> type) {
        this.is = is;
        this.type = type;
    }

    @Override
    public T deserialize() throws IOException {
        return is.readObject(type);
    }
}
