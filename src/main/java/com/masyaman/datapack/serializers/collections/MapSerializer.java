package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.settings.SettingsKeys;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.utils.CollectionReorderer;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

class MapSerializer<K, V> implements Serializer<Map<K, V>> {

    private DataWriter os;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;

    private CollectionReorderer<K> collectionReorderer;

    public MapSerializer(DataWriter os, Serializer<K> keySerializer, Serializer<V> valueSerializer, boolean allowReordering) throws IOException {
        this.os = os;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;


        Integer cacheSize = os.getSettings().get(SettingsKeys.COLLECTION_REORDERING_CACHE_SIZE);
        if (allowReordering && cacheSize >= 0) {
            this.collectionReorderer = new CollectionReorderer<>(cacheSize);
        }
    }

    @Override
    public void serialize(Map<K, V> map) throws IOException {
        if (map == null) {
           os.writeUnsignedLong(null);
           return;
        }
        os.writeUnsignedLong((long) map.size());

        Collection<K> keys = map.keySet();
//        if (!(map instanceof SortedMap) && (Comparable.class.isAssignableFrom(keyType.getType()))) {
//            Collections.sort(keys, (Comparator<? super K>) Comparator.naturalOrder());
//        }
        if (collectionReorderer != null) {
           keys = collectionReorderer.reorderByUsage(keys);
        }

        for (K key : keys) {
            keySerializer.serialize(key);
        }
        for (K key : keys) {
            valueSerializer.serialize(map.get(key));
        }
    }

}
