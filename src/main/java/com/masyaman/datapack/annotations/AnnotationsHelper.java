package com.masyaman.datapack.annotations;

import com.masyaman.datapack.reflection.TypeDescriptor;

import java.lang.annotation.Annotation;

public class AnnotationsHelper {

    public static int getDecimalPrecision(TypeDescriptor<?> type, int defaultPrecision) {
        DecimalPrecision annotation = type.getAnnotation(DecimalPrecision.class);
        if (annotation != null) {
            return annotation.value();
        }
        return defaultPrecision;
    }

    public static int getCacheSize(TypeDescriptor<?> type) {
        CacheSize annotation = type.getAnnotation(CacheSize.class);
        if (annotation != null) {
            return annotation.value();
        }
        return 0;
    }

    public static Class serializeAs(SerializeBy serializeBy, Class inherited) {
        return (serializeBy != null) ? getOrDefault(serializeBy.serializeAs(), inherited) : inherited;
    }
    public static Annotation[] annotationsFrom(SerializeBy serializeBy, Annotation[] inherited) {
        return (serializeBy != null) ? getOrDefault(serializeBy.annotationsFrom(), inherited) : inherited;
    }

    public static Class serializeAs(SerializeKeyBy serializeBy, Class inherited) {
        return (serializeBy != null) ? getOrDefault(serializeBy.serializeAs(), inherited) : inherited;
    }
    public static Annotation[] annotationsFrom(SerializeKeyBy serializeBy, Annotation[] inherited) {
        return (serializeBy != null) ? getOrDefault(serializeBy.annotationsFrom(), inherited) : inherited;
    }

    public static Class serializeAs(SerializeValueBy serializeBy, Class inherited) {
        return (serializeBy != null) ? getOrDefault(serializeBy.serializeAs(), inherited) : inherited;
    }
    public static Annotation[] annotationsFrom(SerializeValueBy serializeBy, Annotation[] inherited) {
        return (serializeBy != null) ? getOrDefault(serializeBy.annotationsFrom(), inherited) : inherited;
    }

    private static Class getOrDefault(Class clazz, Class inherited) {
        if (clazz != null && clazz != InheritFromParent.class) {
            return clazz;
        } else {
            return inherited;
        }
    }
    private static Annotation[] getOrDefault(Class clazz, Annotation[] inherited) {
        if (clazz != null && clazz != InheritFromParent.class) {
            return clazz.getAnnotations();
        } else {
            return inherited;
        }
    }
}
