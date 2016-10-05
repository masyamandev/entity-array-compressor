package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.numbers.NumberSerializationFactory;

/**
 * Serialization factory for Dates.
 * Values are stored as fixed-points Longs.
 * Very basic serialization using signed variable-length coding.
 */
public class DateSerializationFactory extends AbstractDateSerializationFactory {

    public static final DateSerializationFactory INSTANCE = new DateSerializationFactory();

    private DateSerializationFactory() {
        super("_D");
    }

    @Override
    protected SerializationFactory<? extends Number> getNumberSerializationFactory() {
        return NumberSerializationFactory.INSTANCE;
    }
}
