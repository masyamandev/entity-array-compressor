package com.masyaman.datapack.reflection;

import com.masyaman.datapack.annotations.Alias;
import com.masyaman.datapack.streams.ClassManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClassUtils {

    public static Map<String, Getter> getterMap(Class clazz, ClassManager classManager) {
        Map<String, Getter> getterMap = new LinkedHashMap<>();

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

                TypeDescriptor<?> mixInType = getMixInType(field, classManager.getMixInForField(clazz, field.getName()));
                Getter getter = new FieldGetter(field, mixInType);

                Alias alias = mixInType.getAnnotation(Alias.class);
                String varName = alias != null ? alias.value() : field.getName();

                getterMap.putIfAbsent(varName, getter);
            }
            searchClass = searchClass.getSuperclass();
        }

        return getterMap;
    }

    public static Map<String, Setter> setterMap(Class clazz, ClassManager classManager) {
        Map<String, Setter> setterMap = new HashMap<>();

        // Add all setters for private fields
        Class searchClass = clazz;
        while (searchClass != Object.class) {
            for (Field field : searchClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                TypeDescriptor<?> mixInType = getMixInType(field, classManager.getMixInForField(clazz, field.getName()));
                Setter getter = new FieldSetter(field, mixInType);

                Alias alias = mixInType.getAnnotation(Alias.class);
                String varName = alias != null ? alias.value() : field.getName();

                setterMap.putIfAbsent(varName, getter);
            }
            searchClass = searchClass.getSuperclass();
        }

        return setterMap;
    }

    private static TypeDescriptor getMixInType(Field field, Class mixInClass) {
        if (mixInClass == null) {
            return new TypeDescriptor(field);
        }
        try {
            Field mixInField = mixInClass.getDeclaredField(field.getName());
            return new TypeDescriptor(mixInField);
        } catch (NoSuchFieldException e) {
            return new TypeDescriptor(field);
        }
    }

}
