package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.serializers.numbers.AbstractNumberSerializationFactory;
import com.masyaman.datapack.serializers.numbers.NumberDiffSerializationFactory;
import com.masyaman.datapack.serializers.numbers.NumberIncrementalSerializationFactory;

/**
 * Serialization factory for Dates.
 * During serialization it saves unsigned difference to previous value.
 */
public class DateIncrementalSerializationFactory extends AbstractDateSerializationFactory {

    public static final DateIncrementalSerializationFactory INSTANCE = new DateIncrementalSerializationFactory();

    private DateIncrementalSerializationFactory() {
        super("_DI");
    }

    @Override
    protected AbstractNumberSerializationFactory getNumberSerializationFactory() {
        return NumberIncrementalSerializationFactory.INSTANCE;
    }
}
