package com.masyaman.datapack.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;

public class TypeDescriptor<T> {

    public static final TypeDescriptor<String> STRING = new TypeDescriptor<>(String.class);
    public static final TypeDescriptor<Long> LONG = new TypeDescriptor<>(Long.class);

    private Class<T> type;
    private ParameterizedType parametrizedType;
    private Annotation[] annotations;


    public TypeDescriptor(Class<T> type) {
        this(type, type.getAnnotations());
    }

    public TypeDescriptor(Class<T> type, Annotation... annotations) {
        this(type, null, annotations);
    }

    public TypeDescriptor(Class<T> type, Type parametrizedType, Annotation... annotations) {
        this.type = type;
        this.parametrizedType = parametrizedType instanceof ParameterizedType ? (ParameterizedType) parametrizedType : null;
        this.annotations = annotations;
    }

    public TypeDescriptor(Field field) {
        this((Class<T>) field.getType(), field.getGenericType(), field.getDeclaredAnnotations());
    }

    public TypeDescriptor(Method method) {
        this((Class<T>) method.getReturnType(), method.getGenericReturnType(), method.getDeclaredAnnotations());
    }

    public Class<T> getType() {
        return type;
    }

    public ParameterizedType getParametrizedType() {
        return parametrizedType;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public <T> T getAnnotation(Class<T> annotationClass) {
        for (Annotation annotation : annotations) {
            if (annotationClass == annotation.annotationType()) {
                return (T) annotation;
            }
        }
        return null;
    }

    public Class getParametrizedType(int i) {
        if (parametrizedType == null) {
            return Object.class;
        }
        return (Class) getParametrizedType().getActualTypeArguments()[i]; // TODO
    }

    public TypeDescriptor getParametrizedTypeDescriptor(int i) {
        return new TypeDescriptor(getParametrizedType(i));
    }

    public boolean isFinal() {
        return Modifier.isFinal(type.getModifiers());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeDescriptor<?> that = (TypeDescriptor<?>) o;

        if (!type.equals(that.type)) return false;
        if (parametrizedType != null ? !parametrizedType.equals(that.parametrizedType) : that.parametrizedType != null)
            return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(annotations, that.annotations);

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (parametrizedType != null ? parametrizedType.hashCode() : 0);
        result = 31 * result + (annotations != null ? Arrays.hashCode(annotations) : 0);
        return result;
    }
}
