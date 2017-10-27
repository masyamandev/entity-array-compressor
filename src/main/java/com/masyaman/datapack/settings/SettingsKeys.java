package com.masyaman.datapack.settings;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.streams.BufferedDataWriter;

import java.nio.charset.Charset;

import static com.masyaman.datapack.reflection.TypeDescriptor.COLLECTION;

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
     * Default precision of doubles.
     */
    public static final SettingKey<Integer> DEFAULT_PRECISION = new SettingKey(6);

    /**
     * Ignore unknown fields during decoding.
     */
    public static final SettingKey<Boolean> IGNORE_UNKNOWN_FIELDS = new SettingKey(false);



    /**
     * Default type of deserialized collection if it's requested to deserialize it as object.
     * Can be new TypeDescriptor<>(Collection.class) or new TypeDescriptor<>(Object[].class)
     */
    public static final SettingKey<TypeDescriptor> DEFAULT_COLLECTIONS_DESERIALIZATION_TYPE = new SettingKey(COLLECTION, TypeDescriptor.class);

    /**
     * Option for {@link BufferedDataWriter}. Indicates byte buffer size.
     */
    public static final SettingKey<Integer> BYTE_BUFFER_SIZE = new SettingKey(32000);

    private SettingsKeys() {
    }
}
