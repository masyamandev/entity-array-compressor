package com.masyaman.datapack.randombeans.objects;

import com.masyaman.datapack.annotations.serialization.CacheSize;
import com.masyaman.datapack.annotations.serialization.SerializeBy;
import com.masyaman.datapack.annotations.serialization.SerializeValueBy;
import com.masyaman.datapack.serializers.objects.UnknownTypeCachedSerializationFactory;
import com.masyaman.datapack.serializers.objects.UnknownTypeSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringCachedSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringConstantsSerializationFactory;
import com.masyaman.datapack.serializers.strings.StringSerializationFactory;

import java.util.List;

public class BeanWithStrings {

    public String string;

    @SerializeBy(StringSerializationFactory.class)
    public String stringSimple;
    @SerializeBy(StringCachedSerializationFactory.class)
    public String stringCached;
    @SerializeBy(StringConstantsSerializationFactory.class)
    public String stringConstant;

    @SerializeValueBy(StringSerializationFactory.class)
    public List<String> stringSimpleList;
    @SerializeValueBy(StringCachedSerializationFactory.class)
    public List<String> stringCachedList;
    @SerializeValueBy(StringConstantsSerializationFactory.class)
    public List<String> stringConstantList;

    @CacheSize(4)
    @SerializeValueBy(StringCachedSerializationFactory.class)
    public List<String> stringCachedSmallCacheList;
    @CacheSize(4)
    @SerializeValueBy(StringConstantsSerializationFactory.class)
    public List<String> stringConstantSmallCacheList;

    @SerializeValueBy(UnknownTypeSerializationFactory.class)
    public List<String> unknownList;
    @CacheSize(4)
    @SerializeValueBy(UnknownTypeCachedSerializationFactory.class)
    public List<String> unknownCachedList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanWithStrings that = (BeanWithStrings) o;

        if (string != null ? !string.equals(that.string) : that.string != null) return false;
        if (stringSimple != null ? !stringSimple.equals(that.stringSimple) : that.stringSimple != null) return false;
        if (stringCached != null ? !stringCached.equals(that.stringCached) : that.stringCached != null) return false;
        if (stringConstant != null ? !stringConstant.equals(that.stringConstant) : that.stringConstant != null)
            return false;
        if (stringSimpleList != null ? !stringSimpleList.equals(that.stringSimpleList) : that.stringSimpleList != null)
            return false;
        if (stringCachedList != null ? !stringCachedList.equals(that.stringCachedList) : that.stringCachedList != null)
            return false;
        if (stringConstantList != null ? !stringConstantList.equals(that.stringConstantList) : that.stringConstantList != null)
            return false;
        if (stringCachedSmallCacheList != null ? !stringCachedSmallCacheList.equals(that.stringCachedSmallCacheList) : that.stringCachedSmallCacheList != null)
            return false;
        if (stringConstantSmallCacheList != null ? !stringConstantSmallCacheList.equals(that.stringConstantSmallCacheList) : that.stringConstantSmallCacheList != null)
            return false;
        if (unknownList != null ? !unknownList.equals(that.unknownList) : that.unknownList != null) return false;
        if (unknownCachedList != null ? !unknownCachedList.equals(that.unknownCachedList) : that.unknownCachedList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = string != null ? string.hashCode() : 0;
        result = 31 * result + (stringSimple != null ? stringSimple.hashCode() : 0);
        result = 31 * result + (stringCached != null ? stringCached.hashCode() : 0);
        result = 31 * result + (stringConstant != null ? stringConstant.hashCode() : 0);
        result = 31 * result + (stringSimpleList != null ? stringSimpleList.hashCode() : 0);
        result = 31 * result + (stringCachedList != null ? stringCachedList.hashCode() : 0);
        result = 31 * result + (stringConstantList != null ? stringConstantList.hashCode() : 0);
        result = 31 * result + (stringCachedSmallCacheList != null ? stringCachedSmallCacheList.hashCode() : 0);
        result = 31 * result + (stringConstantSmallCacheList != null ? stringConstantSmallCacheList.hashCode() : 0);
        result = 31 * result + (unknownList != null ? unknownList.hashCode() : 0);
        result = 31 * result + (unknownCachedList != null ? unknownCachedList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BeanWithStrings{" +
                "string='" + string + '\'' +
                ", stringSimple='" + stringSimple + '\'' +
                ", stringCached='" + stringCached + '\'' +
                ", stringConstant='" + stringConstant + '\'' +
                ", stringSimpleList=" + stringSimpleList +
                ", stringCachedList=" + stringCachedList +
                ", stringConstantList=" + stringConstantList +
                ", stringCachedSmallCacheList=" + stringCachedSmallCacheList +
                ", stringConstantSmallCacheList=" + stringConstantSmallCacheList +
                ", unknownList=" + unknownList +
                ", unknownCachedList=" + unknownCachedList +
                '}';
    }
}
