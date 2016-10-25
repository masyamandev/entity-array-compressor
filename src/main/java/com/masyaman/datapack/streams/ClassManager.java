package com.masyaman.datapack.streams;

import com.masyaman.datapack.annotations.Alias;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage class alias and mix-in.
 */
public class ClassManager {

    protected Map<String, Class> aliasToClass = new HashMap<>();
    protected Map<Class, Class> mixInClasses = new HashMap<>();

    public ClassManager() {
    }

    public ClassManager(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            addAlias(clazz);
        }
    }

    public ClassManager(Collection<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            addAlias(clazz);
        }
    }

    public void addMixIn(Class<?> clazz, Class<?> mixIn) {
        mixInClasses.put(clazz, mixIn);
        addAlias(clazz, clazz.getAnnotation(Alias.class));
        addAlias(clazz, mixIn.getAnnotation(Alias.class));
    }

    public void addAlias(Class<?> clazz) {
        addAlias(clazz, clazz.getAnnotation(Alias.class));
    }

    private void addAlias(Class<?> clazz, Alias alias) {
        if (alias != null) {
            aliasToClass.put(alias.value(), clazz);
        }
    }

    public Class<?> getClassByAlias(String alias) {
        return aliasToClass.get(alias);
    }

    public Class<?> getMixInClass(Class<?> clazz) {
        return mixInClasses.get(clazz);
    }

    public Class<?> getMixInForField(Class<?> clazz, String fieldName) {
        Class c = clazz;
        while (c != null) {
            Class<?> mixInClass = getMixInClass(c);
            try {
                if (mixInClass != null && mixInClass.getDeclaredField(fieldName) != null) {
                    return mixInClass;
                }
            } catch (NoSuchFieldException e) {}
            c = c.getSuperclass();
        }
        return null;
    }
}
