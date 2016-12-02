# entity-array-compressor (EAC)

EAC is a tool for serialization/deserialization of object arrays. It's designed for serializing of big amount of object
which are mostly similar each to other, so only diffs are saved.

Good use case for this lib is to store history of slowly changed values or states like GPS track or values from 
temperature or humidity sensors.

Despite EAC output is relatively small comparing to other serialization tools, it's actually not a compression tool. 
It's output could be additionally compressed (e.g. with gzip) for even better results.


# Usage

Sample code for serializing to byte array of objects:
```java
byte[] serialized;
try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
    try (ObjectWriter serializer = new SerialDataWriter(byteStream)) {
        for (Object obj : objects) {
            serializer.writeObject(obj);
        }
    }
    serialized = byteStream.toByteArray();
}
```
Sample code for for deserialization of byte array to list of objects:
```java
List<Object> deserialized = new ArrayList<>();
try (ObjectReader objectReader = new SerialDataReader(new ByteArrayInputStream(serialized))) {
    while (objectReader.hasObjects()) {
        deserialized.add(objectReader.readObject());
    }
}
```

`TypeDescriptor` can be used to deserialize to specific type to avoid class cast. However, internally objects will be 
deserialized to the same types which object had during serialization and then cast to specific type.
```java
TypeDescriptor<MyType> typeDescriptor = new TypeDescriptor(MyType.class);
List<MyType> deserialized = new ArrayList<>();
try (ObjectReader objectReader = new SerialDataReader(new ByteArrayInputStream(serialized))) {
    while (objectReader.hasObjects()) {
        deserialized.add(objectReader.readObject(typeDescriptor));
    }
}
```

Serialized format keeps all structures of serialized objects, so binary stream could be deserialized to Json without
original classes:
```java
List<String> jsons = new ArrayList<>();
try (ObjectReader objectReader = new SerialDataReader(new ByteArrayInputStream(serialized))) {
    while (objectReader.hasObjects()) {
        // DeserializationTypes.JSON_WITH_TYPES_TYPE could be used here to add type field to deserialized jsons 
        jsons.add(objectReader.readObject(DeserializationTypes.JSON_TYPE)); 
    }
}
```

# Format description

## Basic types

There are several types of primitives:
* Bytes are saved directly to output stream without any changes.
* Long integers are 64 bits Long. These values are saved in variable-length format, so size may vary from 1 to 9 bytes 
  depending on it's value. Smaller values uses less space. In average one byte contains 7 effective bits. Thus signed 
  values in range -63..63 are saved as 1 byte, -8192..8191 as 2 bytes, -1m..1m as 3 bytes and so on. Any long value 
  could be saved as 9 bytes or less. With such approach there is no need for special handling of 32 bits Integers, 
  Shorts, Bytes etc: small values requires less space. Such coding is also null-friendly. Single-byted value -64 is 
  reserved for nulls.
* Unsigned Longs are also supported and used mostly for storing IDs. Coding is almost the same as signed, but sign bit 
  is used to save bigger values. Thus values in range 0..126 requires 1 byte, 127..16383 2 bytes etc. Single-byte value 
  127 is reserved for nulls. Negative values requires 9 bytes.
* Doubles and Floats are decimal fixed-point Longs. Scale is usually saved in data stream, so there are enough information
  in stream to decode value with required precision.
* Stings are stored as UTF8 byte array length in Unsigned Long format and corresponding number of bytes.
* There is cached objects support. Record contains 2 parts: Id (mandatory) and value itself (optional). When cache array 
  contains value, only Id as index in cache array + 1 is stored. When cache does not contain value, stored 0 as cache Id 
  and then value is stored and added to cache. Id is stored as Unsigned Long. There are several cache strategies, will be 
  described further.
  
## Variable-length Integers and Longs

All Integer values are saved as 64-bits Longs in variable-length format. Signed and unsigned values are slightly different, 
but the main idea is the same. Smaller values requires less space, 7 effective bits per byte in average.
Another feature of this format is that values are null-friendly, so any value could be saved as null and this will require
only 1 byte in stream.

### Unsigned Integers and Longs

First byte consists of 2 parts: length of record and optionally data bits. Length of record is a 0..8 bits of 1s and then
one bit of 0 (0 could be omit if there are exactly 8 bits of leading 1s). Number of 1s indicates how many additional bytes
should be read. All other bits after first bit 0 in first byte are data bits.

Example of encoding unsigned values:

Value `5251` could be represented as `0001 0100, 1000 0011` in binary format.
To write first byte we have to define greatest significant bit, which is 13 in out case. As we use 7 effective bits per 
byte, 2 bytes is required. Leading bits for indicating record length are `10`, showing that 1 additional byte is required.
Data bits are written in little endian notation.
```
1st byte   2nd byte
0001 0100, 1000 0011 - original value
1001 0100, 1000 0011 - bytes in stream
^^\_____/  \_______/
||   ^      Second byte of data bits, little endian 
||   \_ First byte of data bits, little endian
|\_ Fist 0 bit in first byte indicates ending of record length block 
\_ Amount of 1s indicated number of additional 1 byte in stream
```
During deserialization algorithm should read first byte, calculate number of leading 1s in it, switch leading 1s to 0s,
read corresponding amount of additional and build value using little endian notation.

Another example: value `20563` has representation as `0101 0000, 0101 0011` in binary format. Greatest significant bit
is 15, so 3 bytes with 7 effective bits are required. Value will be stored as 3 bytes:
```
1st byte   2nd byte   3rd byte
0000 0000, 0101 0000, 0101 0011 - original value
1100 0000, 0101 0000, 0101 0011 - bytes in stream
|/^\____/  \_______/  \_ Third byte of data bits, little endian 
^ |  ^      Second byte of data bits, little endian 
| |  \_ First byte of data bits, little endian
| \_ Fist 0 bit in first byte indicates ending of record length block 
\_ Amount of 1s indicated number of additional 2 bytes in stream
```

Special cases:
* As Long could not be longer than 64 effective bits or 8 bytes, there is no need to support longer values.
  So, if first byte contains only 1s (`1111 1111`) there is no need to put tailing 0, it's already known that value requires
  8 additional bytes and it's enough to store any Long value to these additional 8 bytes. So, record length can't exceed 9 
  bytes.
* Small values with 7 effective bits or less required 1 byte. Representation of these values in stream is the same as
  binary representation of lvalue itself. The exception is value `127` which is reserved for `null`, so this value requires 2 
  bytes in stream.
* `Null` is stored as single byte `127` or `0111 1111` in binary format.
* Value `127` can't be saved as single byte in 7 bit representation as it's reserved for `null`. So, it'll be stored in 2
  bytes: `1000 0000, 0111 1111`.
  
### Signed Integers and Longs

Signed Integers are stored mostly the same way as unsigned, but effective bit should be calculated differently.

Lets see an example of both positive and negative values `3251` and `-3251` (leading 6 similar bytes are grouped):
```
6 x 0000 0000, 0000 1100, 1011 0011 - binary representation of 3251
6 x 1111 1111, 1111 0011, 0100 1101 - binary representation of -3251
    ^             ^ ^
    |             | \_ First bit not equals to sign bit
    |             \_ Greatest significant bit
    \_ Sign bit (first bit in little endian)
```
In this case greatest significant bit 13, which means that 2 bytes in stream are required:
```
1st byte   2nd byte
1000 1100, 1011 0011 - bytes in stream for 3251
1011 0011, 0100 1101 - bytes in stream for -3251
^^\_____/  \_______/
||   ^      Second byte of data bits, little endian 
||   \_ First byte of data bits, little endian
|\_ Fist 0 bit in first byte indicates ending of record length block 
\_ Amount of 1s indicated number of additional 1 byte in stream
```
Keep in mind leading 0s (for positive value) and 1s (for negative value) in data bits. Greatest data bit should be copied to
all greater data bits (to the left in binary representation) during deserialization.

Another difference to Unsigned representation is storing `null` values. It's stored as smallest negative value which could
be written in single byte representation. In current case it's `-64`, so `null` is stored as `0100 0000` while `-64` requires
2 bytes: `1011 1111, 1100 0000`.