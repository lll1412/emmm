package me.mathyj.object;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ArrayObject implements Object {
    private final List<Object> elements;

    public ArrayObject(List<Object> elements) {
        this.elements = elements;
    }

    public ArrayObject(Object... elements) {
        this.elements = Arrays.asList(elements);
    }

    @Override
    public ObjectType type() {
        return ObjectType.ARRAY;
    }

    @Override
    public String value() {
        return elements.toString();
    }

    public Object get(int i) {
        if (i < 0 || elements == null || i >= elements.size()) return Object.NULL;
        return elements.get(i);
    }

    public int size() {
        return elements == null ? 0 : elements.size();
    }

    public Object add(Object arg) {
        elements.add(arg);
        return this;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayObject that = (ArrayObject) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}
