package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.collections.BitSetSerializationFactory;
import com.masyaman.datapack.serializers.collections.CollectionSerializationFactory;
import com.masyaman.datapack.serializers.collections.MapSerializationFactory;
import com.masyaman.datapack.serializers.dates.*;
import com.masyaman.datapack.serializers.enums.EnumsConstantsSerializationFactory;
import com.masyaman.datapack.serializers.enums.EnumsSerializationFactory;
import com.masyaman.datapack.serializers.numbers.*;
import com.masyaman.datapack.serializers.objects.ObjectSerializationFactory;
import com.masyaman.datapack.serializers.objects.UnknownTypeCachedSerializationFactory;
import com.masyaman.datapack.serializers.objects.UnknownTypeSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringCachedSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringConstantsSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringSerializationFactory;

import java.io.IOException;
import java.util.*;

public class SerializationFactoryLookup {

    static List<SerializationFactory> DEFAULT_FACTORIES = Arrays.asList(
            NumberDiffSerializationFactory.INSTANCE,
            NumberIncrementalSerializationFactory.INSTANCE,
            NumberLinearSerializationFactory.INSTANCE,
            NumberMedianSerializationFactory.INSTANCE,
            NumberSerializationFactory.INSTANCE,
            DateDiffSerializationFactory.INSTANCE,
            DateIncrementalSerializationFactory.INSTANCE,
            DateLinearSerializationFactory.INSTANCE,
            DateMedianSerializationFactory.INSTANCE,
            DateSerializationFactory.INSTANCE,
            UnsignedLongSerializationFactory.INSTANCE,
            EnumsSerializationFactory.INSTANCE,
            EnumsConstantsSerializationFactory.INSTANCE,
            StringSerializationFactory.INSTANCE,
            StringCachedSerializationFactory.INSTANCE,
            StringConstantsSerializationFactory.INSTANCE,
            CollectionSerializationFactory.INSTANCE,
            MapSerializationFactory.INSTANCE,
            BitSetSerializationFactory.INSTANCE,
            ObjectSerializationFactory.INSTANCE,
            UnknownTypeSerializationFactory.INSTANCE,
            UnknownTypeCachedSerializationFactory.INSTANCE
    );

    private Map<String, SerializationFactory> factories = new HashMap<>();

    public SerializationFactoryLookup() {
        for (SerializationFactory factory : DEFAULT_FACTORIES) {
            factories.put(factory.getName(), factory);
        }

    }

    public SerializationFactory getSerializationFactory(TypeDescriptor type, boolean exactType) throws IOException {
        switch (type.getType().getName()) {
            case "java.lang.String":
                return StringSerializationFactory.INSTANCE;

            case "int":
            case "java.lang.Integer":
            case "long":
            case "java.lang.Long":
            case "double":
            case "java.lang.Double":
            case "float":
            case "java.lang.Float":
                return NumberDiffSerializationFactory.INSTANCE;

            case "boolean":
            case "java.lang.Boolean":
            case "byte":
            case "java.lang.Byte":
            case "char":
            case "java.lang.Char":
                throw new IOException("Unable to find serializer for type " + type.getType().getName());

            case "java.util.BitSet":
                return BitSetSerializationFactory.INSTANCE;

            case "java.util.Date":
                return DateDiffSerializationFactory.INSTANCE;

            default:
                if (type.getType().isEnum()) {
                    return EnumsConstantsSerializationFactory.INSTANCE;
                } else if (Map.class.isAssignableFrom(type.getType())) {
                    return MapSerializationFactory.INSTANCE;
                } else if (Collection.class.isAssignableFrom(type.getType())) {
                    return CollectionSerializationFactory.INSTANCE;
                } else if (type.getType().isArray()) {
                    return CollectionSerializationFactory.INSTANCE;
                }
        }

        if (type.getType() != Object.class && type.getType().getName().startsWith("java")) {
            throw new IOException("Unable to find serializer for type " + type.getType().getName());
        }
        
        return exactType ? ObjectSerializationFactory.INSTANCE : UnknownTypeSerializationFactory.INSTANCE;
    }

    public SerializationFactory getByName(String name) {
        return factories.get(name);
    }
}
