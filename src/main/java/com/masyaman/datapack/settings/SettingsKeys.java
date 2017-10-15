package com.masyaman.datapack.settings;

import com.masyaman.datapack.streams.BufferedDataWriter;

import java.nio.charset.Charset;

/**
 * Contains all setting keys.
 */
public final class SettingsKeys {
    /**
     * Default charset. This option should be the same both in Writer and Reader.
     * TODO Should it be written into stream?
     */
    public static final SettingKey<Charset> CHARSET = new SettingKey(Charset.forName("UTF8"), Charset.class);
    /**
     * Collections may be reordered for best compression. Reordering requires some cache to remember history of orders.
     * Any positive number indicates cache size (objects). Use -1 to disable reordering, 0 for unlimited cache size.
     */
    public static final SettingKey<Integer> COLLECTION_REORDERING_CACHE_SIZE = new SettingKey(256);
    /**
     * Change fields order for better compression.
     */
    public static final SettingKey<Boolean> ENABLE_REORDERING_FIELDS = new SettingKey(true);

    /**
     * Manage classes aliases and mix-ins.
     */
    public static final SettingKey<ClassManager> CLASS_MANAGER = new SettingKey(new ClassManager());
    /**
     * Lookup of serialization factory.
     */
    public static final SettingKey<SerializationFactoryLookup> SERIALIZATION_FACTORY_LOOKUP = new SettingKey(new SerializationFactoryLookup());


    /**
     * Option for {@link BufferedDataWriter}. Indicates byte buffer size.
     */
    public static final SettingKey<Integer> BYTE_BUFFER_SIZE = new SettingKey(32000);

    private SettingsKeys() {
    }
}
