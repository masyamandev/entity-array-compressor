package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.settings.SettingsKeys;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.utils.CollectionReorderer;

import java.io.IOException;
import java.util.Collection;

class CollectionSerializer<V> implements Serializer<Collection<V>> {

    private DataWriter os;
    private Serializer<V> valueSerializer;

    private CollectionReorderer<V> collectionReorderer;

    public CollectionSerializer(DataWriter os, Serializer<V> valueSerializer, boolean allowReordering) throws IOException {
        this.os = os;
        this.valueSerializer = valueSerializer;

        Integer cacheSize = os.getSettings().get(SettingsKeys.COLLECTION_REORDERING_CACHE_SIZE);
        if (allowReordering && cacheSize >= 0) {
            this.collectionReorderer = new CollectionReorderer<>(cacheSize);
        }
    }

    @Override
    public void serialize(Collection<V> collection) throws IOException {
        if (collection == null) {
           os.writeUnsignedLong(null);
           return;
        }
        os.writeUnsignedLong((long) collection.size());

        if (collectionReorderer != null) {
            collection = collectionReorderer.reorderByUsage(collection);
        }

        for (V val : collection) {
            valueSerializer.serialize(val);
        }
    }

}
