package com.masyaman.datapack.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;

public class TypeDescriptor<T> {

    private Class<T> type;
    private ParameterizedType parametrizedType;
    private Annotation[] annotations;


    public TypeDescriptor(Class type) {
        this(type, type.getAnnotations());
    }

    public TypeDescriptor(Class type, Annotation... annotations) {
        this(type, null, annotations);
    }

    public TypeDescriptor(Class type, Type parametrizedType, Annotation... annotations) {
        this.type = type;
        this.parametrizedType = parametrizedType instanceof ParameterizedType ? (ParameterizedType) parametrizedType : null;
        this.annotations = annotations;
    }

    public TypeDescriptor(Field field) {
        this(field.getType(), field.getGenericType(), field.getDeclaredAnnotations());
    }

    public TypeDescriptor(Method method) {
        this(method.getReturnType(), method.getGenericReturnType(), method.getDeclaredAnnotations());
    }

    public Class getType() {
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
