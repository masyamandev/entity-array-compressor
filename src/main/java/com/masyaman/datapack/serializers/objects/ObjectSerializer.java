package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.annotations.IgnoredField;
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
    private List<SerializationData> serializations = new ArrayList<>();


    public ObjectSerializer(DataWriter os, TypeDescriptor type) throws IOException {
        this.os = os;

        Class clazz = type.getType();
        os.writeString(clazz.getName());
//        System.out.println("Serializing " + clazz.getName());

        Map<String, Getter> getterMap = ClassUtils.getterMap(clazz);
        for (Map.Entry<String, Getter> getterEntry : getterMap.entrySet()) {
            Getter<?> getter = getterEntry.getValue();

            if (getter.type().getAnnotation(IgnoredField.class) != null) {
                continue;
            }

            SerializeBy declared = getter.type().getAnnotation(SerializeBy.class);
            TypeDescriptor declaredType = new TypeDescriptor(serializeAs(declared, getter.type().getType()),
                    getter.type().getParametrizedType(),
                    annotationsFrom(declared, getter.type().getAnnotations()));

            SerializationFactory serializationFactory = declared != null ? getInstance(declared.value()) :
                    os.getSerializationFactoryLookup().getSerializationFactory(declaredType);
            if (serializationFactory == UnsupportedSerializationFactory.INSTANCE) {
                throw new IOException("Unable to find serializer for " + clazz.getName() + "." + getterEntry.getKey());
            } else if (serializationFactory == null) {
                serializationFactory = UnknownTypeSerializationFactory.INSTANCE;
            }

            serializations.add(new SerializationData(getterEntry.getKey(), getter, serializationFactory, declaredType));
        }

        os.writeUnsignedLong((long) serializations.size());
        for (SerializationData serialization : serializations) {
            os.writeString(serialization.fieldName);
            serialization.serializer = os.createAndRegisterSerializer(serialization.serializationFactory, serialization.declaredType);
        }
    }

    @Override
    public void serialize(T o) throws IOException {
        if (o == null) {
            for (SerializationData serialization : serializations) {
                serialization.serializer.serialize(null);
            }
            os.writeUnsignedLong(null); // mark object as null;
        } else {
            boolean allNulls = true;
            for (SerializationData serialization : serializations) {
                try {
                    Object field = serialization.getter.get(o);
                    serialization.serializer.serialize(field);
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

    private static class SerializationData {
        private String fieldName;
        private Getter getter;
        private SerializationFactory serializationFactory;
        private TypeDescriptor declaredType;
        private Serializer serializer;

        public SerializationData(String fieldName, Getter getter, SerializationFactory serializationFactory, TypeDescriptor declaredType) {
            this.fieldName = fieldName;
            this.getter = getter;
            this.serializationFactory = serializationFactory;
            this.declaredType = declaredType;
        }
    }
}
