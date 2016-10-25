package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.GloballyDefined;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.primitives.SignedLongWriter;
import com.masyaman.datapack.serializers.primitives.StringWriter;
import com.masyaman.datapack.serializers.primitives.UnsignedLongWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DataWriter implements ObjectWriter {

    protected OutputStream os;

    private Serializer<Long> signedLongSerializer;
    private Serializer<Long> unsignedLongSerializer;
    private Serializer<String> stringSerializer;

    public DataWriter(OutputStream os) throws IOException {
        this.os = os;

        signedLongSerializer = new SignedLongWriter(os);
        unsignedLongSerializer = new UnsignedLongWriter(os);
        stringSerializer = new StringWriter(os, unsignedLongSerializer);
    }

    @Override
    public void close() throws IOException {
        os.close();
    }

    public void writeByte(byte b) throws IOException {
        writeByte((int) b);
    }

    public void writeByte(int b) throws IOException {
        os.write(b);
    }

    public void writeByte(long b) throws IOException {
        writeByte((int) b);
    }

    public void writeSignedLong(Long l) throws IOException {
        signedLongSerializer.serialize(l);
    }

    public void writeUnsignedLong(Long l) throws IOException {
        unsignedLongSerializer.serialize(l);
    }

    public void writeString(String s) throws IOException {
        stringSerializer.serialize(s);
    }

    public <T> void writeObject(T o) throws IOException {
        writeObject(o, o == null ? null : new TypeDescriptor<T>(o.getClass()));
    }

    public abstract <T> void writeObject(T o, TypeDescriptor<T> type) throws IOException;

    public abstract ClassManager getClassManager();

    public abstract SerializationFactoryLookup getSerializationFactoryLookup();

    public abstract <E> Serializer<E> createAndRegisterSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException;


    public static class Wrapper extends DataWriter {
        DataWriter parent;

        public Wrapper(OutputStream os, DataWriter parent) throws IOException {
            super(os);
            this.parent = parent;
        }

        @Override
        public <T> void writeObject(T o, TypeDescriptor<T> type) throws IOException {
            parent.writeObject(o, type);
        }

        @Override
        public ClassManager getClassManager() {
            return parent.getClassManager();
        }

        @Override
        public SerializationFactoryLookup getSerializationFactoryLookup() {
            return parent.getSerializationFactoryLookup();
        }

        @Override
        public <E> Serializer<E> createAndRegisterSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
            return parent.createAndRegisterSerializer(factory, type);
        }
    }


    public static abstract class Abstract extends DataWriter {

        protected ClassManager classManager;
        protected SerializationFactoryLookup serializationFactoryLookup;

        protected Map<TypeDescriptor, Integer> typeToId = new HashMap<>();
        protected List<Serializer> registeredSerializers = new ArrayList<>();

        public Abstract(OutputStream os, ClassManager classManager, SerializationFactoryLookup serializationFactoryLookup) throws IOException {
            super(os);
            this.classManager = classManager;
            this.serializationFactoryLookup = serializationFactoryLookup;
        }

        public <T> void writeObject(T o, TypeDescriptor<T> type) throws IOException {
            if (o == null) {
                writeUnsignedLong(null);
                return;
            }
            Serializer<T> serializer = getOrCreateSerializer(type);
            serializer.serialize(o);
        }

        private <T> Serializer<T> getOrCreateSerializer(TypeDescriptor<T> type) throws IOException {
            Integer id = typeToId.getOrDefault(type, 0);
            writeUnsignedLong(Long.valueOf(id));
            if (id <= 0) {
                SerializationFactory factory = serializationFactoryLookup.getSerializationFactory(type, true);
                return writeAndRegisterSerializer(factory, type);
            } else {
                return registeredSerializers.get(id - 1);
            }
        }

        public ClassManager getClassManager() {
            return classManager;
        }

        public SerializationFactoryLookup getSerializationFactoryLookup() {
            return serializationFactoryLookup;
        }

        public <E> Serializer<E> createAndRegisterSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
            if (factory instanceof GloballyDefined) {
                Integer id = typeToId.get(type);
                if (id != null) {
                    writeUnsignedLong(id.longValue());
                    return registeredSerializers.get(id - 1);
                } else {
                    writeUnsignedLong(0L);
                    return writeAndRegisterSerializer(factory, type);
                }
            } else {
                writeUnsignedLong(null);
                return writeSerializer(factory, type);
            }
        }

        private <E> Serializer<E> writeAndRegisterSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException {
            int id = typeToId.size();
            typeToId.put(type, id + 1);
            registeredSerializers.add(null);
            Serializer<E> serializer = writeSerializer(factory, type);
            registeredSerializers.set(id, serializer);
            return serializer;
        }

        protected abstract <E> Serializer<E> writeSerializer(SerializationFactory factory, TypeDescriptor<E> type) throws IOException;
    }
}
