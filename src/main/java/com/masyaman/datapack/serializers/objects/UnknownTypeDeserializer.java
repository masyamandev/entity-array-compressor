package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.ClassUtils;
import com.masyaman.datapack.reflection.Setter;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.streams.DataReader;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
