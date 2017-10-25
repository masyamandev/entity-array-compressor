package com.masyaman.datapack.annotations;

import com.masyaman.datapack.annotations.serialization.*;
import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.settings.SettingsHandler;

import java.lang.annotation.Annotation;
import java.math.RoundingMode;

import static com.masyaman.datapack.settings.SettingsKeys.DEFAULT_PRECISION;

public class AnnotationsHelper {

    public static int getDecimalPrecision(TypeDescriptor<?> type, SettingsHandler settingsHandler) {
        Precision annotation = type.getAnnotation(Precision.class);
        if (annotation != null) {
            return annotation.value();
        }
        if (type.getType() == Double.class || type.getType() == double.class || type.getType() == Float.class || type.getType() == float.class) {
            return settingsHandler.get(DEFAULT_PRECISION);
        } else {
            return 0;
        }
    }

    public static RoundingMode getRoundingMode(TypeDescriptor<?> type) {
        Precision annotation = type.getAnnotation(Precision.class);
        if (annotation != null) {
            return annotation.roundingMode();
        } else {
            return RoundingMode.HALF_UP;
        }
    }

    public static int getCacheSize(TypeDescriptor<?> type) {
        CacheSize annotation = type.getAnnotation(CacheSize.class);
        if (annotation != null) {
            return annotation.value();
        }
        return 0;
    }

    public static boolean allowReordering(TypeDescriptor<?> type, boolean defaultValue) {
        AllowReordering annotation = type.getAnnotation(AllowReordering.class);
        if (annotation != null) {
            return annotation.value();
        }
        return defaultValue;
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
