package com.masyaman.datapack.annotations;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;

public class AnnotationsHelper {

    public static SerializationFactory getSerializationFactoryFromAnnotation(TypeDescriptor type) {
        SerializeBy annotation = (SerializeBy) type.getAnnotation(SerializeBy.class); // TODO cast
        if (annotation != null) {
            try {
                return (SerializationFactory) annotation.value().getField("INSTANCE").get(null);
            } catch (ReflectiveOperationException e) {
                System.out.println("Unable to find static field INSTANCE in " + annotation.value().getClass());
            }
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
