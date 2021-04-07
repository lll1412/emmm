package me.mathyj.object;

import java.util.Objects;

public record ReturnObject(Object retObj) implements Object {
    @Override
    public ObjectType type() {
        return ObjectType.RETURN;
    }

    @Override
    public String value() {
        return retObj.value();
    }

    @Override
    public String toString() {
        return retObj.toString();
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnObject that = (ReturnObject) o;
        return Objects.equals(retObj, that.retObj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retObj);
    }
}
