package com.masyaman.datapack.reflection;

import java.lang.reflect.Field;

public class FieldGetter<T> implements Getter<T> {

    private Field field;
    private TypeDescriptor typeDescriptor;

    public FieldGetter(Field field, TypeDescriptor typeDescriptor) {
        this.field = field;
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    public T get(Object o) throws ReflectiveOperationException {
        return (T) field.get(o);
    }

    @Override
    public TypeDescriptor<?> type() {
        return typeDescriptor;
    }
}
