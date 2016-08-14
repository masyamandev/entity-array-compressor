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

class ObjectSerializer<T> implements Serializer<T> {

    private DataWriter os;
    // TODO make getter with serializer
    private List<Getter> getters = new ArrayList<>();
    private List<Serializer> serializers = new ArrayList<>();

    public ObjectSerializer(DataWriter os, TypeDescriptor type) throws IOException {
        this.os = os;

        Class clazz = type.getType();
        os.writeString(clazz.getName());
//        System.out.println("Serializing " + clazz.getName());

        Map<String, Getter> getterMap = ClassUtils.getterMap(clazz);
        os.writeUnsignedLong((long) getterMap.size());
        for (Map.Entry<String, Getter> getterEntry : getterMap.entrySet()) {
            Getter getter = getterEntry.getValue();
            SerializationFactory serializationFactory = os.getSerializationFactoryLookup().getSerializationFactory(getter.type());
            if (serializationFactory == UnsupportedSerializationFactory.INSTANCE) {
                System.out.println("Unable to find serializer for " + clazz.getName() + "." + getterEntry.getKey());
//                continue;
            } else if (serializationFactory == null) {
                serializationFactory = UnknownTypeSerializationFactory.INSTANCE;
            }
            // TODO add unknown serializer
            os.writeString(getterEntry.getKey());
            serializers.add(os.createAndRegisterSerializer(serializationFactory, getter.type()));
            getters.add(getter);
        }
    }

    @Override
    public void serialize(T o) throws IOException {
        for (int i = 0; i < getters.size(); i++) {
            try {
                serializers.get(i).serialize(getters.get(i).get(o));
            } catch (ReflectiveOperationException e) {
                throw new IOException("Unable to serialize", e);
            }
        }
    }
}
