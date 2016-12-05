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

## Sample code

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

## List of serializers

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

## Caching

If the same object is serialized several times it may worth to use serializer with caching suppurt. In this case when object is serialized for the first time it will be serialized only once and added to cache list, when it's aimed to be serialized for the next time then only cache id will be written instead of serializing whole oblect. Good candidates for caching are Strings, enums and immutable objects.

There are two basic caching algorithms.

### Simple caching

Add tail caching strategy. When element is added to cache, it's added to the tail of the list. So, index of cached object remains the same until cache size limit is reached and object is pushed out by another object. This cache algorithm could be good for a small amount of different objects.

Here is an example of using this cache:
```
// Initial cache is empty, max size is 3.
// Cahce: []
$ push "AAA"
>> id in cache: 0 // new object
// Cache: ["AAA"]
$ push "BBB"
>> id in cache: 0 // new object
// Cache: ["AAA", "BBB"]
$ push "AAA"
>> id in cache: 1 // first element in cahce
// Cache: ["AAA", "BBB"]
$ push "CCC"
>> id in cache: 0 // new object
// Cache: ["AAA", "BBB", "CCC"], max sise is reached
$ push "DDD"
>> id in cache: 0 // new object
// Cache: ["AAA", "DDD", "CCC"], "BBB" is pushed out because it was the least recently used
$ push "AAA"
>> id in cache: 1 // first element in cahce
// Cache: ["AAA", "DDD", "CCC"]
```

This cahce algorithm is used in the following serializers: `StringConstantsSerializationFactory`, `EnumsConstantsSerializationFactory`

### Latest first caching

When new element is added, it's added to the head of a list. When object is used and it's already in the cache list, it's moved th the head. This approach keeps recent and most often used objects near the head of the list. This cache algorithm could be good in case of mixing up oftely used object and objects used only once.

Here is an example of using this cache:
```
// Initial cache is empty, max size is 3.
// Cahce: []
$ push "AAA"
>> id in cache: 0 // new object
// Cache: ["AAA"]
$ push "BBB"
>> id in cache: 0 // new object
// Cache: ["BBB", "AAA"]
$ push "AAA"
>> id in cache: 2 // second element in cahce, used object is moved to the head
// Cache: ["AAA", "BBB"]
$ push "CCC"
>> id in cache: 0 // new object
// Cache: ["CCC", "AAA", "BBB"], max sise is reached
$ push "DDD"
>> id in cache: 0 // new object
// Cache: ["DDD", "CCC", "AAA"], "BBB" is pushed out because it was the least recently used
$ push "AAA"
>> id in cache: 3 // second element in cahce, used object is moved to the head
// Cache: ["AAA", "DDD", "CCC"]
```

This cache algorithm is used in the following serializers: `StringCachedSerializationFactory`, `EnumsSerializationFactory`, `UnknownTypeCachedSerializationFactory`


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
  
### Variable-length Integers and Longs

All Integer values are saved as 64-bits Longs in variable-length format. Signed and unsigned values are slightly different, 
but the main idea is the same. Smaller values requires less space, 7 effective bits per byte in average.
Another feature of this format is that values are null-friendly, so any value could be saved as null and this will require
only 1 byte in stream.

#### Unsigned Integers and Longs

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
  
#### Signed Integers and Longs

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

### Strings

Format of strings are pretty simple: string is converted into byte array using UTF8 encoding. Then array length is written
as variable-length unsigned int. Then bytes from array are written.

## Data format

Data stream consists of stream header and serialized data.

### Stream header

Currently header consists of 2 bytes:
```
UNSIGNED_INT - version in unsigned int format, currently always 0
UNSIGNED_INT - number settings, not supported yet, currently 0
[optionally] - repeat for each setting, not supported yet
  [settings] - not supported yet
[optionally/]
```

### Stream data

When an object is serialized, the following data is written:

```
UNSIGNED_INT - Serializer id. Registered serializer ids are started from 1. 0 means serializer in not registered yet,
               create it and register (assign an id). Null means serializer in not registered, create temoprary serializer
               but don't register it (don't assign an id to it).
[optionally] - Write serializer's parameters if serializer is not registered.
  STRING - Serializer name. Actually name is a short type of serializer.
  [serializer settings] - Settings for specified serializer. Some serializers may not need any settings, another may require
                          registering nested serializers.
[optionally/]
[serialized data] - Object data, specific for specified serializer.
```

Usually when a new object of unknown type is serialized, new serializer is created and stored in serializers list 
(id assigned). Data written: id (0 for new serializer) + serializer name + serializer settings + object data.
When another object of the same type is serialized, previously created serializer is reused, no need to
write serializer name and it's settings. Data written: id (>= 1 for registered serializers) + object data 

## Serializers

Here is a list of existing serializers. Each serializer description consists of 3 points:
* Serializer short name: Identifier of serializer type, short String which identify how stream was serialized (and how to 
  deserialize it). Standard serializer names are started witch underscore character '_'.
* Serializer settings: Setting of serializer, written once when serializer is created.
* Write data: Data format description, written on each serialization.

### Numbers

All number serializers writes original data type, so it can be deserialized to proper type. Number data types are String 
constants:
* `64`: Long, 64 bits signed integer.
* `32`: Integer, 32 bits signed integer.
* `64f`: Double, 64 bits float.
* `32f`: Float, 32 bits float.
Other types (Byte, Char) are not supported yet.

Precision is a signed int value, indicates fractional part length in decimal digits. Precision could be negative, it this
case value will be downscaled before serializing. Precision works as prescaling with the following pseudocode:
```java
outputValue = round(inputValue * pow(10, precision)) 
```

Here is a list number of serializers:
* `NumberSerializationFactory`
  * Serializer short name: `_N`.
  * Serializer settings: (Number data type): String, precision: SignedInt.
  * Write data: value: SignedInt.
* `UnsignedLongSerializationFactory`
  * Serializer short name: `_UL`.
  * Serializer settings: (Number data type): String, precision: SignedInt.
  * Write data: value: UnsignedInt. 
* `NumberDiffSerializationFactory`
  * Serializer short name: `_ND`.
  * Serializer settings: (Number data type): String, precision: SignedInt.
  * Write data: (value - prevValue): SignedInt.
    Initially: prevValue = 0, updated after each data write.
* `NumberDiffNRSerializationFactory` is fully compatible with `NumberDiffSerializationFactory`.
* `NumberIncrementalSerializationFactory`
  * Serializer short name: `_NI`.
  * Serializer settings: (Number data type): String, precision: SignedInt.
  * Write data: (value - prevValue): UnsignedInt.
    Initially: prevValue = 0, updated after each data write.
* `NumberLinearSerializationFactory`
  * Serializer short name: `_NL`.
  * Serializer settings: (Number data type): String, precision: SignedInt.
  * Write data: (value - (prevValue * 2 - prevPrevValue)): SignedInt.
    Initially prevValue = prevPrevValue = 0.
    On first serialization: prevValue = prevPrevValue = value.
    On later serializations: prevPrevValue = prevValue, prevValue = value.
* `NumberMedianSerializationFactory`
  * Serializer short name: `_NM`.
  * Serializer settings: (Number data type): String, precision: SignedInt, (median length): UnsignedInt.
  * Write data: (value - prevValue + median(diffs)): SignedInt.
    Initially: prevValue = 0, diffs is array of zeros of length (median length).
    On first serialization: prevValue = value.
    On later serializations: diffs\[(i++) % diffs.length\] = (value - prevValue), prevValue = value.

### Strings

There are cached and non-cached serializers for Strings. Both of them are using the following underlying mechanism of 
serializing Strings: string in encoded to UTF8 byte array, then array length and array itself are written. Array length
is written as variable-length UnsignedInt. When `null` is serialized then null is written instead of array length and array 
body is omit.  

In cached serializers index in cache is written prior to serializing string data. Index in cache could be:
* `null`: value is `null`, no need to serialize value.
* `0`: String is not in cache, value is serialized and added to cache.
* `>= 1`: String is already in cache, no need to serialize value.

Here is a list of String serializers:

* `StringSerializationFactory` - non-cached serializer
  * Serializer short name: `_S`.
  * Serializer settings: nothing.
  * Write data: (UTF8 byte array length): UnsignedInt, (byte array): Byte[].
* `StringCachedSerializationFactory` - cached serializer, use Latest first caching.
  * Serializer short name: `_SC`.
  * Serializer settings: (cache size): UnsignedInt.
  * Write data: (index in cache): UnsignedInt, optionally: (UTF8 byte array length): UnsignedInt, (byte array): Byte[].
* `StringConstantsSerializationFactory` - cached serializer, use Simple Caching.
  * Serializer short name: `_SF`.
  * Serializer settings: (cache size): UnsignedInt.
  * Write data: (index in cache): UnsignedInt, optionally: (UTF8 byte array length): UnsignedInt, (byte array): Byte[].
    
[TODO describe settings and data formats for serializers] 

## Example of binary format

Here is a step by step serialization and description which data is written.

```java
ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
ObjectWriter serializer = new SerialDataWriter(byteStream);
```
Empty DataWriter is created, header is written, 2 bytes:
```
0x00 - DataWriter version, UNSIGNED_INT, currently 0. 
0x00 - Number of serrings, UNSIGNED_INT, currently 0.
```

Let's write some data:
```java
serializer.writeObject(new VehicleStatus.GpsPosition(0.000010, 0.000020));
```
A lot of data for the first time, 47 bytes:
```
0x00 - Serializer id = 0, create serializer and assing an id = 1 for it.
// Register new serializer:
  // Write serializer name:
  0x02 - Name length, UNSIGNED_INT, 2.
  0x5f, 0x4f - Serializer name, "_O" - ObjectSerializationFactory.
  // Write serializer settings, specific for ObjectSerializationFactory:
  // Write class name:
  0x0b - Class name length, 11. Actually it should be full class name with package, we've simplified it.
  0x47, 0x70, 0x73, 0x50, 0x6f, 0x73, 0x69, 0x74, 0x69, 0x6f, 0x6e - Class name, "GpsPosition".
  // Write fields
  0x02 - Number of fields in class, 2.
  // Write settings for field1:
    // Write name of field:
    0x03 - Field name length, 3.
    0x6c, 0x61, 0x74 - Field name, "lat".
    // Write serializer for field1:
    0x7f - SerializerId, UNSIGNED_INT, null. Create new id-less serializer.
    // Create new serializer:
      // Write serializer name:
      0x03 - Name length, UNSIGNED_INT, 3.
      0x5f, 0x4e, 0x4c - Serializer name, "_NL" - NumberLinearSerializationFactory.
      // Write serializer settings, specific for NumberLinearSerializationFactory.
      0x03 - Length of original data type.
      0x36, 0x34, 0x66 - Original data type, "64f" - Double data type.
      0x06 - Prescale, number of decimal digits in fraction, 6.
  // Write settings for field2:
    // Anything is exactly the same as in field 1, except of field name "lon".
    0x03, 0x6c, 0x6f, 0x6e, 0x7f, 0x03, 0x5f, 0x4e, 0x4c, 0x03, 0x36, 0x34, 0x66, 0x06 
// Finally, write data itself:    
0x0a - Object serializer passes field1 (lat) value to field1 serializer and it writes value (10 = 0.000010 * 10 ^ 6).
0x14 - Object serializer passes field2 (lon) value to field1 serializer and it writes value (20 = 0.000010 * 20 ^ 6) .
```
Let's write data of the same type again:
```java
serializer.writeObject(new VehicleStatus.GpsPosition(0.000012, 0.000025));
```
This time much less data will be written as corresponding serializer already created and registered, only 3 bytes are written:
```
0x01 - Serializer id = 1, get serializer from registered list and pass value to it.
// Write data itself:    
0x02 - Pass field1 (lat) value to field1 serializer (2 = 0.000012 * 10 ^ 6 - 10) (prevValue = 10).
0x05 - Pass field2 (lon) value to field1 serializer (5 = 0.000025 * 10 ^ 6 - 20) (prevValue = 20).
```
As we have `NumberLinearSerializationFactory` for lat and lon, next point should be predicted as (0.000014, 0.000030) and 
only difference written. Lets check 
```java
serializer.writeObject(new VehicleStatus.GpsPosition(0.000015, 0.000028));
```
This time much less data will be written as corresponding serializer already created and registered, only 3 bytes are written:
```
0x01 - Serializer id = 1, get serializer from registered list and pass value to it.
// Write data itself:    
0x01 - Difference to predicted lat, (2 = 0.000015 * 10 ^ 6 - 14) (predicted value = 14).
0x7e - Difference to predicted lon, (-2 = 0.000028 * 10 ^ 6 - 30) (predicted value = 30).
```

When unknown type is serialized, again, new serializer should be created:
```java
serializer.writeObject(new VehicleStatus(
        new VehicleStatus.GpsPosition(0.000015, 0.000020),
        20, 1000000L, "BestFm"));
```
As type VehicleStatus is much more complex than GpsPosition, more data will be written, 143 bytes:
```
0x00 - Serializer id = 0, create serializer and assing an id = 2 for it.
// Register new serializer:
  // Write serializer name:
  0x02, 0x5f, 0x4f - Serializer length & name, 2 + "_O" - ObjectSerializationFactory.
  // Write serializer settings, specific for ObjectSerializationFactory:
  // Write class name:
  0x0d, 0x56, 0x65, 0x68, 0x69, 0x63, 0x6c, 0x65, 0x53, 0x74, 0x61, 0x74, 0x75, 0x73, - Class name 13 + "VehicleStatus".
  // Write fields
  0x04 - Number of fields in class, 4.
  // Write settings for field1:
    0x09, 0x74, 0x69, 0x6d, 0x65, 0x73, 0x74, 0x61, 0x6d, 0x70 - field1 name, 9 + "timestamp" .
    0x7f - SerializerId, UNSIGNED_INT, null. Create new id-less serializer.
    // Create new serializer:
      // Write serializer name:
      0x03, 0x5f, 0x4e, 0x44 - 3 + "_ND", NumberDiffSerializationFactory .
      // Write serializer settings, specific for NumberDiffSerializationFactory.
      0x02, 0x36, 0x34 - Original data type, 2 + "64" - Long data type.
      0x7d - Prescale, -3.
  // Write settings for field2:
    0x05, 0x73, 0x70, 0x65, 0x65, 0x64 - field2 name, 5 + "speed" .
    0x7f - SerializerId, UNSIGNED_INT, null. Create new id-less serializer.
    // Create new serializer:
      // Write serializer name:
      0x03, 0x5f, 0x4e, 0x44 - 3 + "_ND", NumberDiffSerializationFactory .
      // Write serializer settings, specific for NumberDiffSerializationFactory.
      0x03, 0x36, 0x34, 0x66 - Original data type, 3 + "64f" - Double data type.
      0x01 - Prescale, 1.
  // Write settings for field3:
    00x0c, 0x72, 0x61, 0x64, 0x69, 0x6f, 0x53, 0x74, 0x61, 0x74, 0x69, 0x6f, 0x6e - field3 name, 12 + "radioStation" .
    0x7f - SerializerId, UNSIGNED_INT, null. Create new id-less serializer.
    // Create new serializer:
      // Write serializer name:
      0x03, 0x5f, 0x53, 0x43 - 3 + "_SC", StringCachedSerializationFactory .
      // Write serializer settings, specific for StringCachedSerializationFactory.
      0x14 - Cache size, 20 
  // Write settings for field4:
    0x0b, 0x67, 0x70, 0x73, 0x50, 0x6f, 0x73, 0x69, 0x74, 0x69, 0x6f, 0x6e - field4 name, 11 + "gpsPosition" .
    0x7f - SerializerId, null. Create new serializer. Here could be 1 to re-use serializer, but new one is created.
    // Create new serializer:
      // Exactly the same data part as for GpsPosition class.
      0x02, 0x5f, 0x4f - "_O".
      0x0b, 0x47, 0x70, 0x73, 0x50, 0x6f, 0x73, 0x69, 0x74, 0x69, 0x6f, 0x6e - "GpsPosition".
      0x02 - 2 fields.
        0x03, 0x6c, 0x61, 0x74 - "lat".
        0x7f - new serializer.
          0x03, 0x5f, 0x4e, 0x4c - "_NL".
          0x03, 0x36, 0x34, 0x66 - "64f". 
          0x06 - Precision, 3.
        0x03, 0x6c, 0x6f, 0x6e - "lon".
        0x7f - new serializer.
          0x03, 0x5f, 0x4e, 0x4c - "_NL".
          0x03, 0x36, 0x34, 0x66 - "64f"
          0x06 - Precision, 3.
// Write data itself:
// timestamp
0x83, 0xe8 - 1000000 with prescale -3 = 1000, SINGED_INT
// speed
0x80, 0xc8 - 20 with prescale 1 = 200, SINGED_INT
// radioStation
0x00 - id in cache, 0, create new value and put in cache
0x06, 0x42, 0x65, 0x73, 0x74, 0x46, 0x6d - radioStation value, 6 + "BestFm"
// gpsPosition
  0x0f - lat
  0x14 - lon
```
Again, writing object of known type gives much less data output:
```java
serializer.writeObject(new VehicleStatus(
        new VehicleStatus.GpsPosition(0.000018, 0.000025),
        20, 1010000L, "BestFm"));
```
In this case output is 6 bytes only:
```
0x02 - Serializer id = 2, get serializer from registered list and pass value to it.
// Write data itself:
// timestamp
0x0a - (1010000 - 1000000) with prescale -3 = 10, SINGED_INT
// speed
0x00 - no spped changes, SINGED_INT
// radioStation
0x01 - id in cache, under this index value "BestFm" is cached
// gpsPosition
  0x03 - diff to lat
  0x05 - diff to lon
```