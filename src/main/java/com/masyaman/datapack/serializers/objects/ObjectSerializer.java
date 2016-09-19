package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.annotations.SerializeBy;
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

import static com.masyaman.datapack.annotations.AnnotationsHelper.annotationsFrom;
import static com.masyaman.datapack.annotations.AnnotationsHelper.serializeAs;
import static com.masyaman.datapack.serializers.SerializationFactory.getInstance;

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
            Getter<?> getter = getterEntry.getValue();

            SerializeBy declared = getter.type().getAnnotation(SerializeBy.class);
            TypeDescriptor declaredType = new TypeDescriptor(serializeAs(declared, getter.type().getType()),
                    getter.type().getParametrizedType(),
                    annotationsFrom(declared, getter.type().getAnnotations()));

            SerializationFactory serializationFactory = declared != null ? getInstance(declared.value()) :
                    os.getSerializationFactoryLookup().getSerializationFactory(declaredType);
            if (serializationFactory == UnsupportedSerializationFactory.INSTANCE) {
                System.out.println("Unable to find serializer for " + clazz.getName() + "." + getterEntry.getKey());
//                continue;
            } else if (serializationFactory == null) {
                serializationFactory = UnknownTypeSerializationFactory.INSTANCE;
            }
            os.writeString(getterEntry.getKey());
            serializers.add(os.createAndRegisterSerializer(serializationFactory, declaredType));
            getters.add(getter);
        }
    }

    @Override
    public void serialize(T o) throws IOException {
        if (o == null) {
            for (int i = 0; i < getters.size(); i++) {
                serializers.get(i).serialize(null);
            }
            os.writeUnsignedLong(null); // mark object as null;
        } else {
            boolean allNulls = true;
            for (int i = 0; i < getters.size(); i++) {
                try {
                    Object field = getters.get(i).get(o);
                    serializers.get(i).serialize(field);
                    allNulls &= field == null;
                } catch (ReflectiveOperationException e) {
                    throw new IOException("Unable to serialize", e);
                }
            }
            if (allNulls) {
                os.writeUnsignedLong(0L);
            }
        }
    }
}
