package me.mathyj.object;

import java.util.Objects;

public record StringObject(String value) implements Object {

    public static StringObject valueOf(String val) {
        return new StringObject(val);
    }

    @Override
    public ObjectType type() {
        return ObjectType.STRING;
    }


    @Override
    public String toString() {
        return value();
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringObject that = (StringObject) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
