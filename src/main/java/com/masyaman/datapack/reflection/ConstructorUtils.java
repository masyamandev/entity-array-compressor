package com.masyaman.datapack.reflection;

import java.lang.reflect.Modifier;

public class ConstructorUtils {

    public static <E> E createInstance(Class<E> clazz, Class... classesToTry) throws ReflectiveOperationException {
        if (!Modifier.isAbstract(clazz.getModifiers())) {
            return clazz.newInstance();
        }
        for (Class aClass : classesToTry) {
            if (clazz.isAssignableFrom(aClass)) {
                return (E) aClass.newInstance();
            }
        }
        throw new ReflectiveOperationException("Unable to instantiate " + clazz);
    }

}
