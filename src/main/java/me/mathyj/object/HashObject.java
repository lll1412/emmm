package me.mathyj.object;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class HashObject implements Object {
    private final LinkedHashMap<Object, Object> hash;

    public HashObject(LinkedHashMap<Object, Object> hash) {
        this.hash = hash;
    }

    public HashObject() {
        this.hash = new LinkedHashMap<>();
    }

    @Override
    public ObjectType type() {
        return ObjectType.HASH;
    }

    @Override
    public String value() {
        var str = hash == null ? "" : hash.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(", "));
        return "{%s}".formatted(str);
    }

    public Object get(Object indexObj) {
        return hash.getOrDefault(indexObj, Object.NULL);
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashObject that = (HashObject) o;
        return Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

    @Override
    public String toString() {
        return hash.toString();
    }
}
