package com.masyaman.datapack.reflection;

import java.lang.reflect.Method;
import java.util.function.Function;

public class MethodSetter implements Setter {

    private Method method;
    private TypeDescriptor typeDescriptor;

    public MethodSetter(Method method) {
        this.method = method;
        this.typeDescriptor = new TypeDescriptor(method);
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