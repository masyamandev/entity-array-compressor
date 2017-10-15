package com.masyaman.datapack.settings;

/**
 * Handle setting map. Basically it can be treated as Map&lt;SettingKey, Object&gt; with correct type casting
 * of value and getting default value from key if it was not explicitly set.
 */
public class SettingsHandler {

    private static final Object[] EMPTY = new Object[0];
    public static SettingsHandler DEFAULTS = new SettingsHandler();

    private Object[] values = EMPTY;

    public <T> T get(SettingKey<T> key) {
        if (values.length <= key.getId() || values[key.getId()] == null) {
            return key.getDefaultValue();
        } else {
            return (T) values[key.getId()];
        }
    }

    public <T> SettingsHandler set(SettingKey<T> key, T value) {
        if (values.length <= key.getId()) {
            Object[] oldValues = values;
            values = new Object[SettingKey.getIdCounter()];
            System.arraycopy(oldValues, 0, values, 0, oldValues.length);
        }
        values[key.getId()] = value;
        return this;
    }


}
