package com.masyaman.datapack.reflection;

import java.lang.reflect.Method;

public class MethodSetter implements Setter {

    private Method method;
    private TypeDescriptor typeDescriptor;

    public MethodSetter(Method method, TypeDescriptor typeDescriptor) {
        this.method = method;
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    public void set(Object o, Object value) throws ReflectiveOperationException {
        method.invoke(o, value);
    }

    @Override
    public TypeDescriptor type() {
        return null;
    }
}