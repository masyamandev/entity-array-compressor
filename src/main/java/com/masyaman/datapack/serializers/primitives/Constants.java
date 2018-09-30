package com.masyaman.datapack.serializers.primitives;

class Constants {

    static final byte NULL_VALUE = 0x7F; // 127 in single byte representation
    static final long SEVEN_BITS_MASK = 0b01111111;
    static final long MORE_BYTES_MASK = 0b10000000;
    static final long NEGATIVE_BYTE_MASK = 0b01000000;

}
