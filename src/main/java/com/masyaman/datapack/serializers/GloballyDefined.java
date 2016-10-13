package com.masyaman.datapack.serializers;

/**
 * Marker interface for {@link SerializationFactory} to indicate that it can be defined once and reused in all dependant
 * places. Usually it means that serializer and deserializer are independent on context and stateless.
 */
public interface GloballyDefined {
}
