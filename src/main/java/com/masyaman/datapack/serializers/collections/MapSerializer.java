package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.utils.CollectionReorderer;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static com.masyaman.datapack.utils.Constants.COLLECTION_REORDERING_CACHE_SIZE;

class MapSerializer<K, V> implements Serializer<Map<K, V>> {

    private DataWriter os;
    private TypeDescriptor<K> keyType;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;

    private CollectionReorderer<K> collectionReorderer;

    public MapSerializer(DataWriter os, SerializationFactory<K> keySerializationFactory, TypeDescriptor<K> keyType,
                         SerializationFactory<V> valueSerializationFactory, TypeDescriptor<V> valueType, boolean allowReordering) throws IOException {
        this.os = os;
        this.keySerializer = os.createAndRegisterSerializer(keySerializationFactory, keyType);
        this.valueSerializer = os.createAndRegisterSerializer(valueSerializationFactory, valueType);
        this.keyType = keyType;

        if (allowReordering && COLLECTION_REORDERING_CACHE_SIZE >= 0) {
            this.collectionReorderer = new CollectionReorderer<>(COLLECTION_REORDERING_CACHE_SIZE);
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
