package me.mathyj.object;

import java.util.Objects;

public class BooleanObject implements Object {
    public static final BooleanObject TRUE = new BooleanObject(true);
    public static final BooleanObject FALSE = new BooleanObject(false);
    public final boolean value;
    private final String valueStr;

    private BooleanObject(boolean value) {
        this.value = value;
        this.valueStr = String.valueOf(value);
    }

    public static BooleanObject valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BOOLEAN;
    }

    @Override
    public String value() {
        return valueStr;
    }

    @Override
    public String toString() {
        return value();
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanObject that = (BooleanObject) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
