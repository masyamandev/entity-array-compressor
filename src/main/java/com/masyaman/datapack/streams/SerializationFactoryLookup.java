package com.masyaman.datapack.streams;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.collections.BitSetSerializationFactory;
import com.masyaman.datapack.serializers.collections.MapSerializationFactory;
import com.masyaman.datapack.serializers.enums.EnumsSerializationFactory;
import com.masyaman.datapack.serializers.numbers.*;
import com.masyaman.datapack.serializers.objects.ObjectSerializationFactory;
import com.masyaman.datapack.serializers.objects.UnknownTypeCachedSerializationFactory;
import com.masyaman.datapack.serializers.objects.UnknownTypeSerializationFactory;
import com.masyaman.datapack.serializers.objects.UnsupportedSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringCachedSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringSerializationFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializationFactoryLookup {

    static List<SerializationFactory> DEFAULT_FACTORIES = Arrays.asList(
            NumberDiffSerializationFactory.INSTANCE,
            NumberLinearSerializationFactory.INSTANCE,
            NumberMedianSerializationFactory.INSTANCE,
            NumberSerializationFactory.INSTANCE,
            UnsignedLongSerializationFactory.INSTANCE,
            EnumsSerializationFactory.INSTANCE,
            StringSerializationFactory.INSTANCE,
            StringCachedSerializationFactory.INSTANCE,
            MapSerializationFactory.INSTANCE,
            BitSetSerializationFactory.INSTANCE,
            ObjectSerializationFactory.INSTANCE,
            UnknownTypeSerializationFactory.INSTANCE,
            UnknownTypeCachedSerializationFactory.INSTANCE,
            UnsupportedSerializationFactory.INSTANCE
    );

    private Map<String, SerializationFactory> factories = new HashMap<>();

    public SerializationFactoryLookup() {
        for (SerializationFactory factory : DEFAULT_FACTORIES) {
            factories.put(factory.getName(), factory);
        }

    }

    public SerializationFactory getSerializationFactory(TypeDescriptor type) {
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
                return UnsupportedSerializationFactory.INSTANCE; // TODO

            case "java.util.BitSet":
                return BitSetSerializationFactory.INSTANCE;

            case "java.util.List":
            case "java.util.Set":
                return UnsupportedSerializationFactory.INSTANCE; // TODO
            case "java.util.Map":
                return MapSerializationFactory.INSTANCE;
            default:
                if (type.getType().isEnum()) {
                    return EnumsSerializationFactory.INSTANCE;
                }
        }
        return null;//new ObjectSerializationFactory.INSTANCE;
    }

    public SerializationFactory getByName(String name) {
        return factories.get(name);
    }
}
