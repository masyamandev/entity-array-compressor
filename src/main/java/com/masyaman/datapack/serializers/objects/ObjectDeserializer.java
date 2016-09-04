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

class ObjectDeserializer<T> implements Deserializer<T> {

    private DataReader is;
    private Constructor<T> constructor;

    // TODO make setter with serializer
    private List<Setter> setters = new ArrayList<>();
    private List<Deserializer> deserializers = new ArrayList<>();

    public ObjectDeserializer(DataReader is, TypeDescriptor<T> type) throws IOException {
        this.is = is;

        String className = is.readString();
        Class<T> clazz;
        try {
            clazz = (Class<T>) Class.forName(className);
            if (type != null && !type.getType().isAssignableFrom(clazz)) {
                throw new IOException("Classes are not matched, expected " + type.getType().getName() +
                        " but found " + clazz.getName());
            }
            constructor = clazz.getConstructor();
            constructor.setAccessible(true);
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to find class for name " + className);
        } catch (NoSuchMethodException e) {
            throw new IOException("Unable to find default constructor for class " + className);
        }

        Map<String, Setter> setterMap = ClassUtils.setterMap(clazz);

        Long fieldsNum = is.readUnsignedLong();
        for (int i = 0; i < fieldsNum; i++) {
            String fieldName = is.readString();

            Setter setter = setterMap.get(fieldName);
            if (setter == null) {
                System.out.println("No setter found for field " + clazz.getName() + "." + fieldName);
            }

            Deserializer deserializer = is.createAndRegisterDeserializer(setter == null ? null : setter.type());

            deserializers.add(deserializer);
            setters.add(setter);
        }
    }

    @Override
    public T deserialize() throws IOException {

        List fields = new ArrayList<>(setters.size());
        boolean allNulls = true;

        for (int i = 0; i < setters.size(); i++) {
            Object field = deserializers.get(i).deserialize();
            fields.add(field);
            allNulls &= field == null;
        }

        if (allNulls && is.readUnsignedLong() == null) {
            return null;
        }

        T object;
        try {
            object = constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IOException("Unable to create object of type " + constructor.getDeclaringClass().getName(), e);
        }

        for (int i = 0; i < setters.size(); i++) {
            try {
                setters.get(i).set(object, fields.get(i));
            } catch (ReflectiveOperationException e) {
                throw new IOException("Unable to serialize", e);
            }
        }

        return object;
    }
}
