package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.annotations.Alias;
import com.masyaman.datapack.annotations.serialization.IgnoredField;
import com.masyaman.datapack.annotations.serialization.SerializeBy;
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

        Class<?> clazz = type.getType();
        Class<?> mixInClass = os.getClassManager().getMixInClass(clazz);

        if (mixInClass != null && mixInClass.isAnnotationPresent(Alias.class)) {
            os.writeString(mixInClass.getAnnotation(Alias.class).value());
        } else if (clazz.isAnnotationPresent(Alias.class)){
            os.writeString(clazz.getAnnotation(Alias.class).value());
        } else {
            os.writeString(clazz.getName());
        }

        Map<String, Getter> getterMap = ClassUtils.getterMap(clazz, os.getClassManager());
        for (Map.Entry<String, Getter> getterEntry : getterMap.entrySet()) {
            Getter<?> getter = getterEntry.getValue();

            if (getter.type().getAnnotation(IgnoredField.class) != null) {
                continue;
            }

            SerializeBy declared = getter.type().getAnnotation(SerializeBy.class);
            TypeDescriptor declaredType = new TypeDescriptor(serializeAs(declared, getter.type().getType()),
                    getter.type().getParametrizedType(),
                    annotationsFrom(declared, getter.type().getAnnotations()));

            boolean isSpecifiedType = serializeAs(declared, null) != null || declaredType.isFinal();

            SerializationFactory serializationFactory;
            try {
                serializationFactory = declared != null ? getInstance(declared.value()) :
                        os.getSerializationFactoryLookup().getSerializationFactory(declaredType, isSpecifiedType);
            } catch (Exception e) {
                throw new IOException("Unable to create serializer for field " + clazz.getName() + "." + getterEntry.getKey(), e);
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
