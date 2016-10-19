package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;

class UnknownTypeSerializer<T> implements Serializer<T> {

    private DataWriter os;
    private TypeDescriptor<T> type;

    public UnknownTypeSerializer(DataWriter os, TypeDescriptor<T> type) {
        this.os = os;
        this.type = type;
    }

    @Override
    public void serialize(T o) throws IOException {
        os.writeObject(o); // No type here is required
    }
}
