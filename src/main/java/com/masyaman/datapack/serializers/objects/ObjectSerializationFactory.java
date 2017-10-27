package com.masyaman.datapack.serializers.objects;

import com.masyaman.datapack.annotations.deserialization.AsJson;
import com.masyaman.datapack.annotations.serialization.IgnoredField;
import com.masyaman.datapack.annotations.serialization.SerializeBy;
import com.masyaman.datapack.reflection.Getter;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.objects.ObjectDeserializer.FieldDeserializer;
import com.masyaman.datapack.settings.SettingsKeys;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.masyaman.datapack.annotations.AnnotationsHelper.annotationsFrom;
import static com.masyaman.datapack.annotations.AnnotationsHelper.serializeAs;
import static com.masyaman.datapack.serializers.objects.ObjectSerializer.FieldSerializer;

/**
 * Serialization factory for any user objects. However, object type should be known at the moment of serialization creation.
 */
public class ObjectSerializationFactory extends SerializationFactory<Object> {

    public static final ObjectSerializationFactory INSTANCE = new ObjectSerializationFactory();

    private ObjectSerializationFactory() {
        super("_O");
    }

    @Override
    public TypeDescriptor<Object> getDefaultType() {
        return TypeDescriptor.OBJECT;
    }


    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return true;
    }

    @Override
    public <E> Serializer<E> createSerializer(DataWriter os, TypeDescriptor<E> type) throws IOException {
        Class<?> clazz = type.getType();
        os.writeString(os.getClassManager().getClassName(clazz));

        List<FieldSerializer> fieldSerializers = new ArrayList<>();
        for (Map.Entry<String, Getter> getterEntry : os.getClassManager().getterMap(clazz).entrySet()) {
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

            fieldSerializers.add(new FieldSerializer(getterEntry.getKey(), getter, serializationFactory, declaredType));
        }

        if (os.getSettings().get(SettingsKeys.ENABLE_REORDERING_FIELDS)) {
            Collections.sort(fieldSerializers);
        }

        os.writeUnsignedLong((long) fieldSerializers.size());
        for (FieldSerializer fieldSerializer : fieldSerializers) {
            os.writeString(fieldSerializer.getFieldName());
            fieldSerializer.setSerializer(os.createAndRegisterSerializer(fieldSerializer.getSerializationFactory(), fieldSerializer.getDeclaredType()));
        }

        return new ObjectSerializer(os, fieldSerializers);
    }

    @Override
    public Deserializer createDeserializer(DataReader is) throws IOException {
        String className = is.readString();
        Long fieldsNum = is.readUnsignedLong();

        List<FieldDeserializer> deserializations = new ArrayList<>(fieldsNum.intValue());
        for (int i = 0; i < fieldsNum; i++) {
            String fieldName = is.readString();
            Deserializer deserializer = is.createAndRegisterDeserializer();
            deserializations.add(new FieldDeserializer(fieldName, deserializer));
        }

        return new Deserializer() {
            @Override
            public Object deserialize(TypeDescriptor type) throws IOException {
                if (Map.class.isAssignableFrom(type.getType())) {
                    return new MapObjectDeserializer(is, className, deserializations).deserialize(type);
                } else if (type.getAnnotation(AsJson.class) != null) {
                    return new JsonObjectDeserializer(is, className, deserializations).deserialize(type);
                } else {
                    return new ObjectDeserializer<>(is, className, deserializations).deserialize(type);
                }
            }
        };
    }
}
