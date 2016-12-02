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

Also there is a console tool which can convert a stream to json (in this example tool requires gzipped stream):
```sh
$ java -jar entity-array-compressor.jar -input=gzip < inputfile.gz > outputfile.json
```

# Optimize serializers

Depending on nature of data some assumptions could be made to make better compression. For example GPS position usually
changed slowly and next point can be predicted using two previous points. Some data could be rounded, so it's not necessary
to save all information. Such hints could be done with annotations.

In the following example we periodically (e.g. each second) save vehicle position, speed and radio station. We can specify
serializer and precision to save space and get better compression:
```java
public class VehicleStatus {

    // Use serializer for specified type. This field will be serialized strictly as GpsPosition object, inheritance
    // will not be allowed. If @SerializeBy annotation is omit, then serializer UnknownTypeSerializationFactory
    // will be used which could increase space, but allows to store inherited objects (e.g. GpsPositionWithAltitude).
    @SerializeBy(ObjectSerializationFactory.class)
    private GpsPosition gpsPosition;

    // By default NumberDiffSerializationFactory is used, difference to previous value will be stored.
    // Store 1 decimal digit in fraction. Default rounding mode is HALF_UP.
    @Precision(1)
    private double speed;

    // By default NumberDiffSerializationFactory is used, difference to previous value will be stored.
    // Precision can be negative, in this case timestamp will be serialized as seconds instead of millis.
    @Precision(value = -3, roundingMode = RoundingMode.FLOOR)
    private long timestamp;

    // Assuming radioStation will not be switched too often, so we can cache recent values and write only ids from cache.
    @CacheSize(20)
    @SerializeBy(StringCachedSerializationFactory.class)
    private String radioStation;


    public static class GpsPosition {

        // Use linear prediction based on 2 previous values. Default precision is 6 decimal digits.
        @SerializeBy(NumberLinearSerializationFactory.class)
        private double lat;

        // Use linear prediction based on 2 previous values. Default precision is 6 decimal digits.
        @SerializeBy(NumberLinearSerializationFactory.class)
        private double lon;

    }
}
```
All serialization settings are stored in data stream, so they are not required during deserialization. Actually the whole 
class may not be required during deserialization, data stream can be converted into json.

Here is a list of available serialization factories in package `com.masyaman.datapack.serializers`. All serializers are
null-friendly.

* `numbers` - Stores singed or unsigned Long. Values are stored as fixed-points Longs. Doubles are prescaled to Longs with 
  specified precisions (by default 6 decimal digits).
  * `NumberDiffSerializationFactory` - Default serializer for all numbers. During serialization it saves difference to 
    previous value. This gives result close to 0 on small value changes.
  * `NumberDiffNRSerializationFactory` - Same as `NumberDiffSerializationFactory` but has non-precise rounding, can give 
    slightly better compression on noisy data.  
  * `NumberIncrementalSerializationFactory` - Same as `NumberDiffSerializationFactory` for unsigned incrementing values.
  * `NumberLinearSerializationFactory` - Makes linear prediction on 2 recent values and saves difference to prediction.
  * `NumberMedianSerializationFactory` - Similar to `NumberLinearSerializationFactory`, but prediction is a previous value +
    median of 3 recent value increments. 
  * `NumberSerializationFactory` - Very basic serializer for signed values: store value as is using variable-length encoding.
  * `UnsignedLongSerializationFactory` - Very basic serializer for unsigned values: store value as is using variable-length 
    encoding.
* `dates` - Special serializer for dates. Internally uses Number serializers, the only difference is precision. Stores
  timestamp in millis prescaled using scales form `DatePrecisions`.
  * `DateDiffSerializationFactory`
  * `DateIncrementalSerializationFactory`
  * `DateLinearSerializationFactory`
  * `DateMedianSerializationFactory`
  * `DateSerializationFactory`
* `strings` - Serializers for Strings in UTF8.
  * `StringSerializationFactory` - Serialize String as is: UTF8 byte array length (unsigned long) + UTF8 bytes.
  * `StringCachedSerializationFactory` - Cached strings using latest first cache strategy (least recently used values have
    smaller indices in cache). Could be used for storing big variety of strings, some of which are occurred many times, but 
    most of them occurred only once. Cache size could be limited by `CacheSize` annotation.
  * `StringConstantsSerializationFactory` - Cached strings using add last cache strategy (least recently added values have
    bigger indices in cache, once value is added to cache, it's index remains the same). Could be better for storing small 
    amount of unique strings.
* `enums` - Serializers for Enums. Enums are saved as cached Strings using one of two caching strategies.
  * `EnumsSerializationFactory`
  * `EnumsConstantsSerializationFactory`
* `collections` - Serializers for collections and arrays. Fields with collections could be marked with `AllowReordering`
  annotation to specify if it's elements can be mixed up for better compression. Reordering requires caching of recently 
  used elements, so it may be not good idea to use reordering on mutable objects. 
  * `CollectionSerializationFactory` - Serialize collections and arrays. To specify serializer for values (not to collection 
    itself) annotation `SerializeValueBy` can be used.
  * `MapSerializationFactory` - Serialize maps. To specify serializer for key or values annotations `SerializeKeyBy` or 
    `SerializeValueBy` can be used.
  * `BitSetSerializationFactory` - Experimental serializer for `BitSet`.
* `objects` - Serializers for custom objects.
  * `ObjectSerializationFactory` - Serializer for objects of specific type.
  * `UnknownTypeSerializationFactory` - Serializer for objects of unknown type. During serialization new serializer will be
    created for each new type.
  * `UnknownTypeCachedSerializationFactory` - Serializer for objects of unknown type. Serialized objects are cached using
    latest first cache strategy. This serializer should not be used with mutable objects. Also it's worth to limit cache
    size with `CacheSize` annotation.



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