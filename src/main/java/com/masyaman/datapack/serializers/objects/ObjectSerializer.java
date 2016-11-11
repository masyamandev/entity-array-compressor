package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.reflection.Getter;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

class ObjectSerializer<T> implements Serializer<T> {

    private DataWriter os;
    private List<FieldSerializer> fieldSerializers;

    public ObjectSerializer(DataWriter os, List<FieldSerializer> fieldSerializers) {
        this.os = os;
        this.fieldSerializers = fieldSerializers;
    }

    @Override
    public void serialize(T o) throws IOException {
        if (o == null) {
            for (FieldSerializer fieldSerializer : fieldSerializers) {
                fieldSerializer.serializer.serialize(null);
            }
            os.writeUnsignedLong(null); // mark object as null;
        } else {
            boolean allNulls = true;
            for (FieldSerializer fieldSerializer : fieldSerializers) {
                try {
                    Object field = fieldSerializer.getGetter().get(o);
                    fieldSerializer.serializer.serialize(field);
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

    public static class FieldSerializer implements Comparable<FieldSerializer> {
        private String fieldName;
        private Getter getter;
        private SerializationFactory serializationFactory;
        private TypeDescriptor declaredType;
        private Serializer serializer;

        public FieldSerializer(String fieldName, Getter getter, SerializationFactory serializationFactory, TypeDescriptor declaredType) {
            this.fieldName = fieldName;
            this.getter = getter;
            this.serializationFactory = serializationFactory;
            this.declaredType = declaredType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Getter getGetter() {
            return getter;
        }

        public SerializationFactory getSerializationFactory() {
            return serializationFactory;
        }

        public TypeDescriptor getDeclaredType() {
            return declaredType;
        }

        public Serializer getSerializer() {
            return serializer;
        }

        public void setSerializer(Serializer serializer) {
            this.serializer = serializer;
        }

        @Override
        public String toString() {
            return fieldName + ": " + declaredType.getType().getSimpleName();
        }

        @Override
        public int compareTo(FieldSerializer o) {
            int compare;
            compare = serializationFactory.getName().compareTo(serializationFactory.getName());
            if (compare != 0) {
                return compare;
            }
            compare = Integer.compare(typeSort(), o.typeSort());
            if (compare != 0) {
                return compare;
            }
            return declaredType.getType().getName().compareToIgnoreCase(o.declaredType.getType().getName());
        }

        private int typeSort() {
            Class type = declaredType.getType();
            if (type.isEnum()) {
                return 0;
            } else if (type == int.class || type == Integer.class) {
                return 1;
            } else if (type == long.class || type == Long.class) {
                return 2;
            } else if (type == float.class || type == Float.class || type == double.class || type == Double.class) {
                return 3;
            } else if (type.isPrimitive() || Number.class.isAssignableFrom(type)) {
                return 4;
            } else if (Date.class.isAssignableFrom(type)) {
                return 5;
            } else if (String.class.isAssignableFrom(type)) {
                return 6;
            } else if (Map.class.isAssignableFrom(type)) {
                return 19;
            } else if (Collection.class.isAssignableFrom(type)) {
                return 18;
            } else if (type.isArray()) {
                return 17;
            } else {
                return 10;
            }
        }
    }
}
