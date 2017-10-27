package com.masyaman.datapack.serializers.dates;

import com.masyaman.datapack.reflection.TypeDescriptor;
import com.masyaman.datapack.serializers.Deserializer;
import com.masyaman.datapack.serializers.SerializationFactory;
import com.masyaman.datapack.serializers.Serializer;
import com.masyaman.datapack.serializers.numbers.AbstractNumberSerializationFactory;
import com.masyaman.datapack.streams.DataReader;
import com.masyaman.datapack.streams.DataWriter;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.Date;

import static com.masyaman.datapack.annotations.AnnotationsHelper.*;
import static com.masyaman.datapack.reflection.TypeDescriptor.LONG;
import static com.masyaman.datapack.serializers.dates.SerializerWrappers.*;
import static com.masyaman.datapack.serializers.dates.DeserializerWrappers.*;

/**
 * Abstract Serialization factory for Dates.
 * Values are stored as fixed-points Longs.
 * Very basic serialization using signed variable-length coding.
 */
abstract class AbstractDateSerializationFactory extends SerializationFactory {

    protected AbstractDateSerializationFactory(String name) {
        super(name);
    }

    protected abstract AbstractNumberSerializationFactory getNumberSerializationFactory();

    @Override
    public TypeDescriptor getDefaultType() {
        return TypeDescriptor.DATE;
    }

    @Override
    public boolean isApplicable(TypeDescriptor type) {
        return Date.class.isAssignableFrom(type.getType()) || Long.class.isAssignableFrom(type.getType());
    }

    @Override
    public Serializer createSerializer(DataWriter os, TypeDescriptor type) throws IOException {
        RoundingMode roundingMode = getRoundingMode(type);
        int datePrecision = getDecimalPrecision(type, os.getSettings());
        if (datePrecision < 0 || datePrecision >= DatePrecisions.SCALES.length) {
            throw new IOException("Incorrect precision " + datePrecision + " for Date");
        }

        long scale = DatePrecisions.SCALES[datePrecision];

        os.writeSignedLong((long) datePrecision); // Date precision

        return convertFrom(scale(getNumberSerializationFactory().createSerializer(os, LONG, 0, roundingMode), scale, roundingMode), type);
    }

    @Override
    public Deserializer createDeserializer(DataReader is) throws IOException {
        int datePrecision = is.readUnsignedLong().intValue();
        if (datePrecision < 0 || datePrecision >= DatePrecisions.SCALES.length) {
            throw new IOException("Incorrect precision " + datePrecision + " for Date");
        }

        long scale = DatePrecisions.SCALES[datePrecision];

        return convertTo(scale(getNumberSerializationFactory().createDeserializer(is, LONG, 0), scale));
    }

}
