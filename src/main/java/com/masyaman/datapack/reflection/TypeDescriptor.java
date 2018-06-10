package com.masyaman.datapack.reflection;

import com.masyaman.datapack.annotations.deserialization.AsJson;
import com.masyaman.datapack.annotations.deserialization.TypeFieldName;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class TypeDescriptor<T> {

    public static final TypeDescriptor<String> JSON = new TypeDescriptor(String.class, new AsJson.Instance());
    public static final TypeDescriptor<String> JSON_WITH_TYPES = new TypeDescriptor(String.class, new AsJson.Instance(false), new TypeFieldName.Instance("type"));

    public static final TypeDescriptor<Object> OBJECT = new TypeDescriptor<>(Object.class);
    public static final TypeDescriptor<String> STRING = new TypeDescriptor<>(String.class);
    public static final TypeDescriptor<Date> DATE = new TypeDescriptor<>(Date.class);

    public static final TypeDescriptor<Boolean> BOOLEAN = new TypeDescriptor<>(Boolean.class);
    public static final TypeDescriptor<Enum> ENUM = new TypeDescriptor<>(Enum.class);
    public static final TypeDescriptor<Long> LONG = new TypeDescriptor<>(Long.class);
    public static final TypeDescriptor<Integer> INTEGER = new TypeDescriptor<>(Integer.class);
    public static final TypeDescriptor<Double> DOUBLE = new TypeDescriptor<>(Double.class);

    public static final TypeDescriptor<Collection> COLLECTION = new TypeDescriptor<>(Collection.class);

    public static final TypeDescriptor<List> LIST = new TypeDescriptor<>(List.class);
    public static final TypeDescriptor<LinkedList> LINKED_LIST = new TypeDescriptor<>(LinkedList.class);
    public static final TypeDescriptor<ArrayList> ARRAY_LIST = new TypeDescriptor<>(ArrayList.class);

    public static final TypeDescriptor<Set> SET = new TypeDescriptor<>(Set.class);
    public static final TypeDescriptor<HashSet> HASH_SET = new TypeDescriptor<>(HashSet.class);
    public static final TypeDescriptor<TreeSet> TREE_SET = new TypeDescriptor<>(TreeSet.class);
    public static final TypeDescriptor<LinkedHashSet> LINKED_HASH_SET = new TypeDescriptor<>(LinkedHashSet.class);

    public static final TypeDescriptor<Map> MAP = new TypeDescriptor<>(Map.class);
    public static final TypeDescriptor<HashMap> HASH_MAP = new TypeDescriptor<>(HashMap.class);
    public static final TypeDescriptor<TreeMap> TREE_MAP = new TypeDescriptor<>(TreeMap.class);
    public static final TypeDescriptor<LinkedHashMap> LINKED_HASH_MAP = new TypeDescriptor<>(LinkedHashMap.class);

    public static final TypeDescriptor<BitSet> BIT_SET = new TypeDescriptor<>(BitSet.class);

    public static final TypeDescriptor<Object[]> OBJECT_ARRAY = new TypeDescriptor<>(Object[].class);
    public static final TypeDescriptor<String[]> STRING_ARRAY = new TypeDescriptor<>(String[].class);

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
