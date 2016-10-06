package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.serializers.numbers.AbstractNumberSerializationFactory;
import com.masyaman.datapack.serializers.numbers.NumberMedianSerializationFactory;

/**
 * Serialization factory for Dates.
 * Values are stored as fixed-points Longs.
 * During serialization it saves difference and predicted value.
 * Prediction is a previous value + median of three previous value changes.
 */
public class DateMedianSerializationFactory extends AbstractDateSerializationFactory {

    public static final DateMedianSerializationFactory INSTANCE = new DateMedianSerializationFactory();

    private DateMedianSerializationFactory() {
        super("_DM");
    }

    @Override
    protected AbstractNumberSerializationFactory getNumberSerializationFactory() {
        return NumberMedianSerializationFactory.INSTANCE;
    }
}
