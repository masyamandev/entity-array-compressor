package com.masyaman.datapack.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

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
