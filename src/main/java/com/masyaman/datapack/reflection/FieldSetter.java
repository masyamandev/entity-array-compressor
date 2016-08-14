package com.masyaman.datapack.reflection;

import java.lang.reflect.Field;
import java.util.function.Function;

public class FieldSetter implements Setter {

    private Field field;
    private TypeDescriptor typeDescriptor;

    public FieldSetter(Field field) {
        this.field = field;
        this.typeDescriptor = new TypeDescriptor(field);
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