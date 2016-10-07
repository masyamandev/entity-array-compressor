package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.utils.CollectionReorderer;

import java.io.IOException;
import java.util.Collection;

import static com.masyaman.datapack.utils.Constants.COLLECTION_REORDERING_CACHE_SIZE;

class CollectionSerializer<V> implements Serializer<Collection<V>> {

    private DataWriter os;
    private Serializer<V> valueSerializer;

    private CollectionReorderer<V> collectionReorderer;

    public CollectionSerializer(DataWriter os, SerializationFactory<V> valueSerializationFactory, TypeDescriptor<V> valueType, boolean allowReordering) throws IOException {
        this.os = os;
        this.valueSerializer = os.createAndRegisterSerializer(valueSerializationFactory, valueType);

        if (allowReordering && COLLECTION_REORDERING_CACHE_SIZE >= 0) {
            this.collectionReorderer = new CollectionReorderer<>(COLLECTION_REORDERING_CACHE_SIZE);
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
