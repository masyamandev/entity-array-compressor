package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.serializers.numbers.AbstractNumberSerializationFactory;
import com.masyaman.datapack.serializers.numbers.NumberDiffSerializationFactory;

/**
 * Serialization factory for Dates.
 * Values are stored as fixed-points Longs.
 * Used by default for all Date serializations.
 * During serialization it saves difference to previous value. This gives result close to 0 on small value changes, so
 * it could use less bytes in stream.
 */
public class DateDiffSerializationFactory extends AbstractDateSerializationFactory {

    public static final DateDiffSerializationFactory INSTANCE = new DateDiffSerializationFactory();

    private DateDiffSerializationFactory() {
        super("_DD");
    }

    @Override
    protected AbstractNumberSerializationFactory getNumberSerializationFactory() {
        return NumberDiffSerializationFactory.INSTANCE;
    }
}
