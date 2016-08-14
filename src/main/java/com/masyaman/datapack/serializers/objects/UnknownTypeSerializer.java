package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.ClassUtils;
import com.masyaman.datapack.reflection.Getter;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
