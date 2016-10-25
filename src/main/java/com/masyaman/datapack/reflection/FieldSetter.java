package com.masyaman.datapack.reflection;

import java.lang.reflect.Field;

public class FieldSetter implements Setter {

    private Field field;
    private TypeDescriptor typeDescriptor;

    public FieldSetter(Field field, TypeDescriptor typeDescriptor) {
        this.field = field;
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    public void set(Object o, Object value) throws ReflectiveOperationException {
        field.set(o, value);
    }

    @Override
    public TypeDescriptor type() {
        return typeDescriptor;
    }
}