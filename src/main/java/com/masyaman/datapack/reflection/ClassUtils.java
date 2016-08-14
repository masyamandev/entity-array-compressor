package com.masyaman.datapack.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ClassUtils {

    public static Map<String, Getter> getterMap(Class clazz) {
        Map<String, Getter> getterMap = new HashMap<>();

        // Add all getters for getter methods
//        for (Method method : clazz.getMethods()) {
//            if (Modifier.isStatic(method.getModifiers())) {
//                continue;
//            }
//
//            String name = method.getName();
//            if (method.getParameterCount() == 0 && name.startsWith("get") && name.length() > 3) {
//                method.setAccessible(true);
//
//                String varName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
//
//                Getter getter = new MethodGetter(method);
//                getterMap.putIfAbsent(varName, getter);
//            }
//        }

        // Add all getters for private fields
        Class searchClass = clazz;
        while (searchClass != Object.class) {
            for (Field field : searchClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                String varName = field.getName();

                Getter getter = new FieldGetter(field);
                getterMap.putIfAbsent(varName, getter);
            }
            searchClass = searchClass.getSuperclass();
        }

        return getterMap;
    }

    public static Map<String, Setter> setterMap(Class clazz) {
        Map<String, Setter> setterMap = new HashMap<>();

        // Add all setters for private fields
        Class searchClass = clazz;
        while (searchClass != Object.class) {
            for (Field field : searchClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                String varName = field.getName();

                Setter getter = new FieldSetter(field);
                setterMap.putIfAbsent(varName, getter);
            }
            searchClass = searchClass.getSuperclass();
        }

        return setterMap;
    }

}
