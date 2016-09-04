package com.masyaman.datapack.annotations;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;

import static com.masyaman.datapack.serializers.SerializationFactory.*;

public class AnnotationsHelper {

    public static SerializationFactory getSerializationFactoryFromAnnotation(TypeDescriptor type) {
        SerializeBy annotation = (SerializeBy) type.getAnnotation(SerializeBy.class); // TODO cast
        if (annotation != null) {
            return getInstance(annotation.value());
        }
        return null;
    }

    public static int getDecimalPrecision(TypeDescriptor type, int defaultPrecision) {
        DecimalPrecision annotation = (DecimalPrecision) type.getAnnotation(DecimalPrecision.class); // TODO cast
        if (annotation != null) {
            return annotation.value();
        }
        return defaultPrecision;
    }
}
