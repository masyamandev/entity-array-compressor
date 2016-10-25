package com.masyaman.datapack.reflection;

import java.lang.reflect.Method;

public class MethodGetter<T> implements Getter<T> {

    private Method method;
    private TypeDescriptor typeDescriptor;

    public MethodGetter(Method method, TypeDescriptor typeDescriptor) {
        this.method = method;
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    public T get(Object o) throws ReflectiveOperationException {
        return (T) method.invoke(o);
    }

    @Override
    public TypeDescriptor<?> type() {
        return typeDescriptor;
    }
}
