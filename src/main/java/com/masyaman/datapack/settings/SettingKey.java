package com.masyaman.datapack.settings;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Key of specific setting option. Handles value type and default value.
 * @param <T> Type of value.
 */
final class SettingKey<T> {

    private static AtomicInteger idCounter = new AtomicInteger(0);

    private final int id;
    private final T defaultValue;
    private final Class<T> valueType;

    SettingKey(T defaultValue) {
        this(defaultValue, (Class<T>) defaultValue.getClass());
    }

    SettingKey(T defaultValue, Class<T> valueType) {
        this.defaultValue = defaultValue;
        this.valueType = valueType;
        this.id = idCounter.getAndAdd(1);
    }


    public T getDefaultValue() {
        return defaultValue;
    }

    public Class<T> getValueType() {
        return valueType;
    }


    int getId() {
        return id;
    }

    static int getIdCounter() {
        return idCounter.get();
    }
}
