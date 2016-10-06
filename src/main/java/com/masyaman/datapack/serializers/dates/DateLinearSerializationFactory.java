package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.serializers.numbers.AbstractNumberSerializationFactory;
import com.masyaman.datapack.serializers.numbers.NumberLinearSerializationFactory;

/**
 * Serialization factory for Dates.
 * Values are stored as fixed-points Longs.
 * During serialization it saves difference and predicted value. Prediction is made as linear interpolation using 2
 * previous values.
 */
public class DateLinearSerializationFactory extends AbstractDateSerializationFactory {

    public static final DateLinearSerializationFactory INSTANCE = new DateLinearSerializationFactory();

    private DateLinearSerializationFactory() {
        super("_DL");
    }

    @Override
    protected AbstractNumberSerializationFactory getNumberSerializationFactory() {
        return NumberLinearSerializationFactory.INSTANCE;
    }
}
