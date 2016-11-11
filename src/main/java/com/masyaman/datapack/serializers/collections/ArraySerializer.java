package com.masyaman.datapack.serializers.collections;

import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.streams.DataWriter;
import com.masyaman.datapack.utils.CollectionReorderer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static com.masyaman.datapack.utils.Constants.COLLECTION_REORDERING_CACHE_SIZE;

class ArraySerializer implements Serializer<Object[]> {

    private DataWriter os;
    private Serializer valueSerializer;

    private CollectionReorderer collectionReorderer;

    public ArraySerializer(DataWriter os, Serializer valueSerializer, boolean allowReordering) throws IOException {
        this.os = os;
        this.valueSerializer = valueSerializer;

        if (allowReordering && COLLECTION_REORDERING_CACHE_SIZE >= 0) {
            this.collectionReorderer = new CollectionReorderer<>(COLLECTION_REORDERING_CACHE_SIZE);
        }
    }

    @Override
    public void serialize(Object[] array) throws IOException {
        if (array == null) {
           os.writeUnsignedLong(null);
           return;
        }
        os.writeUnsignedLong((long) array.length);

        Collection collection = Arrays.asList(array);
        if (collectionReorderer != null) {
            collection = collectionReorderer.reorderByUsage(collection);
        }

        for (Object val : collection) {
            valueSerializer.serialize(val);
        }
    }

}
