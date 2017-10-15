package com.masyaman.datapack.settings;

import com.masyaman.datapack.annotations.Alias;
import com.masyaman.datapack.reflection.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public ClassManager addMixIn(Class<?> clazz, Class<?> mixIn) {
        mixInClasses.put(clazz, mixIn);
        addAlias(clazz, clazz.getAnnotation(Alias.class));
        addAlias(clazz, mixIn.getAnnotation(Alias.class));
        return this;
    }

    public ClassManager addAlias(Class<?> clazz) {
        addAlias(clazz, clazz.getAnnotation(Alias.class));
        return this;
    }

    private void addAlias(Class<?> clazz, Alias alias) {
        if (alias != null) {
            aliasToClass.put(alias.value(), clazz);
        }
    }

    public Class<?> getClassByName(String className) throws IOException {
        Class clazz = aliasToClass.get(className);
        if (clazz == null) {
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new IOException("Unable to find class for name " + className);
            }
        }
        return clazz;
    }

    public ObjectFactory objectFactoryByName(String className) throws IOException {
        Class clazz = getClassByName(className);
        Constructor constructor;
        try {
            constructor = clazz.getConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IOException("Unable to find default constructor for class " + clazz.getName());
        }
        return new ObjectFactory() {
            @Override
            public Object create() throws IOException {
                try {
                    return constructor.newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new IOException("Unable to create object of type " + constructor.getDeclaringClass().getName(), e);
                }
            }
        };
    }

    public Class<?> getMixInClass(Class<?> clazz) {
        return mixInClasses.get(clazz);
    }

    public String getClassName(Class<?> clazz) {
        Class<?> mixInClass = getMixInClass(clazz);

        if (mixInClass != null && mixInClass.isAnnotationPresent(Alias.class)) {
            return mixInClass.getAnnotation(Alias.class).value();
        } else if (clazz.isAnnotationPresent(Alias.class)) {
            return clazz.getAnnotation(Alias.class).value();
        } else {
            return clazz.getName();
        }
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


    public Map<String, Getter> getterMap(Class clazz) {
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

                TypeDescriptor<?> mixInType = getMixInType(field, getMixInForField(clazz, field.getName()));
                Getter getter = new FieldGetter(field, mixInType);

                Alias alias = mixInType.getAnnotation(Alias.class);
                String varName = alias != null ? alias.value() : field.getName();

                getterMap.putIfAbsent(varName, getter);
            }
            searchClass = searchClass.getSuperclass();
        }

        return getterMap;
    }

    public Map<String, Setter> setterMap(Class clazz) {
        Map<String, Setter> setterMap = new HashMap<>();

        // Add all setters for private fields
        Class searchClass = clazz;
        while (searchClass != Object.class) {
            for (Field field : searchClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                TypeDescriptor<?> mixInType = getMixInType(field, getMixInForField(clazz, field.getName()));
                Setter getter = new FieldSetter(field, mixInType);

                Alias alias = mixInType.getAnnotation(Alias.class);
                String varName = alias != null ? alias.value() : field.getName();

                setterMap.putIfAbsent(varName, getter);
            }
            searchClass = searchClass.getSuperclass();
        }

        return setterMap;
    }

    private TypeDescriptor getMixInType(Field field, Class mixInClass) {
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

    public interface ObjectFactory<T> {
        T create() throws IOException;
    }
}
