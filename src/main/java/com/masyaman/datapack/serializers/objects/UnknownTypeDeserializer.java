package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;

class UnknownTypeDeserializer<E> implements Deserializer<E> {

    private DataReader is;

    public UnknownTypeDeserializer(DataReader is) {
        this.is = is;
    }

    @Override
    public <T extends E> T deserialize(TypeDescriptor<T> type) throws IOException {
        return is.readObject(type);
    }
}
